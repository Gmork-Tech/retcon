package tech.gmork.model.entities.deployment;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import org.quartz.JobExecutionContext;

import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.DeploymentStrategy;
import tech.gmork.model.helper.QuartzJob;

import java.time.Duration;
import java.util.Optional;


@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(DeploymentStrategy.Values.FULL)
public class FullDeployment extends Deployment {

    @Override @Transient
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
                .invoke(throwable -> Log.error("Error providing AAO deployment to a subscriber: ", throwable))
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
