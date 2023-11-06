package tech.gmork.model.entities.deployment;


import io.smallrye.mutiny.Uni;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import tech.gmork.model.Validatable;
import tech.gmork.model.dtos.Subscriber;
import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.DeploymentStrategy;

import java.util.Set;

@Entity
@DiscriminatorValue(DeploymentStrategy.Values.PARTIAL_BY_PERCENTAGE)
public class PartialByPercentDeployment extends Deployment {

    private Short targetPercentage;

    @Override
    public void validate() {

    }

    @Override
    public Uni<Void> deploy(Set<Subscriber> subscribers) {
        return null;
    }
}
