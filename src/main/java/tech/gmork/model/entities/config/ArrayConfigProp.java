package tech.gmork.model.entities.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

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

import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(ValueType.Values.ARRAY)
public class ArrayConfigProp extends ConfigProp {

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Convert(converter = ArrayPropConverter.class)
    private List<Object> val;

    @Override
    protected boolean hasValue() {
        return val != null;
    }

    static class ArrayPropConverter implements AttributeConverter<List<Object>, String> {

        @Override
        public String convertToDatabaseColumn(List<Object> objects) {
            try {
                return CustomJacksonMapper.getInstance().writeValueAsString(objects);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        @Override
        public List<Object> convertToEntityAttribute(String s) {
            try {
                var listRef = new TypeReference<List<Object>>(){};
                return CustomJacksonMapper.getInstance().readValue(s, listRef);
            } catch (JsonProcessingException e) {
                Log.warn("Issue parsing array from DB", e);
                return null;
            }
        }
    }
}
