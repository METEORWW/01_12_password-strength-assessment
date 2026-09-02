package passwordevaluator.core;

/**
 * 密码评测器接口（策略模式的核心抽象）
 * 所有评测规则必须实现此接口
 */
public interface PasswordEvaluator {
    /**
     * 对密码进行评测
     * @param password 待评测的密码字符串
     * @return 0~100 的整数分数
     */
    int evaluate(String password);

    /** 获取规则名称 */
    String getName();

    /** 获取规则在总分中的权重（0.0 ~ 1.0） */
    double getWeight();
}