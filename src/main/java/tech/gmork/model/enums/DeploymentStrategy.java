package tech.gmork.model.enums;

// Use enum with value for discriminator column, inspired by: https://stackoverflow.com/a/26719904/3019198
public enum DeploymentStrategy {
    ALL_AT_ONCE(Values.ALL_AT_ONCE),
    PARTIAL_BY_PERCENTAGE(Values.PARTIAL_BY_PERCENTAGE),
    PARTIAL_BY_QUANTITY(Values.PARTIAL_BY_QUANTITY),
    PARTIAL_BY_USER_DEFINED_HOST_IDS(Values.PARTIAL_BY_USER_DEFINED_HOST_IDS),
    ALL_GRADUAL_BY_PERCENTAGE(Values.ALL_GRADUAL_BY_PERCENTAGE),
    ALL_GRADUAL_BY_QUANTITY(Values.ALL_GRADUAL_BY_QUANTITY);
    private String value;

    DeploymentStrategy(String val) {
        // force equality between name of enum instance, and value of constant
        if (!this.name().equals(val))
            throw new IllegalArgumentException("Incorrect use of DeploymentStrategy");
    }

    public static class Values {
        public static final String ALL_AT_ONCE = "AAO";
        public static final String PARTIAL_BY_PERCENTAGE = "PBP";
        public static final String PARTIAL_BY_QUANTITY = "PBQ";
        public static final String PARTIAL_BY_USER_DEFINED_HOST_IDS = "PBH";
        public static final String ALL_GRADUAL_BY_PERCENTAGE = "AGP";
        public static final String ALL_GRADUAL_BY_QUANTITY = "AGQ";
    }

}
