package passwordevaluator.ui;

import passwordevaluator.core.StrengthCalculator;
import passwordevaluator.core.StrengthResult;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量检测结果对话框
 * 读取文件中的密码列表，逐条评测并展示汇总结果
 */
public class BatchResultDialog extends JDialog {

    private final StrengthCalculator calculator;
    private final File sourceFile;

    public BatchResultDialog(JFrame parent, StrengthCalculator calculator, File sourceFile) {
        super(parent, "批量检测结果", true);
        this.calculator = calculator;
        this.sourceFile = sourceFile;

        setSize(650, 500);
        setLocationRelativeTo(parent);
        initUI();
        runBatchCheck();
    }

    private void initUI() {
        setLayout(new BorderLayout());
    }

    private void runBatchCheck() {
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        resultArea.setBackground(new Color(250, 250, 250));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);

        // 统计信息面板
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statsLabel = new JLabel("正在检测...");
        statsLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        statsPanel.add(statsLabel);
        add(statsPanel, BorderLayout.SOUTH);

        // 在后台线程执行检测（避免阻塞UI）
        new Thread(() -> {
            List<String> passwords = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(sourceFile), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        passwords.add(line);
                    }
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                    resultArea.setText("读取文件失败: " + e.getMessage()));
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("文件: ").append(sourceFile.getName()).append("\n");
            sb.append("共检测 ").append(passwords.size()).append(" 条密码\n");
            sb.append("=" .repeat(60)).append("\n\n");

            int veryWeakCount = 0, weakCount = 0, mediumCount = 0;
            int strongCount = 0, veryStrongCount = 0;

            for (int i = 0; i < passwords.size(); i++) {
                String pwd = passwords.get(i);
                StrengthResult result = calculator.evaluate(pwd);

                sb.append(String.format("[%3d] ", i + 1));
                sb.append(maskPassword(pwd));
                sb.append("  →  ");
                sb.append(String.format("%3d分", result.getTotalScore()));
                sb.append("  [").append(result.getLevel()).append("]");
                sb.append("\n");

                switch (result.getTotalScore() / 20) {
                    case 0: veryWeakCount++; break;
                    case 1: weakCount++; break;
                    case 2: mediumCount++; break;
                    case 3: strongCount++; break;
                    default: veryStrongCount++; break;
                }
            }

            sb.append("\n").append("=" .repeat(60)).append("\n");
            sb.append("📊 统计汇总:\n");
            sb.append("  非常弱 (0-20):  ").append(veryWeakCount).append(" 条\n");
            sb.append("  弱     (21-40): ").append(weakCount).append(" 条\n");
            sb.append("  中等   (41-60): ").append(mediumCount).append(" 条\n");
            sb.append("  强     (61-80): ").append(strongCount).append(" 条\n");
            sb.append("  非常强 (81-100):").append(veryStrongCount).append(" 条\n");

            String statsText = String.format(
                "检测完成 | 总计 %d 条 | 弱口令占比: %.1f%%",
                passwords.size(),
                (veryWeakCount + weakCount) * 100.0 / Math.max(1, passwords.size())
            );

            final String finalResult = sb.toString();
            final String finalStats = statsText;

            SwingUtilities.invokeLater(() -> {
                resultArea.setText(finalResult);
                resultArea.setCaretPosition(0);
                statsLabel.setText(finalStats);
            });
        }).start();
    }

    /** 对密码进行脱敏显示（仅显示前2位和长度） */
    private String maskPassword(String password) {
        if (password.length() <= 2) {
            return password + "**";
        }
        return password.substring(0, 2) + "***" + " (长度:" + password.length() + ")";
    }
}