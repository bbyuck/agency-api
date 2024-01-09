package com.ndm.core.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuerydslConfig {

    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory query() {
        return new JPAQueryFactory(em);
    }
}
