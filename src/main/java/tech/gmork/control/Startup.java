package tech.gmork.control;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import io.quarkus.runtime.StartupEvent;
import tech.gmork.model.entities.LocalUser;


@Singleton
public class Startup {
    @Transactional
    public void loadUsers(@Observes StartupEvent evt) {
        // reset and load all test users
        LocalUser.deleteAll();
        LocalUser.add("admin", "admin", "admin");
        LocalUser.add("user", "user", "user");
    }
}