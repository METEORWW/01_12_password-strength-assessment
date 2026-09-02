package passwordevaluator.core;

/**
 * 评测规则的抽象基类
 * 提供名称、权重、改进建议的公共属性和方法
 * 子类只需实现 evaluate() 方法
 */
public abstract class AbstractRule implements PasswordEvaluator {
    protected String name;
    protected double weight;
    protected String suggestion;

    public AbstractRule(String name, double weight, String suggestion) {
        this.name = name;
        this.weight = weight;
        this.suggestion = suggestion;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    /** 当该规则得分较低时，返回给用户的改进建议 */
    public String getSuggestion() {
        return suggestion;
    }

    @Override
    public abstract int evaluate(String password);
}