package tech.gmork.model.entities;

import com.fasterxml.jackson.annotation.*;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.quartz.Job;
import tech.gmork.model.Validatable;
import tech.gmork.model.entities.deployment.*;
import tech.gmork.model.enums.DeploymentStrategy.Values;
import tech.gmork.model.helper.QuartzJob;

import java.util.Optional;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FullDeployment.class, name = Values.FULL),
        @JsonSubTypes.Type(value = ByQuantityDeployment.class, name = Values.BY_QUANTITY),
        @JsonSubTypes.Type(value = ByPercentDeployment.class, name = Values.BY_PERCENTAGE),
        @JsonSubTypes.Type(value = PartialManualDeployment.class, name = Values.MANUAL)
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="kind", discriminatorType = DiscriminatorType.STRING)
public abstract class Deployment extends PanacheEntityBase implements Validatable, Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(mappedBy = "deployment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ConfigProp> props;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationId")
    private Application application;

    public abstract Uni<Void> deploy();

    public abstract Optional<QuartzJob> schedule();

}
