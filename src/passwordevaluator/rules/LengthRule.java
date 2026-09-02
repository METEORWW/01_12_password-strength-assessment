package passwordevaluator.rules;

import passwordevaluator.core.AbstractRule;

/**
 * 密码长度评测规则
 * 长度越长得分越高
 */
public class LengthRule extends AbstractRule {

    public LengthRule() {
        super("长度检测", 0.25,
              "建议密码长度至少12位，16位以上更安全");
    }

    @Override
    public int evaluate(String password) {
        int len = password.length();
        if (len < 6) {
            return 0;
        } else if (len < 8) {
            return 20;
        } else if (len < 12) {
            return 50;
        } else if (len < 16) {
            return 75;
        } else {
            return 100;
        }
    }
}