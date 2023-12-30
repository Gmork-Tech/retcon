package tech.gmork.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import tech.gmork.model.entities.config.*;

import tech.gmork.model.enums.ValueType;

import java.time.Instant;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "deployment", callSuper = false)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StringConfigProp.class, name = ValueType.Values.STRING),
        @JsonSubTypes.Type(value = NumericConfigProp.class, name = ValueType.Values.NUMBER),
        @JsonSubTypes.Type(value = BoolConfigProp.class, name = ValueType.Values.BOOLEAN),
        @JsonSubTypes.Type(value = TimestampConfigProp.class, name = ValueType.Values.TIMESTAMP),
        @JsonSubTypes.Type(value = ObjectConfigProp.class, name = ValueType.Values.OBJECT),
        @JsonSubTypes.Type(value = ArrayConfigProp.class, name = ValueType.Values.ARRAY),
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="kind", discriminatorType = DiscriminatorType.STRING)
public abstract class ConfigProp extends PanacheEntityBase {

    @Id
    protected String name;
    private boolean nullable = false;

    @Column(nullable = false)
    private Instant created = Instant.now();

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "deploymentId")
    protected Deployment deployment;

}
