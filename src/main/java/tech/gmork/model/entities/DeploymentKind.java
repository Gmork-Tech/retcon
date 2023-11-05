package tech.gmork.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tech.gmork.model.Validatable;
import tech.gmork.model.enums.DeploymentStrategy;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeploymentKind extends PanacheEntityBase implements Validatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private DeploymentStrategy strategy = DeploymentStrategy.COMPLETE_ALL_AT_ONCE;
    private Short targetPercentage;
    private Short targetNumInstances;
    private Instant lastDeployed;

    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> targetHosts;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "deploymentId")
    private Deployment deployment;

    @Transient @JsonIgnore
    public boolean isDependent() {
        return strategy != DeploymentStrategy.COMPLETE_ALL_AT_ONCE;
    }

    @Override
    public void validate() {

    }
}
