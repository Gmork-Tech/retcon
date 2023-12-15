package tech.gmork.control;

import io.quarkus.logging.Log;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import tech.gmork.model.entities.Application;
import tech.gmork.model.dtos.Subscriber;

import java.util.*;

@ServerEndpoint("/ws/{id}")
@ApplicationScoped
public class SocketController {

    @Inject
    EventBus bus;

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        var uuid = tryParse(id);
        Application.<Application>findByIdOptional(uuid)
                .ifPresentOrElse(app -> {
                    var sub = Subscriber.fromSession(session);
                    app.addSubscriber(sub);
                    app.getDeployments().forEach(deployment -> deployment.deploy()
                            .subscribe()
                            .with(dep -> Log.info("Subscriber onOpen event triggered successful evaluation " +
                                            "of deployment " + deployment.getName() + " for application " +
                                            app.getName()),
                                    fail -> Log.warn("Subscriber onOpen event failed evaluation " +
                                            "of deployment " + deployment.getName() + " for application " +
                                            app.getName(), fail)));
                    },
                () -> {
                    throw new WebApplicationException("Please provide a valid UUID.", Response.Status.BAD_REQUEST);
                }
        );
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String id) {
        var uuid = tryParse(id);
        Application.<Application>findByIdOptional(uuid)
                .ifPresentOrElse(app -> {
                    app.removeSubscriberById(session.getId());
                    app.getDeployments().forEach(deployment -> deployment.deploy()
                            .subscribe()
                            .with(dep -> Log.info("Subscriber onClose event triggered successful evaluation " +
                                            "of deployment " + deployment.getName() + " for application " +
                                            app.getName()),
                                    fail -> Log.warn("Subscriber onClose event failed evaluation " +
                                            "of deployment " + deployment.getName() + " for application " +
                                            app.getName(), fail)));
                    },
                () -> {
                    throw new WebApplicationException("Please provide a valid UUID.", Response.Status.BAD_REQUEST);
                }
        );
    }

    @OnError
    public void onError(Session session, @PathParam("id") String id, Throwable throwable) {
        Log.error("onError> " + id + ": ", throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("id") String id) {
        Log.info("onMessage> " + id + ": " + message);
    }

    private UUID tryParse(String id) {
        try {
            return UUID.fromString(id);
        }
        catch (Exception e) {
            Log.warn("A client attempted to open a connection with an invalid application ID.");
            throw new WebApplicationException("Please provide a valid UUID.", Response.Status.BAD_REQUEST);
        }
    }

}
