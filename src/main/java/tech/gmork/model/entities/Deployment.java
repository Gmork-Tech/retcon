package tech.gmork.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.gmork.model.Validatable;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Deployment extends PanacheEntity implements Validatable {
    private String name;

    @OneToOne
    private DeploymentKind kind;

    @OneToMany(mappedBy = "deployment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ConfigProp> props;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationId")
    private Application application;

    @Override
    public void validate() {

    }
}
