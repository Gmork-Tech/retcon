package tech.gmork.model.entities;

import com.fasterxml.jackson.annotation.*;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.config.ConfigProvider;
import org.quartz.Job;
import tech.gmork.model.Validatable;
import tech.gmork.model.entities.deployment.*;
import tech.gmork.model.enums.DeploymentStrategy;
import tech.gmork.model.enums.DeploymentStrategy.Values;
import tech.gmork.model.helper.QuartzJob;

import java.util.List;
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
public abstract class Deployment extends PanacheEntityBase implements Validatable, Job {

    protected static final long MIN_DELAY_MILLIS =
            ConfigProvider.getConfig().getValue("deployments.minimum.increment.delay.millis", long.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(nullable = false)
    private short priority = 1;

    @OneToMany(mappedBy = "deployment", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ConfigProp> props;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "applicationId")
    private Application application;

    public abstract Uni<Void> deploy();

    public abstract Optional<QuartzJob> schedule();

}
