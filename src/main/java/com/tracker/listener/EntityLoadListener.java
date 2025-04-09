package com.tracker.listener;

import com.tracker.moniter.MonitoringService;
import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.event.spi.InitializeCollectionEvent;
import org.hibernate.event.spi.InitializeCollectionEventListener;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EntityLoadListener implements PostLoadEventListener, InitializeCollectionEventListener {

    private static final ThreadLocal<Map<String, Object>> LOADED_ENTITIES = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, Object>> INITIALIZED_COLLECTIONS = ThreadLocal.withInitial(HashMap::new);
    private static final Map<String, RelationshipInfo> ENTITY_RELATIONSHIPS = new ConcurrentHashMap<>();

    private final MonitoringService monitoringService;

    public EntityLoadListener(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @Override
    public void onPostLoad(PostLoadEvent postLoadEvent) {
        EntityPersister persister = postLoadEvent.getPersister();
        String entityName = persister.getEntityName();
        Object entityId = postLoadEvent.getId();
        Object entity = postLoadEvent.getEntity();

        String entityKey = entityName + "#" + entityId;
        Map<String, Object> loadedEntities = LOADED_ENTITIES.get();
        loadedEntities.put(entityKey, entity);

        monitoringService.recordEntityLoad(entityName, entityId);
    }

    @Override
    public void onInitializeCollection(InitializeCollectionEvent collectionEvent) throws HibernateException {
        PersistentCollection<?> persistentCollection = collectionEvent.getCollection();
        String role = persistentCollection.getRole(); // ex) com.tracker.aop.MethodCallInfo

        String ownerEntityName = collectionEvent.getAffectedOwnerEntityName();

        String childEntityName = extractChildEntityNameFromRole(role);

        RelationshipInfo relationshipInfo = ENTITY_RELATIONSHIPS.computeIfAbsent(
                role, k -> new RelationshipInfo(ownerEntityName, childEntityName, role)
        );
        relationshipInfo.incrementUsageCount();

        monitoringService.recordCollectionInitialization(ownerEntityName, childEntityName, role);

        Map<String, Object> initializedCollections = INITIALIZED_COLLECTIONS.get();
        initializedCollections.put(role, persistentCollection);
    }

    private String extractChildEntityNameFromRole(String role) {
        if (role == null || role.isEmpty()) {
            return "Unknown";
        }

        int lastDotIndex = role.lastIndexOf('.');
        if (lastDotIndex <= 0) {
            return "Unknown";
        }

        String collectionFieldName = role.substring(lastDotIndex + 1);

        String ownerClassName = role.substring(0, lastDotIndex);

        return "Collection<" + ownerClassName + "." + collectionFieldName + ">";
    }

    public static Map<String, Object> getLoadedEntities() {
        return LOADED_ENTITIES.get();
    }

    public static Map<String, Object> getInitializedCollections() {
        return INITIALIZED_COLLECTIONS.get();
    }

    public static Map<String, RelationshipInfo> getEntityRelationships() {
        return ENTITY_RELATIONSHIPS;
    }

    public static void clearThreadLocals() {
        LOADED_ENTITIES.remove();
        INITIALIZED_COLLECTIONS.remove();
    }
}