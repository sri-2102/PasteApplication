package com.example.paste.service;

import com.example.paste.model.Paste;
import com.example.paste.repo.PasteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class PasteService {
    private final PasteRepository repo;

    public PasteService(PasteRepository repo) {
        this.repo = repo;
    }

    public Paste save(Paste p) {
        return repo.save(p);
    }

    @Transactional
    public Optional<Paste> fetchAndConsumeView(String id, Instant now) {
        Optional<Paste> opt = repo.findById(id);
        if (!opt.isPresent()) return Optional.empty();
        Paste p = opt.get();
        // check TTL
        if (p.getExpiresAt() != null && now.isAfter(p.getExpiresAt())) {
            return Optional.empty();
        }
        // check views
        if (p.getRemainingViews() != null) {
            if (p.getRemainingViews() <= 0) return Optional.empty();
            p.setRemainingViews(Math.max(0, p.getRemainingViews() - 1));
            repo.save(p);
        }
        return Optional.of(p);
    }

    @Transactional(readOnly = true)
    public Optional<Paste> fetchWithoutConsume(String id, Instant now) {
        Optional<Paste> opt = repo.findById(id);
        if (!opt.isPresent()) return Optional.empty();
        Paste p = opt.get();
        if (p.getExpiresAt() != null && now.isAfter(p.getExpiresAt())) return Optional.empty();
        if (p.getRemainingViews() != null && p.getRemainingViews() <= 0) return Optional.empty();
        return Optional.of(p);
    }
}
