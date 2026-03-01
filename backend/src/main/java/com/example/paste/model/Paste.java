package com.example.paste.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Version;
import java.time.Instant;

@Entity
public class Paste {
    @Id
    private String id;

    @Lob
    @Column(nullable = false)
    private String content;

    private Instant createdAt;

    private Instant expiresAt;

    private Integer maxViews;

    private Integer remainingViews;

    @Version
    private Long version;

    public Paste() {}

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public Integer getMaxViews() { return maxViews; }
    public void setMaxViews(Integer maxViews) { this.maxViews = maxViews; }
    public Integer getRemainingViews() { return remainingViews; }
    public void setRemainingViews(Integer remainingViews) { this.remainingViews = remainingViews; }
}
