package tech.gmork.model.entities.config;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
@DiscriminatorValue(ValueType.Values.STRING)
public class StringConfigProp extends ConfigProp {
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String val;
}
