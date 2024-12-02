package tech.gmork.model.entities;

import com.fasterxml.jackson.annotation.*;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.ConfigProvider;
import tech.gmork.model.Validatable;
import tech.gmork.model.dtos.ClientTask;
import tech.gmork.model.dtos.Subscriber;
import tech.gmork.model.entities.deployment.*;
import tech.gmork.model.enums.DeploymentStrategy.Values;
import tech.gmork.model.helper.ChangeRequest;
import tech.gmork.model.helper.Compliance;
import tech.gmork.model.helper.QuartzJob;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "application", callSuper = false)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FullDeployment.class, name = Values.FULL),
        @JsonSubTypes.Type(value = ByQuantityDeployment.class, name = Values.BY_QUANTITY),
        @JsonSubTypes.Type(value = ByPercentDeployment.class, name = Values.BY_PERCENTAGE),
        @JsonSubTypes.Type(value = PartialManualDeployment.class, name = Values.MANUAL)
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="kind", discriminatorType = DiscriminatorType.STRING)
public abstract class Deployment extends PanacheEntityBase implements Validatable {

    protected static final long MIN_DELAY_MILLIS =
            ConfigProvider.getConfig().getValue("deployments.minimum.increment.delay.millis", long.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(nullable = false)
    private short priority = 100;

    @OneToMany(mappedBy = "deployment", cascade = CascadeType.ALL)
    private Set<ConfigProp> props;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "applicationId")
    private Application application;

    public Uni<Void> deploy() {
        // Get the total number of currently connected subscribers, if 0, end the deployment phase immediately
        var subs = this.getApplication().getSubscribers();
        if (subs.isEmpty()) {
            Log.info("No subscribers for application " + this.getApplication().getName() + ", therefore nothing to deploy.");
            return Uni.createFrom().voidItem();
        }

        var compliance = determineCompliance(subs);
        var req = determineChange(compliance);
        return Multi.createFrom()
                .iterable(req.getSubscribers())
                .onItem()
                .invoke(subscriber -> {
                    ClientTask task = new ClientTask();
                    task.setChangeType(req.getChangeType());
                    task.setDeployment(this);
                    subscriber.sendDeploymentChangeTask(task);
                })
                .onFailure()
                .retry()
                .withBackOff(Duration.ofMillis(500))
                .atMost(3)
                .onItem()
                .invoke(subscriber -> subscriber.updateVersionedDeployment(this.getId(), this.hashCode()))
                .onFailure()
                .invoke(ex -> Log.error("Error providing deployment: " + this.getName() + " to subscriber.", ex))
                .collect()
                .last()
                .replaceWithVoid();
    }

    public Compliance determineCompliance(Set<Subscriber> subscribers) {

        Set<Subscriber> compliantSubscribers = new HashSet<>();
        Set<Subscriber> nonCompliantSubscribers = new HashSet<>();

        // Determine how many subscribers already have the latest deployment version based on hashcode
        subscribers.forEach(subscriber -> {
            if (subscriber.getVersionedDeployments().containsKey(this.getId()) && subscriber.getVersionedDeployments().get(this.getId()) == hashCode()) {
                compliantSubscribers.add(subscriber);
            } else {
                nonCompliantSubscribers.add(subscriber);
            }
        });

        return Compliance.newBuilder()
                .withNonCompliantSubscribers(nonCompliantSubscribers)
                .withCompliantSubscribers(compliantSubscribers)
                .withCurrentNumCompliantSubscribers(compliantSubscribers.size())
                .withTargetNumCompliantSubscribers(determineIdeal(subscribers))
                .build();
    }

    public abstract Optional<QuartzJob> schedule();

    public abstract ChangeRequest determineChange(Compliance compliance);

    public abstract int determineIdeal(Set<Subscriber> subscribers);

}
