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


@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(ValueType.Values.BOOLEAN)
public class BoolConfigProp extends ConfigProp {
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Convert(converter = BoolPropConverter.class)
    private Boolean val;

    @Override
    protected boolean hasValue() {
        return val != null;
    }

    static class BoolPropConverter implements AttributeConverter<Boolean, String> {

        @Override
        public String convertToDatabaseColumn(Boolean aBoolean) {
            return aBoolean.toString();
        }

        @Override
        public Boolean convertToEntityAttribute(String s) {
            try {
                return Boolean.parseBoolean(s);
            } catch (Exception e) {
                Log.warn("Issue parsing boolean from DB", e);
                return null;
            }
        }
    }
}
