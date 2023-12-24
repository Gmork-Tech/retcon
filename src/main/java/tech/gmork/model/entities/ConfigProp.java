package tech.gmork.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tech.gmork.control.json.CustomJacksonMapper;
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
    private String value;

    @Column(nullable = false)
    private PropType propType;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "deploymentId")
    private Deployment deployment;

    @Override
    public void validate() {
        switch (propType) {
            case STRING -> {
                // Q: When is a string not a string?
                // A: When it's a char!
            }
            case NUMBER -> {
                if (!value.toString().matches("[-+]?[0-9]*\\.?[0-9]+")) {
                    throw new WebApplicationException("Value for prop " + name + " in deployment " +
                            deployment.getName() + " of application " + deployment.getApplication().getName() +
                            " cannot be converted to a number.", Response.Status.BAD_REQUEST);
                }
            }
            case BOOLEAN -> {
                if (!value.toString().matches("(?i)(true|false|1|0)")) {
                    throw new WebApplicationException("Value for prop " + name + " in deployment " +
                            deployment.getName() + " of application " + deployment.getApplication().getName() +
                            " cannot be converted to a boolean.", Response.Status.BAD_REQUEST);
                }
            }
            case TIMESTAMP -> {
                if (!value.toString().matches("^\\d{4}-\\d{2}-\\d{2}(T\\d{2}:\\d{2}(:\\d{2}(\\.\\d+)?Z?)?)?$")) {
                    throw new WebApplicationException("Value for prop " + name + " in deployment " +
                            deployment.getName() + " of application " + deployment.getApplication().getName() +
                            " cannot be converted to a timestamp.", Response.Status.BAD_REQUEST);
                }
            }
            case OBJECT -> {
                try {
                    String str = CustomJacksonMapper.getInstance().writeValueAsString(value);
                    if (!str.startsWith("{") || !str.endsWith("}")) {
                        throw new WebApplicationException("Value for prop " + name + " in deployment " +
                                deployment.getName() + " of application " + deployment.getApplication().getName() +
                                " cannot be converted to an object.", Response.Status.BAD_REQUEST);
                    }
                } catch (JsonProcessingException e) {
                    throw new WebApplicationException("Value for prop " + name + " in deployment " +
                            deployment.getName() + " of application " + deployment.getApplication().getName() +
                            " cannot be converted to an object.", Response.Status.BAD_REQUEST);
                }
            }
            case ARRAY -> {
                try {
                    String str = CustomJacksonMapper.getInstance().writeValueAsString(value);
                    if (!str.startsWith("[") || !str.endsWith("]")) {
                        throw new WebApplicationException("Value for prop " + name + " in deployment " +
                                deployment.getName() + " of application " + deployment.getApplication().getName() +
                                " cannot be converted to an array.", Response.Status.BAD_REQUEST);
                    }
                } catch (JsonProcessingException e) {
                    throw new WebApplicationException("Value for prop " + name + " in deployment " +
                            deployment.getName() + " of application " + deployment.getApplication().getName() +
                            " cannot be converted to an array.", Response.Status.BAD_REQUEST);
                }
            }
        }
    }
}
