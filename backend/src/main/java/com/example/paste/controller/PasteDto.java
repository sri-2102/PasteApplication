package com.example.paste.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class PasteDto {
    @Schema(description = "Paste content")
    private String content;

    @JsonProperty("remaining_views")
    @Schema(description = "Remaining views (null if unlimited)")
    private Integer remainingViews;

    @JsonProperty("expires_at")
    @Schema(description = "Expiry timestamp in ISO-8601 UTC (null if none)")
    private String expiresAt;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getRemainingViews() { return remainingViews; }
    public void setRemainingViews(Integer remainingViews) { this.remainingViews = remainingViews; }
    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
}
