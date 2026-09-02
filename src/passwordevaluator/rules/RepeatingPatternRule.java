package passwordevaluator.rules;

import passwordevaluator.core.AbstractRule;

/**
 * 重复字符/模式检测规则
 * 检测连续重复字符（aaa、111）和重复子串（abcabc）
 */
public class RepeatingPatternRule extends AbstractRule {

    public RepeatingPatternRule() {
        super("重复模式检测", 0.10,
              "避免使用连续重复的字符（如aaa）或重复的模式（如abcabc）");
    }

    @Override
    public int evaluate(String password) {
        int penalty = 0;
        int len = password.length();

        // 1. 检测连续3个及以上相同字符
        int consecutiveSame = 1;
        for (int i = 1; i < len; i++) {
            if (password.charAt(i) == password.charAt(i - 1)) {
                consecutiveSame++;
            } else {
                if (consecutiveSame >= 3) {
                    penalty += (consecutiveSame - 2) * 15;
                }
                consecutiveSame = 1;
            }
        }
        if (consecutiveSame >= 3) {
            penalty += (consecutiveSame - 2) * 15;
        }

        // 2. 检测重复子串（如 "abcabc"、"123123"）
        for (int subLen = 2; subLen <= len / 2; subLen++) {
            for (int i = 0; i + subLen * 2 <= len; i++) {
                String sub1 = password.substring(i, i + subLen);
                String sub2 = password.substring(i + subLen, i + subLen * 2);
                if (sub1.equals(sub2)) {
                    penalty += 20;
                    break; // 找到一个重复子串即可
                }
            }
        }

        return Math.max(0, 100 - penalty);
    }
}