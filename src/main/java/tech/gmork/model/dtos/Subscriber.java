package tech.gmork.model.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.websocket.Session;
import lombok.Data;

import tech.gmork.model.Validatable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscriber implements Validatable {
    private Session session;
    private Map<Long, Long> versionedDeployments = new ConcurrentHashMap<>();
    private String hostName;
    private String ipAddress;

    @Override
    public void validate() {

    }

    public static Subscriber fromSession(Session session) {
        var sub = new Subscriber();
        sub.setSession(session);
        return sub;
    }
}
