package passwordevaluator.ui;

import passwordevaluator.core.StrengthCalculator;
import passwordevaluator.core.StrengthResult;
import passwordevaluator.loader.WeakPasswordLoader;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.Map;

/**
 * 主界面窗口
 * 提供密码输入、实时评测、规则详情展示、批量检测等功能
 */
public class MainFrame extends JFrame {

    private final StrengthCalculator calculator;
    private final WeakPasswordLoader loader;

    // UI 组件
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JProgressBar strengthBar;
    private JLabel scoreLabel;
    private JLabel levelLabel;
    private JPanel rulesPanel;
    private JTextArea suggestionArea;
    private JButton batchButton;
    private JLabel dictStatusLabel;

    // 颜色常量
    private static final Color BG_COLOR = new Color(245, 247, 250);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color TITLE_COLOR = new Color(40, 55, 71);
    private static final Color VERY_WEAK_COLOR = new Color(231, 76, 60);
    private static final Color WEAK_COLOR = new Color(230, 126, 34);
    private static final Color MEDIUM_COLOR = new Color(241, 196, 15);
    private static final Color STRONG_COLOR = new Color(46, 204, 113);
    private static final Color VERY_STRONG_COLOR = new Color(39, 174, 96);

    public MainFrame(WeakPasswordLoader loader) {
        this.loader = loader;
        this.calculator = StrengthCalculator.createDefault(loader);

        initUI();
        setTitle("🔐 多策略密码强度评测器");
        setSize(700, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示
        setResizable(false);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // ==== 标题区域 ====
        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // ==== 密码输入区域 ====
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // ==== 评分展示区域 ====
        JPanel scorePanel = createScorePanel();
        mainPanel.add(scorePanel);
        mainPanel.add(Box.createVerticalStrut(12));

        // ==== 规则详情区域 ====
        JPanel rulesDetailPanel = createRulesDetailPanel();
        mainPanel.add(rulesDetailPanel);
        mainPanel.add(Box.createVerticalStrut(12));

        // ==== 改进建议区域 ====
        JPanel suggestionPanel = createSuggestionPanel();
        mainPanel.add(suggestionPanel);
        mainPanel.add(Box.createVerticalStrut(12));

        // ==== 底部按钮 ====
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel);

        // 放入滚动面板
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        setContentPane(scrollPane);

        // 初始状态
        updateResult(null);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setMaximumSize(new Dimension(650, 50));

        JLabel titleLabel = new JLabel("🔐 多策略密码强度评测器");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setForeground(TITLE_COLOR);
        panel.add(titleLabel, BorderLayout.WEST);

        dictStatusLabel = new JLabel("字典: " + loader.getCount() + " 条");
        dictStatusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        dictStatusLabel.setForeground(new Color(100, 100, 100));
        panel.add(dictStatusLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(createRoundedBorder("密码输入"));
        panel.setMaximumSize(new Dimension(650, 100));

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        inputRow.setBackground(PANEL_BG);

        JLabel label = new JLabel("请输入密码：");
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));

