package tech.gmork.model.entities.deployment;


import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import jakarta.persistence.Transient;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.quartz.*;
import tech.gmork.model.dtos.Subscriber;
import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.DeploymentStrategy;
import tech.gmork.model.helper.QuartzJob;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Setter
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

    @Transient
    private boolean firstRun = true;

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
        if(shouldIncrement) {
            if (incrementDelay == null) {
                throw new WebApplicationException("Percentage based deployments require an increment delay " +
                        "if incremental deployment is requested.", Response.Status.BAD_REQUEST);
            }
            if (incrementDelay.toMillis() < MIN_DELAY_MILLIS) {
                throw new WebApplicationException("Percentage based deployments require a minimum deployment delay of " +
                        MIN_DELAY_MILLIS + "ms if incremental deployment is requested.", Response.Status.BAD_REQUEST);
            }
        }
        if (incrementPercentage == null && shouldIncrement) {
            throw new WebApplicationException("Percentage based deployments require an increment percentage " +
                    "if incremental deployment is requested.", Response.Status.BAD_REQUEST);
        }
    }

    @Override
    public Uni<Void> deploy() {

        // If target percentage is zero or for some reason negative, end the deployment phase immediately
        if (targetPercentage <= 0) {
            Log.info("User has specified that they would like 0% of hosts to receive this deployment.");
            return Uni.createFrom().voidItem();
        }

        // Get the total number of currently connected subscribers, if 0, end the deployment phase immediately
        var totalSubs = this.getApplication().getSubscribers().size();
        if (totalSubs == 0) {
            Log.info("No subscribers for application: " + this.getApplication() + " therefore nothing to deploy.");
            return Uni.createFrom().voidItem();
        }


        List<Subscriber> subsAwaitingDeployment = new ArrayList<>();
        int subsAlreadyDeployed = 0;
        int numSubsToDeployTo = 0;

        // Determine how many subscribers already have the latest deployment version based on hashcode
        for (var subscriber : this.getApplication().getSubscribers()) {
            if (!subscriber.getVersionedDeployments().containsKey(this.getId())) {
                subsAwaitingDeployment.add(subscriber);
            } else {
                if (subscriber.getVersionedDeployments().get(this.getId()) != hashCode()) {
                    subsAwaitingDeployment.add(subscriber);
                } else {
                    subsAlreadyDeployed++;
                }
            }
        }

        // Determine our end goal
        int finalTargetNumSubs = Math.round(totalSubs * (targetPercentage / 100.00F));

        if (!shouldIncrement) {
            numSubsToDeployTo = finalTargetNumSubs;
            // Fallback to 1 if we are under goal, the user requests *some* non-zero target deployment, but the math rounds us down to 0
            if (numSubsToDeployTo == 0 && targetPercentage > 0) {
                numSubsToDeployTo = 1;
            }
            if (numSubsToDeployTo > subsAlreadyDeployed) {
                // Even though incrementing is disabled, we still want to hit our target. This line exists in case auto-scaling increases
                // the number of subscribers at some point, and we are no longer in alignment with our target percentage.
                numSubsToDeployTo = numSubsToDeployTo - subsAlreadyDeployed;
            } else if (numSubsToDeployTo < subsAlreadyDeployed) {
                // This will produce a negative number, indicating we need to remove the config values from some subscribers to fall in line with the target.
                // This can happen when the number of subscribers decreases, potentially due to auto-scaling.
                numSubsToDeployTo = numSubsToDeployTo - subsAlreadyDeployed;
            }
        } else if (this.isFirstRun()) {
            // If this is the first time this deployment has been processed, and we should incrementally deploy, use the initial percentage
            numSubsToDeployTo = Math.round(totalSubs * (initialPercentage / 100.00F));
            // Fallback to 1 if the user requests *some* non-zero initial deployment percentage, but the math rounds us down to 0
            if (numSubsToDeployTo == 0 && initialPercentage > 0) {
                numSubsToDeployTo = 1;
            }
            // Set our transient variable such that this isn't considered again
            // Todo: Might want to persist this property in case the service restarts
            this.setFirstRun(false);
        } else {
            // Else get the currently deployed percentage
            float currentPercentage = ((float) subsAlreadyDeployed / totalSubs) * 100.00F;

            // Now let's consider the possibilities. We are incrementing either positively or negatively.


            // If we've already met goal, end the deployment phase immediately
            if (currentPercentage >= targetPercentage) {
                Log.info("Deployment: " + this.getId() + " already meets or exceeds target deployment percentage");
                return Uni.createFrom().voidItem();
            } else if (currentPercentage + incrementPercentage <= targetPercentage) {
                numSubsToDeployTo = Math.round(totalSubs * (incrementPercentage / 100.00F));
                // Fallback to 1 if we are under goal, the user requests *some* increment and the math rounds us down to 0
                if (numSubsToDeployTo == 0 && incrementPercentage > 0) {
                    numSubsToDeployTo = 1;
                }
            } else {
                float remainderPercentage = targetPercentage - currentPercentage;
                numSubsToDeployTo = Math.round(totalSubs * (remainderPercentage / 100.00F));
                // Fallback to 1 if we are under goal, and the remainder percentage is not 0, but the math rounds us down to 0
                if (numSubsToDeployTo == 0 && remainderPercentage > 0) {
                    numSubsToDeployTo = 1;
                }
            }
        }

        // If we need to deploy more than what remains in order to hit goal, set the number to what remains
        if (numSubsToDeployTo > subsAwaitingDeployment.size()) {
            numSubsToDeployTo = subsAwaitingDeployment.size();
        }

        var subsForDeployment = subsAwaitingDeployment.subList(0, numSubsToDeployTo);

        return Multi.createFrom().iterable(subsForDeployment)
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
                .onItem()
                .invoke(subscriber -> subscriber.updateVersionedDeployment(this.getId(), this.hashCode()))
                .onFailure()
                .invoke(ex -> Log.error("Error providing BY_PERCENT deployment to a subscriber: ", ex))
                .skip()
                .where(ignored -> true)
                .toUni()
                .replaceWithVoid();
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
                .withSchedule(schedule)
                .startNow()
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
