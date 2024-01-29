package tech.gmork.model.entities.config;

import io.quarkus.logging.Log;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tech.gmork.model.entities.ConfigProp;
import tech.gmork.model.enums.ValueType;

import java.text.NumberFormat;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(ValueType.Values.NUMBER)
public class NumericConfigProp extends ConfigProp {
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Convert(converter = NumericPropConverter.class)
    private Number val;

    @Override
    protected boolean hasValue() {
        return val != null;
    }

    static class NumericPropConverter implements AttributeConverter<Number, String> {

        @Override
        public String convertToDatabaseColumn(Number number) {
            return number.toString();
        }

        @Override
        public Number convertToEntityAttribute(String s) {
            try {
                return NumberFormat.getInstance().parse(s);
            } catch (Exception e) {
                Log.warn("Issue parsing number from DB", e);
                return null;
            }
        }
    }
}
