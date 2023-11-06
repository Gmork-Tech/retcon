package tech.gmork.model.entities.deployment;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import tech.gmork.model.Validatable;
import tech.gmork.model.dtos.Subscriber;
import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.DeploymentStrategy;

import java.time.Duration;
import java.util.Set;


@Entity
@DiscriminatorValue(DeploymentStrategy.Values.ALL_AT_ONCE)
public class AllAtOnceDeployment extends Deployment {

    @Override @Transient
    public Uni<Void> deploy(Set<Subscriber> subscribers) {
        return Multi.createFrom().iterable(subscribers)
                .onItem()
                .transformToUni(subscriber ->
                        Uni.createFrom().future(subscriber.getSession().getAsyncRemote().sendObject(this)))
                .merge()
                .onFailure()
                .retry()
                .withBackOff(Duration.ofMillis(500))
                .atMost(3)
                .onFailure()
                .invoke(throwable -> Log.error("Error providing AAO deployment to a subscriber: ", throwable))
                .collect()
                .last()
                .replaceWithVoid();
    }

    @Override
    public void validate() {

    }
}
