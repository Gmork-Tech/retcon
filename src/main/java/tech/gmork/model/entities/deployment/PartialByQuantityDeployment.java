package tech.gmork.model.entities.deployment;

import io.smallrye.mutiny.Uni;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import tech.gmork.model.Validatable;
import tech.gmork.model.dtos.Subscriber;
import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.DeploymentStrategy;

import java.util.Set;

@Entity
@DiscriminatorValue(DeploymentStrategy.Values.PARTIAL_BY_QUANTITY)
public class PartialByQuantityDeployment extends Deployment {

    private Short targetNumInstances;

    @Override
    public void validate() {

    }

    @Override
    public Uni<Void> deploy(Set<Subscriber> subscribers) {
        return null;
    }
}
