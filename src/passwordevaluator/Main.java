package passwordevaluator;

import passwordevaluator.loader.WeakPasswordLoader;
import passwordevaluator.ui.MainFrame;

import javax.swing.*;

/**
 * 程序入口
 * 加载弱口令字典并启动 GUI 主界面
 */
public class Main {
    public static void main(String[] args) {
        // 设置系统外观为 Nimbus（更现代的外观，Java 自带）
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // 若 Nimbus 不可用，使用默认外观
        }

        // 加载弱口令字典
        WeakPasswordLoader loader = new WeakPasswordLoader();
        loader.load("data/weakpass.txt");

        // 启动 GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(loader);
            frame.setVisible(true);
        });
    }
}