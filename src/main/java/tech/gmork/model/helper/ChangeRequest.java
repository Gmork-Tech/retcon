package tech.gmork.model.helper;

import lombok.Data;
import tech.gmork.model.dtos.Subscriber;
import tech.gmork.model.enums.ChangeType;

import java.util.HashSet;
import java.util.Set;

@Data
public class ChangeRequest {
    private ChangeType changeType;
    private Set<Subscriber> subscribers = new HashSet<>();

    public static ChangeRequest fromCompliance(Compliance compliance) {
        var req = new ChangeRequest();
        if (compliance.aboveTarget()) {
            // In case auto-scaling in an environment like K8s increases the number of subscribers at some point.
            req.setChangeType(ChangeType.DECREMENT);
            req.setSubscribers(compliance.getFirstXCompliantSubscribers(compliance.diffCompliantToTarget()));
        } else if (compliance.belowTarget()) {
            req.setChangeType(ChangeType.INCREMENT);
            req.setSubscribers(compliance.getFirstXNonCompliantSubscribers(compliance.diffCompliantToTarget()));
        } else {
            req.setChangeType(ChangeType.HOLD);
        }
        return req;
    }
}
