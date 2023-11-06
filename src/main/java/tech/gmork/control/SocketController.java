package tech.gmork.control;

import io.quarkus.logging.Log;
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
import tech.gmork.model.entities.Application;
import tech.gmork.model.dtos.Subscriber;

import java.util.*;

import static tech.gmork.model.events.InternalEvents.NEW_SUBSCRIBER_EVENT;

@ServerEndpoint("/ws/{id}")
@ApplicationScoped
public class SocketController {

    @Inject
    EventBus bus;

    @OnOpen
    @Transactional
    public void onOpen(Session session, @PathParam("id") String id) {
        var uuid = tryParse(id);
        var maybeApp = Application.<Application>findByIdOptional(uuid);
        if (maybeApp.isEmpty()) {
            Log.warn("A client attempted to open a connection with an invalid application ID.");
            throw new WebApplicationException("Please provide a valid UUID.", Response.Status.BAD_REQUEST);
        }

        var sub = Subscriber.fromSession(session);
        maybeApp.get().addSubscriber(sub);
        Log.info("Opened websocket session for application id: " + maybeApp.get().getName());
        // Publish a message on the event bus to trigger initial config send
        bus.publish(NEW_SUBSCRIBER_EVENT, sub);
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String id) {
        var uuid = tryParse(id);
        var subId = session.getId();
        var maybeApp = Application.<Application>findByIdOptional(uuid);
        if (maybeApp.isEmpty()) {
            Log.warn("A client attempted to close a connection with an invalid application ID.");
            throw new WebApplicationException("Please provide a valid UUID.", Response.Status.BAD_REQUEST);
        }
        maybeApp.get().removeSubscriberById(subId);
        // Log the action
        Log.info("Closed a connection with a subscriber for app id: " + uuid);
    }

    @OnError
    public void onError(Session session, @PathParam("id") String id, Throwable throwable) {
        Log.error("onError> " + id + ": ", throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("id") String id) {
        Log.info("onMessage> " + id + ": " + message);
    }

    @ConsumeEvent(value = NEW_SUBSCRIBER_EVENT, blocking = true)
    public void onNewSubscriber(Subscriber subscriber) {

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
