package de.MCmoderSD.cloudflare.objects;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

@SuppressWarnings("unused")
public class ModifiedRecord extends DnsRecord {

    // Modified attributes
    private String modifiedContent;
    private Boolean modifiedProxied;
    private Integer modifiedTtl;
    private String modifiedComment;

    // Constructor
    public ModifiedRecord(DnsRecord dnsRecord) {
        super(dnsRecord);
    }

    // Create JSON object for modified content
    public ObjectNode getModifiedContent() {
        ObjectNode node = new ObjectMapper().createObjectNode()
                .put("id", getId())
                .put("name", getName())
                .put("type", getType().toString())
                .put("content", getContent())
                .put("proxied", isProxied())
                .put("ttl", getTtl());
        if (getComment() != null) node.put("comment", getComment());
        return node;
    }

    // Setters for modified attributes
    public void modifyContent(String content) {
        if (content == null) throw new IllegalArgumentException("Content cannot be null");
        if (content.isBlank()) throw new IllegalArgumentException("Content cannot be blank");
        modifiedContent = content;
    }

    public void modifyProxy(boolean proxied) {
        if (proxiable) modifiedProxied = proxied;
        else throw new IllegalArgumentException("Record is not proxiable");
    }

    public void modifyTtl(int ttl) {
        if (ttl < 60) throw new IllegalArgumentException("TTL must be at least 1 minute (60 seconds)");
        if (ttl > 86400) throw new IllegalArgumentException("TTL cannot be more than 1 day (86400 seconds)");
        modifiedTtl = ttl;
    }

    public void modifyComment(String comment) {
        modifiedComment = comment;
    }

    // Overridden getters to return modified values if set
    @Override
    public String getContent() {
        return modifiedContent == null ? super.getContent() : content.contains(" ") && !content.startsWith("\"") && !content.endsWith("\"") ? "\"" + modifiedContent + "\"" : modifiedContent;
    }

    @Override
    public boolean isProxied() {
        return modifiedProxied == null ? super.isProxied() : modifiedProxied;
    }

    @Override
    public int getTtl() {
        return modifiedTtl == null ? super.getTtl() : modifiedTtl;
    }

    @Override
    public long getTtl(TimeUnit timeUnit) {
        if (timeUnit == null) throw new IllegalArgumentException("TimeUnit cannot be null");
        return timeUnit.convert(modifiedTtl == null ? super.getTtl() : modifiedTtl, SECONDS);
    }

    @Override
    public String getComment() {
        return modifiedComment == null ? super.getComment() : modifiedComment;
    }
}