package tech.gmork.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.gmork.model.Validatable;
import tech.gmork.model.dtos.Subscriber;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Deployment> deployments;

    @Override
    public void validate() {
        if (name == null) {
            throw new WebApplicationException("Application must have a name", Response.Status.BAD_REQUEST);
        }
        deployments.forEach(Deployment::validate);
    }

    @Transient @JsonIgnore
    private Set<Subscriber> subscribers = new CopyOnWriteArraySet<>();

    @Transient @JsonIgnore
    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Transient @JsonIgnore
    public void removeSubscriberById(String sid) {
        subscribers = subscribers.stream()
                .filter(subscriber -> !Objects.equals(subscriber.getId(), sid))
                .collect(Collectors.toCollection(CopyOnWriteArraySet::new));
    }

}
