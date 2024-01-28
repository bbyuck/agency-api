package com.ndm.core.domain.file.repository;


import com.ndm.core.entity.Photo;
import com.ndm.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByOwner(User owner);
}
