package passwordevaluator.rules;

import passwordevaluator.core.AbstractRule;
import passwordevaluator.loader.WeakPasswordLoader;

/**
 * 弱口令字典匹配规则
 * 若密码出现在弱口令字典中，直接判定为低分
 */
public class DictionaryCheckRule extends AbstractRule {
    private final WeakPasswordLoader loader;

    public DictionaryCheckRule(WeakPasswordLoader loader) {
        super("弱口令字典检测", 0.25,
              "该密码是常见弱口令，极易被暴力破解，请更换");
        this.loader = loader;
    }

    @Override
    public int evaluate(String password) {
        if (loader.isWeak(password)) {
            return 0;   // 命中弱口令字典，得零分
        }
        // 同时检查密码的小写版本（防止大小写绕过）
        if (loader.isWeak(password.toLowerCase())) {
            return 10;
        }
        return 100;
    }
}