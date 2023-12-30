package tech.gmork.model.entities.deployment;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.quartz.JobExecutionContext;

import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.DeploymentStrategy;
import tech.gmork.model.helper.QuartzJob;

import java.time.Duration;
import java.util.Optional;


@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(DeploymentStrategy.Values.FULL)
public class FullDeployment extends Deployment {

    @Override
    public Uni<Void> deploy() {
        return Multi.createFrom().iterable(this.getApplication().getSubscribers())
                .onItem()
                .invoke(subscriber -> {
                    if (!subscriber.getVersionedDeployments().containsKey(this.getId())) {
                        subscriber.getSession().getAsyncRemote().sendObject(this);
                    }
                    if (subscriber.getVersionedDeployments().get(this.getId()) != this.hashCode()) {
                        subscriber.getSession().getAsyncRemote().sendObject(this);
                    }
                })
                .onFailure()
                .retry()
                .withBackOff(Duration.ofMillis(500))
                .atMost(3)
                .onFailure()
                .invoke(ex -> Log.error("Error providing FULL deployment to a subscriber: ", ex))
                .onItem()
                .invoke(subscriber -> subscriber.updateVersionedDeployment(this.getId(), this.hashCode()))
                .skip()
                .where(ignored -> true)
                .toUni()
                .replaceWithVoid();
    }

    @Override
    public Optional<QuartzJob> schedule() {
        return Optional.empty();
    }

    @Override
    public void validate() {

    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {}

}
