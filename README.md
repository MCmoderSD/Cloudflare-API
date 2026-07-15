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
    <version>1.1.6</version>
</dependency>
```

### Example
```java
import de.MCmoderSD.cloudflare.core.CloudflareClient;
import de.MCmoderSD.cloudflare.objects.DnsRecord;

import static de.MCmoderSD.cloudflare.enums.RecordType.TXT;
import static java.lang.IO.println;

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
        println("ID: " + record.getId());
        println("Name: " + record.getName());
        println("Type: " + record.getType());
        println("Content: " + record.getContent());
        println("Proxiable: " + record.isProxiable());
        println("Proxied: " + record.isProxied());
        println("TTL: " + record.getTtl() + " seconds");
        println("Comment: " + record.getComment());
        println("Created On: " + record.getCreated());
        println("Modified On: " + record.getModified());
        println("--------------------------------");
    }

    // Find base domain
    var baseDomain = records.stream()
            .map(DnsRecord::getName)
            .min(Comparator.comparingInt(String::length))
            .orElseThrow();

    var recordExists = records.stream().anyMatch(record -> record.getType().equals(TXT) && record.getName().equals("hello-world." + baseDomain));

    if (recordExists) {

        println("\nRecord already exists.");
        println("Deleting record...");

        // Find and delete the record
        var recordToDelete = records.stream()
                .filter(record -> record.getType().equals(TXT) && record.getName().equals("hello-world." + baseDomain))
                .findFirst()
                .orElseThrow();

        // Delete the record
        var success = client.deleteRecord(recordToDelete);

        // Output result
        if (success) println("Deleted record 'hello-world." + baseDomain + "' of type TXT.");
        else println("Failed to delete record 'hello-world." + baseDomain + "'.");

    } else {

        println("\nRecord does not exist.");
        println("Creating record...");

        // Create a new TXT record
        var record = DnsRecord.builder(TXT)
                .name("hello-world." + baseDomain)
                .content("This is a test record.")
                .buildJson();

        // Create the record
        client.createRecord(record);

        // Output result
        println("Created record 'hello-world." + baseDomain + "' of type TXT.");
    }
}
```