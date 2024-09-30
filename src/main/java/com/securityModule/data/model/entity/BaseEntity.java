package com.securityModule.data.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EnableJpaAuditing
public abstract class BaseEntity {

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime regDt;

    @LastModifiedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime updateDt;
}
