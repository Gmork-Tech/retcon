package tech.gmork.model.entities.deployment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
@DiscriminatorValue(DeploymentStrategy.Values.MANUAL)
public class PartialManualDeployment extends Deployment {

    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> targetHosts;

    @Override
    public void validate() {}

    @Override
    public Optional<QuartzJob> schedule() {
        return Optional.empty();
    }

    @Override
    public ChangeRequest determineChange(Compliance compliance) {
        return null;
    }

    @Override
    public int determineIdeal(Set<Subscriber> subscribers) {
        return 0;
    }

}
