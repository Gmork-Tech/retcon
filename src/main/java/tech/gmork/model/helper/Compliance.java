package tech.gmork.model.helper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import tech.gmork.model.dtos.Subscriber;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with", builderMethodName = "newBuilder")
public class Compliance {
    private Set<Subscriber> nonCompliantSubscribers;
    private Set<Subscriber> compliantSubscribers;
    private int currentNumCompliantSubscribers;
    private int targetNumCompliantSubscribers;

    public boolean aboveTarget() {
        return currentNumCompliantSubscribers > targetNumCompliantSubscribers;
    }

    public boolean belowTarget() {
        return currentNumCompliantSubscribers < targetNumCompliantSubscribers;
    }

    public boolean onTarget() {
        return currentNumCompliantSubscribers == targetNumCompliantSubscribers;
    }

    public int diffCompliantToTarget() {
        return Math.abs(targetNumCompliantSubscribers - currentNumCompliantSubscribers);
    }

    @JsonIgnore
    public Set<Subscriber> getFirstXCompliantSubscribers(int limit) {
        return subsetSubscribers(limit, compliantSubscribers);
    }

    @JsonIgnore
    public Set<Subscriber> getFirstXNonCompliantSubscribers(int limit) {
        return subsetSubscribers(limit, nonCompliantSubscribers);
    }

    private Set<Subscriber> subsetSubscribers(int limit, Set<Subscriber> subscribers) {
        return subscribers.stream()
                .limit(limit)
                .collect(Collectors.toSet());
    }

}
