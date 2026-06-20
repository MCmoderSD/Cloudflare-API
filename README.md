# Cloudflare-API

## Description
A simple Java library to interact with the Cloudflare API, focusing on DNS record management.

## Features
- List DNS records
- Add DNS records (A, AAAA, CNAME, TXT)
- Update DNS records
- Delete DNS records

## Usage

### Maven
Make sure you have my Sonatype Nexus OSS repository added to your `pom.xml` file:
```xml
<repositories>
    <repository>
        <id>Nexus</id>
        <name>Sonatype Nexus</name>
        <url>https://mcmodersd.de/nexus/repository/maven-releases/</url>
    </repository>
</repositories>
```
Add the dependency to your `pom.xml` file:
```xml
<dependency>
    <groupId>de.MCmoderSD</groupId>
    <artifactId>Cloudflare-API</artifactId>
    <version>1.1.5</version>
</dependency>
```

### Example
```java
import de.MCmoderSD.cloudflare.core.CloudflareClient;
import de.MCmoderSD.cloudflare.objects.DnsRecord;

import static de.MCmoderSD.cloudflare.enums.RecordType.TXT;

void main() {

    // Cloudflare Credentials
    var zoneId = "YOUR_ZONE_ID";
    var apiToken = "YOUR_API";

    // Initialize Cloudflare Client
    var client = new CloudflareClient(zoneId, apiToken);

    // Get DNS Records
    var records = client.getRecords();

    // List DNS Records
    for (var record : records) {
        IO.println("ID: " + record.getId());
        IO.println("Name: " + record.getName());
        IO.println("Type: " + record.getType());
        IO.println("Content: " + record.getContent());
        IO.println("Proxiable: " + record.isProxiable());
        IO.println("Proxied: " + record.isProxied());
        IO.println("TTL: " + record.getTtl() + " seconds");
        IO.println("Comment: " + record.getComment());
        IO.println("Created On: " + record.getCreated());
        IO.println("Modified On: " + record.getModified());
        IO.println("--------------------------------");
    }

    // Find base domain
    var baseDomain = records.stream()
            .map(DnsRecord::getName)
            .min(Comparator.comparingInt(String::length))
            .orElseThrow();

    var recordExists = records.stream().anyMatch(record -> record.getType().equals(TXT) && record.getName().equals("hello-world." + baseDomain));

    if (recordExists) {

        IO.println("\nRecord already exists.");
        IO.println("Deleting record...");

        // Find and delete the record
        var recordToDelete = records.stream()
                .filter(record -> record.getType().equals(TXT) && record.getName().equals("hello-world." + baseDomain))
                .findFirst()
                .orElseThrow();

        // Delete the record
        var success = client.deleteRecord(recordToDelete);

        // Output result
        if (success) IO.println("Deleted record 'hello-world." + baseDomain + "' of type TXT.");
        else IO.println("Failed to delete record 'hello-world." + baseDomain + "'.");

    } else {

        IO.println("\nRecord does not exist.");
        IO.println("Creating record...");

        // Create a new TXT record
        var record = DnsRecord.builder(TXT)
                .name("hello-world." + baseDomain)
                .content("This is a test record.")
                .buildJson();

        // Create the record
        client.createRecord(record);

        // Output result
        IO.println("Created record 'hello-world." + baseDomain + "' of type TXT.");
    }
}
```