package de.MCmoderSD.cloudflare.enums;

import java.io.Serializable;

public enum RecordType implements Serializable {

    // Values
    A,
    AAAA,
    CAA,
    CERT,
    CNAME,
    DNSKEY,
    DS,
    HTTPS,
    LOC,
    MX,
    NAPTR,
    NS,
    PTR,
    SMIMEA,
    SRV,
    SSHFP,
    SVCB,
    TLSA,
    TXT,
    URI;

    // Methods
    public boolean isProxiable() {
        return isProxiable(this);
    }

    public static boolean isProxiable(RecordType type) {
        return type == A || type == AAAA || type == CNAME;
    }

    public static RecordType fromString(String type) {
        if (type == null || type.isBlank()) throw new IllegalArgumentException("Type cannot be null or blank");
        for (var recordType : RecordType.values()) if (recordType.name().equalsIgnoreCase(type)) return recordType;
        throw new IllegalArgumentException("Unknown record type: " + type);
    }
}