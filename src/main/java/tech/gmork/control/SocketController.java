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
import tech.gmork.model.entities.Deployment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static tech.gmork.model.events.InternalEvents.DEPLOYMENT_CHANGE_EVENT;
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
    public void onOpen(Session session, @PathParam("id") String id) {
        var uuid = tryParse(id);
        var maybeApp = Application.<Application>findByIdOptional(uuid);
        if (maybeApp.isEmpty()) {
            logger.warn("A client attempted to open a connection with an invalid application ID.");
            throw new WebApplicationException("Please provide a valid UUID.", Response.Status.BAD_REQUEST);
        }

        var sub = Subscriber.fromSession(session);
        appSubscriberMap.computeIfAbsent(uuid, uid -> new HashSet<>()).add(sub);
        logger.info("Opened websocket session for application id: " + maybeApp.get().getName());
        // Publish a message on the event bus to trigger initial config send
        bus.publish(NEW_SUBSCRIBER_EVENT, sub);
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String id) {
        var uuid = tryParse(id);
        // Get all subscribers for the given application, check that the set is not already empty
        var subs = appSubscriberMap.get(uuid);
        if (subs == null || subs.isEmpty()) {
            logger.warn("A subscriber disconnected while the subscriber map was empty. This should not be possible.");
            return;
        }

        // Replace the subscriber set for the given application with a new one, sans the disconnected subscriber
        var newSubs = subs.stream()
                .filter(sub -> !Objects.equals(sub.getSession().getId(), session.getId()))
                .collect(Collectors.toSet());
        appSubscriberMap.put(uuid, newSubs);

        // Log the action
        logger.info("Closed a connection with a subscriber for app id: " + uuid);
    }

    @OnError
    public void onError(Session session, @PathParam("id") String id, Throwable throwable) {
        logger.error("onError> " + id + ": ", throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("id") String id) {
        logger.info("onMessage> " + id + ": " + message);
    }


    @ConsumeEvent(value = DEPLOYMENT_CHANGE_EVENT, blocking = true)
    private void onDeploymentChange(Deployment deployment) {
        // Get the target application from the deployment
        var appId = deployment.getApplication().getId();

        // Nothing to do if no one is listening
        if (!appSubscriberMap.containsKey(appId) || appSubscriberMap.get(appId).isEmpty()) {
            return;
        }

        // If this is a normal deployment
        if (!deployment.getKind().isDependent()) {
            appSubscriberMap.get(appId).forEach(subscriber -> {

            });
        } else {

        }
    }
    @ConsumeEvent(value = NEW_SUBSCRIBER_EVENT, blocking = true)
    public void onNewSubscriber(Subscriber subscriber) {

    }

    private UUID tryParse(String id) {
        try {
            return UUID.fromString(id);
        }
        catch (Exception e) {
            logger.warn("A client attempted to open a connection with an invalid application ID.");
            throw new WebApplicationException("Please provide a valid UUID.", Response.Status.BAD_REQUEST);
        }
    }

}
