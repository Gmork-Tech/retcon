package tech.gmork.control;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import tech.gmork.model.entities.Application;
import tech.gmork.model.dtos.Subscriber;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static tech.gmork.model.events.InternalEvents.NEW_SUBSCRIBER_EVENT;

@ServerEndpoint("/ws/{id}")
@ApplicationScoped
public class SocketController {

    private static final Logger logger = Logger.getLogger(SocketController.class);
    private static final Map<UUID, Set<Subscriber>> appSubscriberMap = new ConcurrentHashMap<>();

    @Inject
    EventBus bus;

    @OnOpen
    @Transactional
    public void onOpen(Session session, @PathParam("id") UUID id) {
        var maybeApp = Application.<Application>findByIdOptional(id);
        if (maybeApp.isEmpty()) {
            logger.warn("A client attempted to open a connection with an invalid application ID.");
            throw new WebApplicationException("Please provide a valid uuid.", Response.Status.BAD_REQUEST);
        }

        var sub = Subscriber.fromSession(session);
        appSubscriberMap.computeIfAbsent(id, uid -> new HashSet<>()).add(sub);
        bus.publish(NEW_SUBSCRIBER_EVENT, sub);
        logger.info("Opened websocket session for application id: " + maybeApp.get().getName());
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") UUID id) {
        logger.info("onClose> " + id);
        var subs = appSubscriberMap.get(id);

    }

    @OnError
    public void onError(Session session, @PathParam("id") UUID id, Throwable throwable) {
        logger.error("onError> " + id + ": ", throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("id") UUID id) {
        logger.info("onMessage> " + id + ": " + message);
    }


    private void onAppChange(Application app) {

        // Nothing to do if no one is listening
        if (appSubscriberMap.get(app.getId()) == null || appSubscriberMap.get(app.getId()).isEmpty()) {
            return;
        }
        // Nothing to do if no deployments defined
        if (app.getDeployments() == null || app.getDeployments().isEmpty()) {
            return;
        }
        app.getDeployments().forEach(deployment -> {
            // Determine if we are dealing with simple config props or not.
            if (!deployment.getKind().isDependent()) {
                // If so, push the props to every consumer
                appSubscriberMap.get(app.getId()).forEach(subscriber -> {

                });
            } else {

            }
        });
    }

    @ConsumeEvent(value = NEW_SUBSCRIBER_EVENT, blocking = true)
    public void onNewSubscriber(Subscriber subscriber) {

    }

}
