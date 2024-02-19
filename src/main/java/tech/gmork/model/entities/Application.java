package tech.gmork.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.*;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.gmork.control.SubscriberCache;
import tech.gmork.model.Validatable;
import tech.gmork.model.dtos.Subscriber;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Application extends PanacheEntityBase implements Validatable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean optimizable = false;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    private Set<Deployment> deployments;

    @Override
    public void validate() {
        if (name == null) {
            throw new WebApplicationException("Application must have a name", Response.Status.BAD_REQUEST);
        }
        deployments.forEach(Deployment::validate);
    }

    @Transient @JsonIgnore
    public Set<Subscriber> getSubscribers() {
        return SubscriberCache.getSubscribersByAppKey(this.id);
    }

    @Transient @JsonIgnore
    public void addSubscriber(Subscriber subscriber) {
        SubscriberCache.addSubscriber(this.id, subscriber);
    }

    @Transient @JsonIgnore
    public void removeSubscriber(Subscriber subscriber) {
        SubscriberCache.removeSubscriber(this.id, subscriber);
    }

}
