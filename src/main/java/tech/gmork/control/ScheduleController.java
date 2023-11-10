package tech.gmork.control;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import tech.gmork.model.entities.Deployment;

import java.util.Optional;

import static tech.gmork.model.events.InternalEvents.DEPLOYMENT_CHANGE_EVENT;

@ApplicationScoped
public class ScheduleController {

    @Inject
    Scheduler scheduler;

    @ActivateRequestContext
    void onStart(@Observes StartupEvent startupEvent) throws SchedulerException {
        scheduler.clear();
        Deployment.<Deployment>streamAll()
                .map(Deployment::schedule)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(job -> {
                    try {
                        scheduler.scheduleJob(job.getDetails(), job.getTrigger());
                    }
                    catch (SchedulerException e) {
                        Log.warn("Failed to schedule a job, reason: ", e);
                    }
                });
    }

    @ConsumeEvent(value = DEPLOYMENT_CHANGE_EVENT, blocking = true)
    void onDeploymentChanged(Deployment deployment) {
        deployment.schedule()
                .ifPresent(job -> {
                    try {
                        scheduler.rescheduleJob(job.getTrigger().getKey(), job.getTrigger());
                    }
                    catch (SchedulerException e) {
                        Log.warn("Failed to reschedule a job, reason: ", e);
                    }
                });
    }

}
