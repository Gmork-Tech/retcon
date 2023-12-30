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

import java.util.Map;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(ValueType.Values.OBJECT)
public class ObjectConfigProp extends ConfigProp {
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Convert(converter = ObjectPropConverter.class)
    private Map<String,Object> val;


    static class ObjectPropConverter implements AttributeConverter<Map<String,Object>, String> {

        @Override
        public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
            try {
                return CustomJacksonMapper.getInstance().writeValueAsString(stringObjectMap);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        @Override
        public Map<String, Object> convertToEntityAttribute(String s) {
            try {
                var mapRef = new TypeReference<Map<String, Object>>(){};
                return CustomJacksonMapper.getInstance().readValue(s, mapRef);
            } catch (JsonProcessingException e) {
                Log.warn("Issue parsing object map from DB", e);
                return null;
            }
        }
    }

}
