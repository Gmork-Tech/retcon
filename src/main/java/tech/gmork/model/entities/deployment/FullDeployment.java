package tech.gmork.model.entities.deployment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import tech.gmork.model.dtos.Subscriber;
import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.DeploymentStrategy;
import tech.gmork.model.helper.ChangeRequest;
import tech.gmork.model.helper.Compliance;
import tech.gmork.model.helper.QuartzJob;

import java.util.Optional;
import java.util.Set;


@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(DeploymentStrategy.Values.FULL)
public class FullDeployment extends Deployment {

    @Override
    public Optional<QuartzJob> schedule() {
        return Optional.empty();
    }

    @Override
    public ChangeRequest determineChange(Compliance compliance) {
        return ChangeRequest.fromCompliance(compliance);
    }

    @Override
    public int determineIdeal(Set<Subscriber> subscribers) {
        return subscribers.size();
    }

    @Override
    public void validate() {}

}
