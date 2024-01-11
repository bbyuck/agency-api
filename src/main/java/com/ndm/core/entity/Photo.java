package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "photo")
public class Photo extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "photo_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User owner;

    @Column(name = "file_path", length = 250)
    private String filePath;

}
