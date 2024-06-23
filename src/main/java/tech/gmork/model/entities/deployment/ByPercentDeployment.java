package tech.gmork.model.entities.deployment;


import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import jakarta.persistence.Transient;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import tech.gmork.model.dtos.Subscriber;
import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.ChangeType;
import tech.gmork.model.enums.DeploymentStrategy;
import tech.gmork.model.helper.ChangeRequest;
import tech.gmork.model.helper.Compliance;
import tech.gmork.model.helper.QuartzJob;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

    @Transient
    private boolean firstRun = true;

    @Override
    public void validate() {
        if (targetPercentage == null) {
            throw new WebApplicationException("Percentage based deployments require a target deployment percentage.",
                    Response.Status.BAD_REQUEST);
        }
        if(shouldIncrement) {
            if (initialPercentage == null) {
                throw new WebApplicationException("Percentage based deployments require an initial deployment " +
                        "percentage, if incremental deployment is requested.", Response.Status.BAD_REQUEST);
            }
            if (incrementPercentage == null) {
                throw new WebApplicationException("Percentage based deployments require an increment percentage " +
                        "if incremental deployment is requested.", Response.Status.BAD_REQUEST);
            }
            if (incrementDelay == null) {
                throw new WebApplicationException("Percentage based deployments require an increment delay " +
                        "if incremental deployment is requested.", Response.Status.BAD_REQUEST);
            }
            if (incrementDelay.toMillis() < MIN_DELAY_MILLIS) {
                throw new WebApplicationException("Percentage based deployments require a minimum deployment delay of " +
                        MIN_DELAY_MILLIS + "ms if incremental deployment is requested.", Response.Status.BAD_REQUEST);
            }
        }
    }

    @Override
    public int determineIdeal(Set<Subscriber> subscribers) {
        int totalSubs = subscribers.size();
        // Determine the ideal number of subscribers to have the deployment in order to be compliant
        int idealTargetNumSubs = Math.round(totalSubs * (targetPercentage / 100.00F));
        // Fallback to 1 if the user requests *some* non-zero target deployment, but the math rounds us down to 0
        if (idealTargetNumSubs == 0 && targetPercentage > 0) {
            idealTargetNumSubs = 1;
        }
        return idealTargetNumSubs;
    }

    @Override
    public ChangeRequest determineChange(Compliance compliance) {

        var req = new ChangeRequest();
        int totalSubs = this.getApplication().getSubscribers().size();

        int numSubsToDeployTo;

        if (!shouldIncrement) {
            return ChangeRequest.fromCompliance(compliance);
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
            short currentPercentage = (short) (((float) compliance.getCompliantSubscribers().size() / totalSubs) * 100);
            // If we are perfect, we can end immediately
            if (currentPercentage == targetPercentage) {
                Log.info("Deployment: " + this.getName() + " already meets target deployment percentage");
                req.setChangeType(ChangeType.HOLD);
                return req;
            } else if (currentPercentage > targetPercentage) {
                // If we are decrementing
                if (currentPercentage - incrementPercentage >= targetPercentage) {
                    numSubsToDeployTo = -Math.round(totalSubs * (incrementPercentage / 100.00F));
                } else {
                    short remainderPercentage = (short) (currentPercentage - targetPercentage);
                    numSubsToDeployTo = -Math.round(totalSubs * (remainderPercentage / 100.00F));
                }
            } else {
                // If we are incrementing
                if (currentPercentage + incrementPercentage <= targetPercentage) {
                    numSubsToDeployTo = Math.round(totalSubs * (incrementPercentage / 100.00F));
                } else {
                    short remainderPercentage = (short) (targetPercentage - currentPercentage);
                    numSubsToDeployTo = Math.round(totalSubs * (remainderPercentage / 100.00F));
                }
            }
        }

        // If we need to remove some values from the subs that have the values....
        if (numSubsToDeployTo < 0) {
            req.setChangeType(ChangeType.DECREMENT);
            req.setSubscribers(compliance.getFirstXCompliantSubscribers(compliance.diffCompliantToTarget()));
        } else if (numSubsToDeployTo > 0) {
            req.setChangeType(ChangeType.INCREMENT);
            req.setSubscribers(compliance.getFirstXNonCompliantSubscribers(compliance.diffCompliantToTarget()));
        } else {
            req.setChangeType(ChangeType.HOLD);
        }

        return req;
    }

    @Override
    public Uni<Void> deploy() {

        // If target percentage is negative, end the deployment phase immediately
        if (targetPercentage < 0) {
            Log.info("User has specified an invalid target percentage for deployment: " + this.getName());
            return Uni.createFrom().voidItem();
        }

        // Get the total number of currently connected subscribers, if 0, end the deployment phase immediately
        var subs = this.getApplication().getSubscribers();
        if (subs.isEmpty()) {
            Log.info("No subscribers for application " + this.getApplication().getName() + ", therefore nothing to deploy.");
            return Uni.createFrom().voidItem();
        }

        var compliance = determineCompliance(subs);
        var changeRequest = determineChange(compliance);

        return deploy(changeRequest.getSubscribers());
    }

    @Override
    public Optional<QuartzJob> schedule() {
        var job = QuartzJob.newBuilder()
                .withName("Deployment:" + this.getId())
                .withInterval(this.incrementDelay)
                .build();
        return Optional.of(job);
    }

}
