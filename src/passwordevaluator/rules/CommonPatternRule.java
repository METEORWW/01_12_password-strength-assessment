package passwordevaluator.rules;

import passwordevaluator.core.AbstractRule;

/**
 * 常见模式检测规则
 * 检测密码是否匹配常见的不安全模式（纯数字生日、年份相关等）
 */
public class CommonPatternRule extends AbstractRule {

    public CommonPatternRule() {
        super("常见模式检测", 0.05,
              "避免使用纯数字的生日、电话号码等常见模式");
    }

    @Override
    public int evaluate(String password) {
        int penalty = 0;

        // 1. 纯数字且长度在6~10之间（疑似生日或电话号码）
        if (password.matches("\\d{6,10}")) {
            penalty += 40;
        }

        // 2. 检测是否包含年份（1900~2099）
        for (int year = 1900; year <= 2099; year++) {
            if (password.contains(String.valueOf(year))) {
                penalty += 25;
                break;
            }
        }

        // 3. 全部为同一字符类型（已在CharCompositionRule中体现，此做补充）
        if (password.matches("[a-z]+") && password.length() >= 6) {
            penalty += 10; // 纯小写字母且较长
        }
        if (password.matches("[A-Z]+") && password.length() >= 6) {
            penalty += 15; // 纯大写字母且较长
        }

        // 4. 以常见数字结尾（如123、2024等）
        if (password.matches(".*(123|1234|2024|2025|admin)\\d*$")) {
            penalty += 15;
        }

        return Math.max(0, 100 - penalty);
    }
}