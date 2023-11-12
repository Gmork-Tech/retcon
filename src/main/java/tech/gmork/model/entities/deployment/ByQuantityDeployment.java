package tech.gmork.model.entities.deployment;

import io.smallrye.mutiny.Uni;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.EqualsAndHashCode;
import org.quartz.*;

import tech.gmork.model.entities.ConfigProp;
import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.DeploymentStrategy;
import tech.gmork.model.helper.QuartzJob;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Entity
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(DeploymentStrategy.Values.BY_QUANTITY)
public class ByQuantityDeployment extends Deployment {

    private Instant lastDeployed;
    private Short incrementQuantity;
    private Short initialQuantity = 1;
    private boolean shouldIncrement = false;
    private Duration incrementDelay = Duration.ofMinutes(5);
    private Short targetQuantity;
    private boolean convertToFull = false;

    @Override
    public Uni<Void> deploy() {
        return null;
    }

    @Override
    public Optional<QuartzJob> schedule() {
        var job = JobBuilder.newJob(ByQuantityDeployment.class)
                .withIdentity("deployment:" + this.getId())
                .build();

        var schedule = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMilliseconds(incrementDelay.toMillis())
                .repeatForever();

        var trigger = TriggerBuilder.newTrigger()
                .withIdentity( "deployment:" + this.getId())
                .startNow()
                .withSchedule(schedule)
                .build();

        return Optional.of(QuartzJob.fromJobAndTrigger(job, trigger));
    }

    @Override
    public void validate() {
        if (targetQuantity == null) {
            throw new WebApplicationException("Quantity based deployments require a target deployment quantity.",
                    Response.Status.BAD_REQUEST);
        }
        if (initialQuantity == null && shouldIncrement) {
            throw new WebApplicationException("Quantity based deployments require an initial deployment " +
                    "quantity, if incremental deployment is requested.", Response.Status.BAD_REQUEST);
        }
        if (incrementDelay == null && shouldIncrement) {
            throw new WebApplicationException("Quantity based deployments require an increment delay " +
                    "if incremental deployment is requested.", Response.Status.BAD_REQUEST);
        }
        if (incrementQuantity == null && shouldIncrement) {
            throw new WebApplicationException("Quantity based deployments require an increment quantity " +
                    "if incremental deployment is requested.", Response.Status.BAD_REQUEST);
        }
        if (getProps() != null) {
            getProps().forEach(ConfigProp::validate);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        deploy()
                .subscribe()
                .with(item -> {}, fail -> {});
    }
}
