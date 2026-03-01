package com.example.paste.repo;

import com.example.paste.model.Paste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface PasteRepository extends JpaRepository<Paste, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Paste> findById(String id);
}
