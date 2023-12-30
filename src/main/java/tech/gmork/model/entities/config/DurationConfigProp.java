package tech.gmork.model.entities.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.logging.Log;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tech.gmork.control.json.CustomJacksonMapper;
import tech.gmork.model.entities.ConfigProp;
import tech.gmork.model.enums.ValueType;

import java.time.Duration;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(ValueType.Values.DURATION)
public class DurationConfigProp extends ConfigProp {
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Convert(converter = DurationPropConverter.class)
    private Duration val;

    static class DurationPropConverter implements AttributeConverter<Duration, String> {

        @Override
        public String convertToDatabaseColumn(Duration duration) {
            try {
                return CustomJacksonMapper.getInstance().writeValueAsString(duration);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        @Override
        public Duration convertToEntityAttribute(String s) {
            try {
                return CustomJacksonMapper.getInstance().readValue(s, Duration.class);
            } catch (JsonProcessingException e) {
                Log.warn("Issue parsing duration from DB", e);
                return null;
            }
        }
    }
}
