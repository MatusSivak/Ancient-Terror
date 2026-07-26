package sk.sivak.eldritchhorror.core.constants;

public class Interval {
    private Integer min;
    private Integer max;

    private Interval(Integer min, Integer max) {
        this.min = min;
        this.max = max;
    }

    public static Interval build(String expression) {
        // 1-2
        // 3+
        // 0
        if (expression.contains("-")) {
            String[] split = expression.split("-");
            Integer min = Integer.valueOf(split[0]);
            Integer max = Integer.valueOf(split[1]);
            return new Interval(min, max);
        } else if (expression.contains("+")) {
            Integer min = Integer.valueOf(expression.substring(0, expression.length() - 1));
            return new Interval(min, null);
        } else {
            return new Interval(Integer.valueOf(expression), Integer.valueOf(expression));
        }
    }

    @Override
    public String toString() {
        if (max == null) {
            return min + "+";
        } else {
            return min + "-" + max;
        }
    }

    public boolean test(int rolledValue) {
        if (max == null) {
            return min <= rolledValue;
        } else {
            return min <= rolledValue && max >= rolledValue;
        }
    }
}
