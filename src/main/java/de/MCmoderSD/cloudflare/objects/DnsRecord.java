package de.MCmoderSD.cloudflare.objects;

import de.MCmoderSD.cloudflare.enums.RecordType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static de.MCmoderSD.cloudflare.enums.RecordType.*;
import static java.util.concurrent.TimeUnit.SECONDS;

@SuppressWarnings("unused")
public class DnsRecord {

    // Attributes
    protected final String id;
    protected final String name;
    protected final RecordType type;
    protected final String content;
    protected final boolean proxiable;
    protected final boolean proxied;
    protected final int ttl;
    protected final String comment;
    protected final Timestamp created;
    protected final Timestamp modified;

    // Constructor
    public DnsRecord(JsonNode dnsRecord) {

        // Check dnsRecord
        if (dnsRecord == null || dnsRecord.isNull() || dnsRecord.isEmpty()) throw new IllegalArgumentException("DNS record JSON cannot be null or empty");
        if (!dnsRecord.has("id") || dnsRecord.get("id").isNull() || !dnsRecord.get("id").isString()) throw new IllegalArgumentException("DNS record ID is missing or invalid");
        if (!dnsRecord.has("name") || dnsRecord.get("name").isNull() || !dnsRecord.get("name").isString()) throw new IllegalArgumentException("DNS record name is missing or invalid");
        if (!dnsRecord.has("type") || dnsRecord.get("type").isNull() || !dnsRecord.get("type").isString()) throw new IllegalArgumentException("DNS record type is missing or invalid");
        if (!dnsRecord.has("content") || dnsRecord.get("content").isNull() || !dnsRecord.get("content").isString()) throw new IllegalArgumentException("DNS record content is missing or invalid");
        if (!dnsRecord.has("proxiable") || dnsRecord.get("proxiable").isNull() || !dnsRecord.get("proxiable").isBoolean()) throw new IllegalArgumentException("DNS record proxiable flag is missing or invalid");
        if (!dnsRecord.has("proxied") || dnsRecord.get("proxied").isNull() || !dnsRecord.get("proxied").isBoolean()) throw new IllegalArgumentException("DNS record proxied flag is missing or invalid");
        if (!dnsRecord.has("ttl") || dnsRecord.get("ttl").isNull() || !dnsRecord.get("ttl").isInt()) throw new IllegalArgumentException("DNS record TTL is missing or invalid");
        if (!dnsRecord.has("created_on") || dnsRecord.get("created_on").isNull() || !dnsRecord.get("created_on").isString()) throw new IllegalArgumentException("DNS record creation timestamp is missing or invalid");
        if (!dnsRecord.has("modified_on") || dnsRecord.get("modified_on").isNull() || !dnsRecord.get("modified_on").isString()) throw new IllegalArgumentException("DNS record modification timestamp is missing or invalid");

        // Set attributes
        id = dnsRecord.get("id").asString();
        name = dnsRecord.get("name").asString();
        type = RecordType.fromString(dnsRecord.get("type").asString());
        content = dnsRecord.get("content").asString();
        proxiable = dnsRecord.get("proxiable").asBoolean();
        proxied = dnsRecord.get("proxied").asBoolean();
        ttl = dnsRecord.get("ttl").asInt();

        // Optional comment
        if (dnsRecord.has("comment") && !dnsRecord.get("comment").isNull() && dnsRecord.get("comment").isString()) comment = dnsRecord.get("comment").asString();
        else comment = null;

        // Parse timestamps
        created = Timestamp.valueOf(dnsRecord.get("created_on").asString().replaceAll("T", " ").replaceAll("Z", ""));
        modified = Timestamp.valueOf(dnsRecord.get("modified_on").asString().replaceAll("T", " ").replaceAll("Z", ""));
    }

    // Copy constructor
    public DnsRecord(DnsRecord dnsRecord) {

        // Check dnsRecord
        if (dnsRecord == null) throw new IllegalArgumentException("Original record cannot be null");

        // Copy attributes
        id = dnsRecord.id;
        name = dnsRecord.name;
        type = dnsRecord.type;
        content = dnsRecord.content;
        proxiable = dnsRecord.proxiable;
        proxied = dnsRecord.proxied;
        ttl = dnsRecord.ttl;
        comment = dnsRecord.comment;
        created = dnsRecord.created;
        modified = dnsRecord.modified;
    }

