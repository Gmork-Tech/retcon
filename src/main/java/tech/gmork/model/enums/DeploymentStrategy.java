package tech.gmork.model.enums;

// Use enum with value for discriminator column, inspired by: https://stackoverflow.com/a/26719904/3019198
public enum DeploymentStrategy {
    FULL(Values.FULL),
    MANUAL(Values.MANUAL),
    BY_PERCENTAGE(Values.BY_PERCENTAGE),
    BY_QUANTITY(Values.BY_QUANTITY);
    private String value;

    DeploymentStrategy(String val) {
        // force equality between name of enum instance, and value of constant
        if (!this.name().equals(val))
            throw new IllegalArgumentException("Incorrect use of DeploymentStrategy");
    }

    public static class Values {
        public static final String FULL = "full";
        public static final String BY_PERCENTAGE = "percent";
        public static final String BY_QUANTITY = "quantity";
        public static final String MANUAL = "manual";
    }

}
