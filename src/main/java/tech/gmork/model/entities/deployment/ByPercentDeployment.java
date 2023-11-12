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
@DiscriminatorValue(DeploymentStrategy.Values.BY_PERCENTAGE)
public class ByPercentDeployment extends Deployment {

    private Instant lastDeployed;
    private boolean shouldIncrement = false;
    private Duration incrementDelay = Duration.ofMinutes(5);
    private Short incrementPercentage = 5;
    private Short initialPercentage = 5;
    private Short targetPercentage = 100;
    private boolean convertToFull = true;

    @Override
    public void validate() {
        if (targetPercentage == null) {
            throw new WebApplicationException("Percentage based deployments require a target deployment percentage.",
                    Response.Status.BAD_REQUEST);
        }
        if (initialPercentage == null && shouldIncrement) {
            throw new WebApplicationException("Percentage based deployments require an initial deployment " +
                    "percentage, if incremental deployment is requested.", Response.Status.BAD_REQUEST);
        }
        if (incrementDelay == null && shouldIncrement) {
            throw new WebApplicationException("Percentage based deployments require an increment delay " +
                    "if incremental deployment is requested.", Response.Status.BAD_REQUEST);
        }
        if (incrementPercentage == null && shouldIncrement) {
            throw new WebApplicationException("Percentage based deployments require an increment percentage " +
                    "if incremental deployment is requested.", Response.Status.BAD_REQUEST);
        }
        if (getProps() != null) {
            getProps().forEach(ConfigProp::validate);
        }
    }

    @Override
    public Uni<Void> deploy() {
        return null;
    }

    @Override
    public Optional<QuartzJob> schedule() {
        var job = JobBuilder.newJob(ByPercentDeployment.class)
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
    public void execute(JobExecutionContext jobExecutionContext) {
        deploy()
                .subscribe()
                .with(item -> {}, fail -> {});
    }
}
