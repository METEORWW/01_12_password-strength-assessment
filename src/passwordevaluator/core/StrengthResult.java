package passwordevaluator.core;

import java.util.List;
import java.util.Map;

/**
 * 密码强度评测结果
 * 封装总分、等级、各规则得分及改进建议
 */
public class StrengthResult {
    private final int totalScore;
    private final String level;
    private final Map<String, Integer> ruleScores;
    private final List<String> suggestions;

    public StrengthResult(int totalScore, String level,
                          Map<String, Integer> ruleScores,
                          List<String> suggestions) {
        this.totalScore = totalScore;
        this.level = level;
        this.ruleScores = ruleScores;
        this.suggestions = suggestions;
    }

    public int getTotalScore() { return totalScore; }

    public String getLevel() { return level; }

    public Map<String, Integer> getRuleScores() { return ruleScores; }

    public List<String> getSuggestions() { return suggestions; }
}