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
import tech.gmork.model.dtos.Subscriber;
import tech.gmork.model.entities.deployment.*;
import tech.gmork.model.enums.DeploymentStrategy.Values;
import tech.gmork.model.helper.QuartzJob;

import java.time.Duration;
import java.util.Collection;
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

    public abstract Uni<Void> deploy();

    public Uni<Void> deploy(Collection<Subscriber> subscribers) {
        return Multi.createFrom()
                .iterable(subscribers)
                .onItem()
                .invoke(subscriber -> {
                    var deployedVersion = subscriber.getVersionedDeployments().get(this.getId());
                    if (!subscriber.hasDeployment(id) || deployedVersion == null || deployedVersion != hashCode()) {
                        subscriber.sendDeploymentChangeEvent(this);
                    }
                })
                .onFailure()
                .retry()
                .withBackOff(Duration.ofMillis(500))
                .atMost(3)
                .onItem()
                .invoke(subscriber -> subscriber.updateVersionedDeployment(this.getId(), this.hashCode()))
                .onFailure()
                .invoke(ex -> Log.error("Error providing deployment: " + this.getName() + " to subscriber.", ex))
                .skip()
                .where(ignored -> true)
                .toUni()
                .replaceWithVoid();
    }

    public abstract Optional<QuartzJob> schedule();

}
