package tech.gmork.control;

import tech.gmork.model.dtos.Subscriber;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SubscriberCache {
    private static final Map<UUID, Set<Subscriber>> subscriberMap = new ConcurrentHashMap<>();

    public static Set<Subscriber> getSubscribersByAppKey(UUID appKey) {
        return subscriberMap.getOrDefault(appKey, new HashSet<>());
    }

    public static void addSubscriber(UUID appKey, Subscriber subscriber) {
        subscriberMap
                .computeIfAbsent(appKey, key -> new HashSet<>())
                .add(subscriber);
    }

    public static void removeSubscriber(UUID appKey, Subscriber subscriber) {
        subscriberMap
                .computeIfAbsent(appKey, key -> new HashSet<>())
                .remove(subscriber);
    }

}
