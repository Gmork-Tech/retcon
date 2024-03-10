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

import java.time.Instant;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(ValueType.Values.TIMESTAMP)
public class TimestampConfigProp extends ConfigProp {

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Convert(converter = TimstampPropConverter.class)
    private Instant val;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Convert(converter = TimstampPropConverter.class)
    private Instant dval;

    @Override
    protected boolean hasValue() {
        return val != null;
    }

    static class TimstampPropConverter implements AttributeConverter<Instant, String> {

        @Override
        public String convertToDatabaseColumn(Instant instant) {
            return instant.toString();
        }

        @Override
        public Instant convertToEntityAttribute(String s) {
            try {
                return Instant.parse(s);
            } catch (Exception e) {
                Log.warn("Issue parsing timestamp from DB", e);
                return null;
            }
        }
    }
}
