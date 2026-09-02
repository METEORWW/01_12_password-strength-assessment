package passwordevaluator.core;

import passwordevaluator.rules.*;
import passwordevaluator.loader.WeakPasswordLoader;

import java.util.*;

/**
 * 密码强度计算器（组合多个评测规则）
 * 使用组合模式管理所有规则，计算加权总分
 */
public class StrengthCalculator {
    private final List<PasswordEvaluator> rules;

    public StrengthCalculator() {
        this.rules = new ArrayList<>();
    }

    /**
     * 创建包含全部默认规则的 StrengthCalculator
     * @param loader 弱口令加载器（用于字典检查规则）
     */
    public static StrengthCalculator createDefault(WeakPasswordLoader loader) {
        StrengthCalculator calc = new StrengthCalculator();
        calc.addRule(new LengthRule());
        calc.addRule(new CharCompositionRule());
        calc.addRule(new DictionaryCheckRule(loader));
        calc.addRule(new RepeatingPatternRule());
        calc.addRule(new KeyboardSequenceRule());
        calc.addRule(new CommonPatternRule());
        return calc;
    }

    public void addRule(PasswordEvaluator rule) {
        rules.add(rule);
    }

    /**
     * 对密码进行综合评测
     */
    public StrengthResult evaluate(String password) {
        if (password == null || password.isEmpty()) {
            Map<String, Integer> emptyScores = new LinkedHashMap<>();
            for (PasswordEvaluator rule : rules) {
                emptyScores.put(rule.getName(), 0);
            }
            return new StrengthResult(0, "无输入", emptyScores,
                    Collections.singletonList("请输入密码"));
        }

        Map<String, Integer> ruleScores = new LinkedHashMap<>();
        List<String> suggestions = new ArrayList<>();
        double totalWeightedScore = 0.0;

        for (PasswordEvaluator rule : rules) {
            int score = rule.evaluate(password);
            ruleScores.put(rule.getName(), score);

            if (score < 50 && rule instanceof AbstractRule) {
                String sug = ((AbstractRule) rule).getSuggestion();
                if (sug != null && !sug.isEmpty()) {
                    suggestions.add("【" + rule.getName() + "】" + sug);
                }
            }

            totalWeightedScore += score * rule.getWeight();
        }

        int finalScore = (int) Math.round(Math.max(0, Math.min(100, totalWeightedScore)));

        String level;
        if (finalScore <= 20) {
            level = "非常弱 🔴";
        } else if (finalScore <= 40) {
            level = "弱 🟠";
        } else if (finalScore <= 60) {
            level = "中等 🟡";
        } else if (finalScore <= 80) {
            level = "强 🟢";
        } else {
            level = "非常强 🟢⭐";
        }

        return new StrengthResult(finalScore, level, ruleScores, suggestions);
    }

    public List<PasswordEvaluator> getRules() {
        return Collections.unmodifiableList(rules);
    }
}