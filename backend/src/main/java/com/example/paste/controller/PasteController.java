package com.example.paste.controller;

import com.example.paste.model.Paste;
import com.example.paste.service.PasteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@RestController
public class PasteController {
    private final PasteService service;

    public PasteController(PasteService service) {
        this.service = service;
    }

    private Instant determineNow(HttpServletRequest req) {
        String testMode = System.getenv("TEST_MODE");
        if ("1".equals(testMode)) {
            String header = req.getHeader("x-test-now-ms");
            if (header != null) {
                try {
                    long ms = Long.parseLong(header);
                    return Instant.ofEpochMilli(ms);
                } catch (NumberFormatException ignored) {}
            }
        }
        return Instant.now();
    }

    @GetMapping(path = "/api/healthz", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> health() {
        try {
            // quick DB access check: try to fetch a non-existing id
            service.fetchWithoutConsume("-not-used-", Instant.now());
            return ResponseEntity.ok().body(java.util.Collections.singletonMap("ok", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Collections.singletonMap("ok", false));
        }
    }

    @PostMapping(path = "/api/pastes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPaste(@RequestBody CreatePasteRequest body, HttpServletRequest req) {
        String content = body.getContent();
        if (!StringUtils.hasText(content)) {
            return badRequest("content is required and must be a non-empty string");
        }
        Integer ttl = body.getTtlSeconds();
        Integer maxViews = body.getMaxViews();
        if (ttl != null && ttl < 1) return badRequest("ttl_seconds must be >= 1");
        if (maxViews != null && maxViews < 1) return badRequest("max_views must be >= 1");

        Paste p = new Paste();
        String id = UUID.randomUUID().toString();
        p.setId(id);
        p.setContent(content);
        Instant now = determineNow(req);
        p.setCreatedAt(now);
        if (ttl != null) {
            p.setExpiresAt(now.plusSeconds(ttl));
        }
        if (maxViews != null) {
            p.setMaxViews(maxViews);
            p.setRemainingViews(maxViews);
        }
        service.save(p);

        String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/p/").path(id).toUriString();
        return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of("id", id, "url", url));
    }

    private ResponseEntity<java.util.Map<String,Object>> badRequest(String msg) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of("error", msg));
    }

    @GetMapping(path = "/api/pastes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPasteApi(@PathVariable String id, HttpServletRequest req) {
        Instant now = determineNow(req);
        Optional<Paste> opt = service.fetchAndConsumeView(id, now);
        if (!opt.isPresent()) return notFound();
        Paste p = opt.get();
        PasteDto dto = new PasteDto();
        dto.setContent(p.getContent());
        dto.setRemainingViews(p.getRemainingViews());
        dto.setExpiresAt(p.getExpiresAt() == null ? null : DateTimeFormatter.ISO_INSTANT.format(p.getExpiresAt().atOffset(ZoneOffset.UTC)));
        return ResponseEntity.ok(dto);
    }

    private ResponseEntity<java.util.Map<String,Object>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.Map.of("error", "not found"));
    }

    @GetMapping(path = "/p/{id}")
    public ResponseEntity<String> viewHtml(@PathVariable String id, HttpServletRequest req) {
        Instant now = determineNow(req);
        Optional<Paste> opt = service.fetchAndConsumeView(id, now);
        if (!opt.isPresent()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        Paste p = opt.get();
        String safe = HtmlUtils.htmlEscape(p.getContent());
        String html = "<!doctype html><html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><title>Paste " + id + "</title></head><body><pre>" + safe + "</pre></body></html>";
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }
}
