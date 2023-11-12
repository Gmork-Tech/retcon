package tech.gmork.model.entities.deployment;

import io.smallrye.mutiny.Uni;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.quartz.JobExecutionContext;

import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.DeploymentStrategy;
import tech.gmork.model.helper.QuartzJob;

import java.util.Optional;
import java.util.Set;

@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(DeploymentStrategy.Values.MANUAL)
public class PartialManualDeployment extends Deployment {

    @JdbcTypeCode(SqlTypes.JSON)
    private Set<String> targetHosts;

    @Override
    public void validate() {

    }

    @Override
    public Uni<Void> deploy() {
        return null;
    }

    @Override
    public Optional<QuartzJob> schedule() {
        return Optional.empty();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {}

}
