package com.tracker.moniter;

import org.springframework.stereotype.Service;

@Service
public interface MonitoringService {
    void recordEntityLoad(String entityName, Object entityId);

    void recordCollectionInitialization(String ownerEntityName, String childEntityName, String role);
}
