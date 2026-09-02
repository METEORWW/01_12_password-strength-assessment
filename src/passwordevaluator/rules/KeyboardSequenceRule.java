package passwordevaluator.rules;

import passwordevaluator.core.AbstractRule;

import java.util.*;

/**
 * 键盘相邻序列检测规则
 * 检测密码是否包含键盘上连续相邻的字符序列（如qwerty、123456、asdfgh）
 */
public class KeyboardSequenceRule extends AbstractRule {

    // 预定义的键盘横向序列（长度 ≥ 3）
    private static final List<String> KEYBOARD_SEQUENCES = new ArrayList<>();

    static {
        // 构建键盘行序列
        String[] keyboardRows = {
            "1234567890",
            "qwertyuiop",
            "asdfghjkl",
            "zxcvbnm"
        };

        for (String row : keyboardRows) {
            // 正向序列
            for (int start = 0; start < row.length(); start++) {
                for (int end = start + 3; end <= row.length(); end++) {
                    KEYBOARD_SEQUENCES.add(row.substring(start, end));
                }
            }
            // 反向序列
            String reversed = new StringBuilder(row).reverse().toString();
            for (int start = 0; start < reversed.length(); start++) {
                for (int end = start + 3; end <= reversed.length(); end++) {
                    KEYBOARD_SEQUENCES.add(reversed.substring(start, end));
                }
            }
        }
    }

    public KeyboardSequenceRule() {
        super("键盘序列检测", 0.05,
              "避免使用键盘上相邻的连续字符（如qwerty、123456、asdf）");
    }

    @Override
    public int evaluate(String password) {
        String lower = password.toLowerCase();
        int penalty = 0;

        for (String seq : KEYBOARD_SEQUENCES) {
            if (lower.contains(seq)) {
                // 序列越长，扣分越多
                penalty += seq.length() * 5;
            }
        }

        return Math.max(0, 100 - penalty);
    }
}