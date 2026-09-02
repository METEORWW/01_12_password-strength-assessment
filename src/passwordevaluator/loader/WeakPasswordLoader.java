package passwordevaluator.loader;

import java.io.*;
import java.util.*;

/**
 * 弱口令字典加载器
 * 从文件加载常见弱口令列表，文件不存在时使用内置默认列表
 */
public class WeakPasswordLoader {
    private final Set<String> weakPasswords;

    public WeakPasswordLoader() {
        this.weakPasswords = new HashSet<>();
    }

    /**
     * 从文件路径加载弱口令字典
     * @param filePath 字典文件路径（如 "data/weakpass.txt"）
     * @return 加载成功返回 true，失败则使用内置默认字典
     */
    public boolean load(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[警告] 未找到字典文件: " + filePath + "，使用内置默认字典");
            loadDefaults();
            return false;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    weakPasswords.add(line);
                    count++;
                }
            }
            System.out.println("[信息] 成功从文件加载 " + count + " 条弱口令");
            return true;
        } catch (IOException e) {
            System.out.println("[错误] 读取字典文件失败: " + e.getMessage());
            loadDefaults();
            return false;
        }
    }

    /** 内置默认弱口令列表（文件加载失败时的后备方案） */
    private void loadDefaults() {
        String[] defaults = {
            "123456", "password", "12345678", "qwerty", "123456789",
            "12345", "1234", "111111", "1234567", "sunshine",
            "qwerty123", "iloveyou", "admin", "123123", "abc123",
            "football", "monkey", "654321", "!@#$%^&*", "charlie",
            "aa123456", "donald", "password1", "qwerty12345",
            "1234567890", "123456a", "qwertyuiop", "1q2w3e4r",
            "123321", "666666", "888888", "000000", "112233",
            "1qaz2wsx", "hello123", "welcome", "letmein", "dragon"
        };
        Collections.addAll(weakPasswords, defaults);
        System.out.println("[信息] 已加载内置默认字典 " + defaults.length + " 条");
    }

    /** 检查密码是否在弱口令字典中 */
    public boolean isWeak(String password) {
        return weakPasswords.contains(password);
    }

    /** 获取已加载的弱口令数量 */
    public int getCount() {
        return weakPasswords.size();
    }
}