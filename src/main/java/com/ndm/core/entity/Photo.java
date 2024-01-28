package com.ndm.core.entity;

import com.ndm.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TableGenerator(
        name = "photo_seq_generator",
        table = "ddu_seq",
        pkColumnName = "sequence_name",
        pkColumnValue = "photo_seq",
        allocationSize = 100
)
@Table(name = "photo")
public class Photo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "photo_seq_generator")
    @Column(name = "photo_id", unique = true, nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", unique = true, nullable = false)
    private User owner;

    @Column(name = "file_path", length = 250)
    private String filePath;

    @Transient
    public static final List<String> EXTENTIONS = Arrays.asList("png", "jpg", "jpeg");

}