    public boolean equals(DnsRecord dnsRecord) {
        if (dnsRecord == null) throw new IllegalArgumentException("DnsRecord cannot be null");
        boolean equals = id.equals(dnsRecord.id);
        equals &= name.equals(dnsRecord.name);
        equals &= type == dnsRecord.type;
        equals &= content.equals(dnsRecord.content);
        equals &= proxiable == dnsRecord.proxiable;
        equals &= proxied == dnsRecord.proxied;
        equals &= ttl == dnsRecord.ttl;
        equals &= Objects.equals(comment, dnsRecord.comment);
        equals &= created.equals(dnsRecord.created);
        equals &= modified.equals(dnsRecord.modified);
        return equals;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RecordType getType() {
        return type;
    }

    public String getContent() {
        return content.contains(" ") && !content.startsWith("\"") && !content.endsWith("\"") ? "\"" + content + "\"" : content;
    }

    public boolean isProxiable() {
        return proxiable;
    }

    public boolean isProxied() {
        return proxied;
    }

    public int getTtl() {
        return ttl;
    }

    public long getTtl(TimeUnit timeUnit) {
        if (timeUnit == null) throw new IllegalArgumentException("TimeUnit cannot be null");
        return timeUnit.convert(ttl, SECONDS);
    }

    public String getComment() {
        return comment;
    }

    public Timestamp getCreated() {
        return created;
    }

    public Timestamp getModified() {
        return modified;
    }

    public static Builder builder(RecordType type) {
        return new Builder(type);
    }

    public static class Builder {

        // Attributes
        private final RecordType type;

        private String name;
        private String content;
        private boolean proxied;
        private int ttl;
        private String comment;

        // Constructor
        public Builder(RecordType type) {

            // Null check
            if (type == null) throw new IllegalArgumentException("Type cannot be null");

            // Check unsupported types
            if (type == CAA)    throw new RuntimeException("CAA records are currently not implemented");
            if (type == CERT)   throw new RuntimeException("CERT records are currently not implemented");
            if (type == DNSKEY) throw new RuntimeException("DNSKEY records are currently not implemented");
            if (type == DS)     throw new RuntimeException("DS records are currently not implemented");
            if (type == HTTPS)  throw new RuntimeException("HTTPS records are currently not implemented");
            if (type == LOC)    throw new RuntimeException("LOC records are currently not implemented");
            if (type == MX)     throw new RuntimeException("MX records are currently not implemented");
            if (type == NAPTR)  throw new RuntimeException("NAPTR records are currently not implemented");
            if (type == SMIMEA) throw new RuntimeException("SMIMEA records are currently not implemented");
            if (type == SRV)    throw new RuntimeException("SRV records are currently not implemented");
            if (type == SSHFP)  throw new RuntimeException("SSHFP records are currently not implemented");
            if (type == SVCB)   throw new RuntimeException("SVCB records are currently not implemented");
            if (type == TLSA)   throw new RuntimeException("TLSA records are currently not implemented");
            if (type == URI)    throw new RuntimeException("URI records are currently not implemented");

            // Set type
            this.type = type;
        }

        public Builder name(String name) {
            if (name == null) throw new IllegalArgumentException("Name cannot be null");
            if (name.isBlank()) throw new IllegalArgumentException("Name cannot be blank");
            if (name.contains(" ")) throw new IllegalArgumentException("Name cannot contain spaces");
            this.name = name;
            return this;
        }

        public Builder content(String content) {

            // Check content
            if (content == null) throw new IllegalArgumentException("Content cannot be null");
            if (content.isBlank()) throw new IllegalArgumentException("Content cannot be blank");

            // Set content
            this.content = content;

            // Return builder
            return this;
        }

        public Builder proxied(boolean proxied) {
            if (proxied && !type.isProxiable()) throw new IllegalArgumentException("Record type " + type + " cannot be proxied");
            this.proxied = proxied;
            return this;
        }

        public Builder ttl(int ttl) {
            if (ttl < 60) throw new IllegalArgumentException("TTL must be at least 1 minute (60 seconds)");
            if (ttl > 86400) throw new IllegalArgumentException("TTL cannot be greater than 1 day (86400 seconds)");
            this.ttl = ttl;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public ObjectNode buildJson() {

            // Check required attributes
            if (name == null) throw new IllegalArgumentException("Name is required");
            if (content == null) throw new IllegalArgumentException("Content is required");
            if (ttl == 0) ttl = 300; // Default TTL 300 seconds

            // Create JSON object
            ObjectNode node = new ObjectMapper().createObjectNode()
                    .put("type", type.toString())
                    .put("name", name)
                    .put("content", content.contains(" ") ? "\"" + content + "\"" : content)
                    .put("ttl", ttl)
                    .put("proxied", proxied);
            if (comment != null) node.put("comment", comment);

            // Return JSON object
            return node;
        }
    }
}