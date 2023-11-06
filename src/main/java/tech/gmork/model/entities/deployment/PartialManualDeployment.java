package tech.gmork.model.entities.deployment;

import io.smallrye.mutiny.Uni;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tech.gmork.model.Validatable;
import tech.gmork.model.dtos.Subscriber;
import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.DeploymentStrategy;

import java.util.Set;

@Entity
@DiscriminatorValue(DeploymentStrategy.Values.PARTIAL_BY_USER_DEFINED_HOST_IDS)
public class PartialManualDeployment extends Deployment {

    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> targetHosts;

    @Override
    public void validate() {

    }

    @Override
    public Uni<Void> deploy(Set<Subscriber> subscribers) {
        return null;
    }
}
