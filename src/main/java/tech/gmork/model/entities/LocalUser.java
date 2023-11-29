package tech.gmork.model.entities;

import jakarta.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.ws.rs.WebApplicationException;
import tech.gmork.model.Validatable;

@Entity
@UserDefinition
public class LocalUser extends PanacheEntity implements Validatable {
    @Username
    public String username;
    @Password
    public String password;
    @Roles
    public String role;

    /**
     * Adds a new user to the database
     * @param username the username
     * @param password the unencrypted password (it will be encrypted with bcrypt)
     * @param role the comma-separated roles
     */
    public static void add(String username, String password, String role) {
        LocalUser user = new LocalUser();
        user.username = username;
        user.password = BcryptUtil.bcryptHash(password);
        user.role = role;
        user.persist();
    }

    @Override
    public void validate() {
        if (username == null || password == null || role == null) {
            throw new WebApplicationException("A user must have a username, password, and role");
        }
    }
}