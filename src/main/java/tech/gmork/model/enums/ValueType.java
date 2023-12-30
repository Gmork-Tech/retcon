package tech.gmork.model.enums;

// Use enum with value for discriminator column, inspired by: https://stackoverflow.com/a/26719904/3019198
public enum ValueType {
    STRING(Values.STRING),
    NUMBER(Values.NUMBER),
    BOOLEAN(Values.BOOLEAN),
    TIMESTAMP(Values.TIMESTAMP),
    DURATION(Values.DURATION),
    OBJECT(Values.OBJECT),
    ARRAY(Values.ARRAY);
    private String value;

    ValueType(String val) {
        // force equality between name of enum instance, and value of constant
        if (!this.name().equals(val))
            throw new IllegalArgumentException("Incorrect use of ValueType");
    }

    public static class Values {
        public static final String STRING = "STRING";
        public static final String NUMBER = "NUMBER";
        public static final String BOOLEAN = "BOOLEAN";
        public static final String TIMESTAMP = "TIMESTAMP";
        public static final String DURATION = "DURATION";
        public static final String OBJECT = "OBJECT";
        public static final String ARRAY = "ARRAY";
    }

}