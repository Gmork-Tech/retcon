package tech.gmork.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tech.gmork.model.Validatable;
import tech.gmork.model.enums.PropType;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ConfigProp extends PanacheEntityBase implements Validatable {

    @Id
    private String name;
    private boolean nullable = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "val")
    private Object value;

    @Column(nullable = false)
    private PropType propType;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "deploymentId")
    private Deployment deployment;

    @Override
    public void validate() {
        switch (propType) {
            case STRING -> {}
            case NUMBER -> {
                // TODO Add regex values and runtime type checking for configuration properties
                if (!value.toString().matches("")) {
                    throw new WebApplicationException("Value for prop " + name + " in deployment " +
                            deployment.getName() + " of application " + deployment.getApplication().getName() +
                            " cannot be converted to a number.", Response.Status.BAD_REQUEST);
                }
            }
            case BOOLEAN -> {}
            case TIMESTAMP -> {}
            case OBJECT -> {}
            case ARRAY -> {}
        }
    }
}
