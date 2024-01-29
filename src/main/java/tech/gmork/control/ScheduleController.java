package tech.gmork.control;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduler;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import tech.gmork.model.entities.Deployment;

import static tech.gmork.model.events.InternalEvents.DEPLOYMENT_CHANGE_EVENT;

@ApplicationScoped
public class ScheduleController {

    @Inject
    Scheduler scheduler;

    @ActivateRequestContext
    void onStart(@Observes StartupEvent startupEvent) {
        Deployment.<Deployment>streamAll()
                .forEach(this::scheduleJob);
    }

    void scheduleJob(Deployment deployment) {
        deployment.schedule()
                .ifPresent(job -> scheduler.newJob(job.getName())
                        .setInterval(job.getInterval())
                        .setAsyncTask(executionContext -> deployment.deploy())
                        .schedule());
    }

    @ConsumeEvent(value = DEPLOYMENT_CHANGE_EVENT, blocking = true)
    void rescheduleJob(Deployment deployment) {
        deployment.schedule()
                .ifPresent(job -> {
                    scheduler.unscheduleJob(job.getName());
                    scheduler.newJob("Deployment: " + deployment.getId())
                            .setInterval(job.getInterval())
                            .setAsyncTask(executionContext -> deployment.deploy())
                            .schedule();
                });
    }

}
