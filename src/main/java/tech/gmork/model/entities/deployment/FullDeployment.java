package tech.gmork.model.entities.deployment;

import io.smallrye.mutiny.Uni;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.DeploymentStrategy;
import tech.gmork.model.helper.QuartzJob;

import java.util.Optional;


@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(DeploymentStrategy.Values.FULL)
public class FullDeployment extends Deployment {

    @Override
    public Uni<Void> deploy() {
        return deploy(this.getApplication().getSubscribers());
    }

    @Override
    public Optional<QuartzJob> schedule() {
        return Optional.empty();
    }

    @Override
    public void validate() {

    }

}
