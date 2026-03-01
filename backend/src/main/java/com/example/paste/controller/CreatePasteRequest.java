package com.example.paste.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class CreatePasteRequest {
    @Schema(description = "Paste content", required = true)
    private String content;

    @JsonProperty("ttl_seconds")
    @Schema(description = "Time-to-live in seconds (optional)")
    private Integer ttlSeconds;

    @JsonProperty("max_views")
    @Schema(description = "Maximum number of views (optional)")
    private Integer maxViews;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getTtlSeconds() { return ttlSeconds; }
    public void setTtlSeconds(Integer ttlSeconds) { this.ttlSeconds = ttlSeconds; }
    public Integer getMaxViews() { return maxViews; }
    public void setMaxViews(Integer maxViews) { this.maxViews = maxViews; }
}
