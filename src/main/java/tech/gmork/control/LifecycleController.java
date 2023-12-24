package tech.gmork.control;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import tech.gmork.model.entities.LocalUser;


@Singleton
public class LifecycleController {

    @ConfigProperty(name = "quarkus.http.auth.basic")
    boolean useBasicAuth;

    @Transactional
    public void loadUsers(@Observes StartupEvent evt) {
        // Don't run the task if basic auth is disabled
        if (useBasicAuth) {
            // Validate that the database contains the admin user, and if not, create the admin user
            if (LocalUser.find("username = ?1", "admin").singleResultOptional().isEmpty()) {
                LocalUser.add("admin", "admin", "admin");
            }
        }
    }
}