        passwordField = new JPasswordField(30);
        passwordField.setFont(new Font("Consolas", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(300, 35));

        showPasswordCheckBox = new JCheckBox("显示密码");
        showPasswordCheckBox.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        showPasswordCheckBox.setBackground(PANEL_BG);
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('●');
            }
        });

        // 实时监听密码输入
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { onPasswordChanged(); }
            @Override
            public void removeUpdate(DocumentEvent e) { onPasswordChanged(); }
            @Override
            public void changedUpdate(DocumentEvent e) { onPasswordChanged(); }

            private void onPasswordChanged() {
                String password = new String(passwordField.getPassword());
                updateResult(password);
            }
        });

        inputRow.add(label);
        inputRow.add(passwordField);
        inputRow.add(showPasswordCheckBox);

        panel.add(inputRow);
        return panel;
    }

    private JPanel createScorePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBackground(PANEL_BG);
        panel.setBorder(createRoundedBorder("综合评分"));
        panel.setMaximumSize(new Dimension(650, 90));

        // 左侧：进度条
        strengthBar = new JProgressBar(0, 100);
        strengthBar.setValue(0);
        strengthBar.setStringPainted(true);
        strengthBar.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        strengthBar.setPreferredSize(new Dimension(350, 30));
        strengthBar.setForeground(new Color(189, 195, 199)); // 默认灰色
        panel.add(strengthBar, BorderLayout.CENTER);

        // 右侧：分数和等级
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 3, 3));
        infoPanel.setBackground(PANEL_BG);

        scoreLabel = new JLabel("分数: --");
        scoreLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));

        levelLabel = new JLabel("等级: 无输入");
        levelLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));

        infoPanel.add(scoreLabel);
        infoPanel.add(levelLabel);
        panel.add(infoPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createRulesDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(createRoundedBorder("各规则评测详情"));
        panel.setMaximumSize(new Dimension(650, 200));

        rulesPanel = new JPanel();
        rulesPanel.setLayout(new BoxLayout(rulesPanel, BoxLayout.Y_AXIS));
        rulesPanel.setBackground(PANEL_BG);

        // 为每个规则创建一行（初始为空）
        for (var rule : calculator.getRules()) {
            JPanel row = createRuleRow(rule.getName(), "--", 0);
            rulesPanel.add(row);
        }

        JScrollPane scroll = new JScrollPane(rulesPanel);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(620, 150));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRuleRow(String name, String scoreText, int score) {
        JPanel row = new JPanel(new BorderLayout(20, 0));
        row.setBackground(PANEL_BG);
        row.setMaximumSize(new Dimension(600, 28));
        row.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        JLabel nameLabel = new JLabel("• " + name);
        nameLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        row.add(nameLabel, BorderLayout.WEST);

        JLabel scoreLabel = new JLabel(scoreText);
        scoreLabel.setFont(new Font("Consolas", Font.PLAIN, 13));
        scoreLabel.setForeground(getScoreColor(score));
        row.add(scoreLabel, BorderLayout.EAST);

        row.putClientProperty("scoreLabel", scoreLabel);
        row.putClientProperty("ruleName", name);
        return row;
    }

    private JPanel createSuggestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(createRoundedBorder("改进建议"));
        panel.setMaximumSize(new Dimension(650, 100));

        suggestionArea = new JTextArea(3, 40);
        suggestionArea.setEditable(false);
        suggestionArea.setLineWrap(true);
        suggestionArea.setWrapStyleWord(true);
        suggestionArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        suggestionArea.setBackground(new Color(255, 251, 235));
        suggestionArea.setText("请输入密码以获取改进建议");

        JScrollPane scroll = new JScrollPane(suggestionArea);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(BG_COLOR);
        panel.setMaximumSize(new Dimension(650, 40));

        batchButton = new JButton("📂 批量检测（从文件）");
        batchButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        batchButton.addActionListener(e -> openBatchDialog());
        panel.add(batchButton);

        JButton clearButton = new JButton("清空");
        clearButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        clearButton.addActionListener(e -> {
            passwordField.setText("");
            updateResult(null);
        });
        panel.add(clearButton);

        return panel;
    }

    /** 密码变更时更新所有UI */
    private void updateResult(String password) {
        if (password == null || password.isEmpty()) {
            strengthBar.setValue(0);
            strengthBar.setString("等待输入...");
            strengthBar.setForeground(new Color(189, 195, 199));
            scoreLabel.setText("分数: --");
            levelLabel.setText("等级: 无输入");
            levelLabel.setForeground(Color.GRAY);
            suggestionArea.setText("请输入密码以获取改进建议");

            // 重置规则行
            for (java.awt.Component comp : rulesPanel.getComponents()) {
                if (comp instanceof JPanel row) {
                    JLabel sl = (JLabel) row.getClientProperty("scoreLabel");
                    if (sl != null) {
                        sl.setText("--");
                        sl.setForeground(Color.GRAY);
                    }
                }
            }
            return;
        }

        StrengthResult result = calculator.evaluate(password);

        // 进度条
        int score = result.getTotalScore();
        strengthBar.setValue(score);
        strengthBar.setString(score + " / 100");
        Color barColor = getBarColor(score);
        strengthBar.setForeground(barColor);

        // 分数和等级
        scoreLabel.setText("分数: " + score + " / 100");
        levelLabel.setText("等级: " + result.getLevel());
        levelLabel.setForeground(barColor);

        // 规则详情
        Map<String, Integer> ruleScores = result.getRuleScores();
        for (java.awt.Component comp : rulesPanel.getComponents()) {
            if (comp instanceof JPanel row) {
                String ruleName = (String) row.getClientProperty("ruleName");
                JLabel sl = (JLabel) row.getClientProperty("scoreLabel");
                if (ruleName != null && sl != null && ruleScores.containsKey(ruleName)) {
                    int rs = ruleScores.get(ruleName);
                    sl.setText(rs + " 分");
                    sl.setForeground(getScoreColor(rs));
                }
            }
        }

        // 建议
        if (result.getSuggestions().isEmpty()) {
            suggestionArea.setText("✅ 密码强度良好，无需改进建议。");
        } else {
            StringBuilder sb = new StringBuilder();
            for (String sug : result.getSuggestions()) {
                sb.append("⚠ ").append(sug).append("\n");
            }
            suggestionArea.setText(sb.toString().trim());
        }
        suggestionArea.setCaretPosition(0);
    }

    /** 打开批量检测对话框 */
    private void openBatchDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择包含密码的文本文件（每行一个密码）");
        fileChooser.setCurrentDirectory(new File("."));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            new BatchResultDialog(this, calculator, file).setVisible(true);
        }
    }

    /** 根据分数获取进度条颜色 */
    private Color getBarColor(int score) {
        if (score <= 20) return VERY_WEAK_COLOR;
        if (score <= 40) return WEAK_COLOR;
        if (score <= 60) return MEDIUM_COLOR;
        if (score <= 80) return STRONG_COLOR;
        return VERY_STRONG_COLOR;
    }

    /** 根据分数获取文字颜色 */
    private Color getScoreColor(int score) {
        if (score <= 20) return VERY_WEAK_COLOR;
        if (score <= 40) return WEAK_COLOR;
        if (score <= 70) return MEDIUM_COLOR;
        return STRONG_COLOR;
    }

    /** 创建圆角边框面板 */
    private Border createRoundedBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1, true),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Microsoft YaHei", Font.BOLD, 13),
                TITLE_COLOR
        );
    }
}