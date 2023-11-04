package tech.gmork.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tech.gmork.model.Validatable;
import tech.gmork.model.enums.DeploymentStrategy;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeploymentKind extends PanacheEntity implements Validatable {

    private DeploymentStrategy strategy = DeploymentStrategy.UNIVERSAL;
    private Short targetDeploymentPercentage;
    private Short targetNumInstances;

    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> targetHosts;

    @Transient @JsonIgnore
    public boolean isDependent() {
        return strategy != DeploymentStrategy.UNIVERSAL;
    }

    @Override
    public void validate() {

    }
}
