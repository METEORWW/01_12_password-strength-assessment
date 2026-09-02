package passwordevaluator.rules;

import passwordevaluator.core.AbstractRule;

/**
 * 字符组合复杂度评测规则
 * 检测是否包含大写字母、小写字母、数字、特殊符号
 */
public class CharCompositionRule extends AbstractRule {

    public CharCompositionRule() {
        super("字符组合检测", 0.30,
              "建议混合使用大写字母、小写字母、数字和特殊符号（如!@#$%）");
    }

    @Override
    public int evaluate(String password) {
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }

        int types = 0;
        if (hasUpper) types++;
        if (hasLower) types++;
        if (hasDigit) types++;
        if (hasSpecial) types++;

        switch (types) {
            case 1: return 20;
            case 2: return 45;
            case 3: return 70;
            case 4: return 100;
            default: return 0;
        }
    }
}