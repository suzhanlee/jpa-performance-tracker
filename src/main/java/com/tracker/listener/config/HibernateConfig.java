package com.tracker.listener.config;

import com.tracker.listener.EntityLoadListener;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Autowired
    private EntityLoadListener entityLoadListener;

    @Autowired
    private EntityManagerFactory emf;

    @Bean
    public Object postProcessEntityManagerFactory() {
        SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        registry.getEventListenerGroup(EventType.POST_LOAD)
                .appendListener(entityLoadListener);

        registry.getEventListenerGroup(EventType.INIT_COLLECTION)
                .appendListener(entityLoadListener);

        return new Object();
    }
}
