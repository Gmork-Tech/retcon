package tech.gmork.model.dtos;

import jakarta.websocket.Session;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import tech.gmork.model.entities.Deployment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Subscriber {

    // This is the websocket session for the client
    private Session session;

    // Key is deployment id and value is hashcode of said deployment
    @Setter(AccessLevel.NONE)
    private Map<Long, Integer> versionedDeployments = new ConcurrentHashMap<>();

    // Extra information the subscriber can provide after initial connection that allows for manual deployments
    private String hostName;
    private String ipAddress;

    public String getId() {
        return session.getId();
    }

    public void updateVersionedDeployment(long id, int hashcode) {
        versionedDeployments.put(id, hashcode);
    }

    public void removeDeployment(long id) {
        versionedDeployments.remove(id);
    }

    public boolean hasDeployment(long id) {
        return versionedDeployments.containsKey(id);
    }

    public void sendDeploymentChangeEvent(Deployment deployment) {
        session.getAsyncRemote().sendObject(deployment);
    }

    public static Subscriber fromSession(Session session) {
        var sub = new Subscriber();
        sub.setSession(session);
        return sub;
    }
}
