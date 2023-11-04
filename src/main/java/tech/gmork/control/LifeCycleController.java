package tech.gmork.control;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.event.Observes;
import tech.gmork.model.entities.Subscriber;

public class LifeCycleController {

    @ActivateRequestContext
    public void onStart(@Observes StartupEvent startupEvent) {
        // Remove all subscribers from the DB since the application is just now starting.
        Subscriber.deleteAll();
    }

}
