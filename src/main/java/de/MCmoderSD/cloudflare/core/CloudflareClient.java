package de.MCmoderSD.cloudflare.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.MCmoderSD.cloudflare.objects.DnsRecord;
import de.MCmoderSD.cloudflare.objects.ModifiedRecord;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.HashSet;

public class CloudflareClient {

    // Endpoint URLs
    private static final String BASE_URL = "https://api.cloudflare.com/client/v4/zones/";

    // Credentials
    private final String zoneId;

    // Attributes
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final HttpRequest.Builder requestBuilder;

    // Constructor
    public CloudflareClient(String zoneId, String apiToken) {

        // Set Zone ID
        this.zoneId = zoneId;

        // Initialize Attributes
        mapper = new ObjectMapper();
        client = HttpClient.newHttpClient();

        // Initialize Request Builder
        requestBuilder = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json");
    }

    private JsonNode sendRequest(HttpRequest request) throws IOException, InterruptedException {

        // Check request
        if (request == null) throw new IllegalArgumentException("Request cannot be null");

        // Send request
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response
        if (httpResponse == null) throw new RuntimeException("HTTP response is null");
        if (httpResponse.statusCode() != 200) throw new RuntimeException("Failed : HTTP error code : " + httpResponse.statusCode());
        var body = httpResponse.body();
        if (body == null) throw new RuntimeException("Response body is null");
        if (body.isBlank()) throw new RuntimeException("Response body is empty");

        // Parse response
        JsonNode response = mapper.readTree(body);
        if (response == null) throw new RuntimeException("Response body is null");
        if (response.isEmpty()) throw new RuntimeException("Response body is empty");
        if (!response.has("result") || response.get("result").isEmpty()) throw new RuntimeException("Response body does not contain result");
        if (!response.has("success") || !response.get("success").asBoolean()) throw new RuntimeException("Response body indicates failure");

        // Return response
        return response;
    }

    // Get Record Map
    public HashMap<String, DnsRecord> getRecordMap() {
        HashMap<String, DnsRecord> recordMap = new HashMap<>();
        for (var record : getRecords()) recordMap.put(record.getId(), record);
        return recordMap;
    }

    // Get Records
    public HashSet<DnsRecord> getRecords() {
        try {

            // Create request
            HttpRequest request = requestBuilder
                    .uri(new URI(BASE_URL + zoneId + "/dns_records"))
                    .GET()
                    .build();

            // Send request
            JsonNode response = sendRequest(request);

            // Check Result
            if (!response.get("result").isArray()) throw new RuntimeException("Response body does not contain result array");

            // Convert to set of Records
            HashSet<DnsRecord> dnsRecords = new HashSet<>();
            var result = response.get("result");
            for (var record : result) dnsRecords.add(new DnsRecord(record));
            return dnsRecords;

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get DNS records", e);
        }
    }

    // Update Record
    public boolean updateRecord(ModifiedRecord modifiedRecord) {
        try {

            // Check record
            if (modifiedRecord == null) throw new IllegalArgumentException("Modified record cannot be null");

            // Create request
            HttpRequest request = requestBuilder
                    .uri(new URI(BASE_URL + zoneId + "/dns_records/" + modifiedRecord.getId()))
                    .PUT(HttpRequest.BodyPublishers.ofString(modifiedRecord.getModifiedContent().toString()))
                    .build();

            // Send request
            DnsRecord response = new DnsRecord(sendRequest(request).get("result"));

            // Check if update was successful
            return response.equals(getRecordMap().get(modifiedRecord.getId()));

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException("Failed to update DNS record", e);
        }
    }

    // Delete Record
    public boolean deleteRecord(DnsRecord dnsRecord) {
        try {

            // Check record
            if (dnsRecord == null) throw new IllegalArgumentException("DNS record cannot be null");

            // Create request
            HttpRequest request = requestBuilder
                    .uri(new URI(BASE_URL + zoneId + "/dns_records/" + dnsRecord.getId()))
                    .DELETE()
                    .build();

            // Send request
            sendRequest(request);

            // Check if deletion was successful
            return !getRecordMap().containsKey(dnsRecord.getId());

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException("Failed to delete DNS record", e);
        }
    }

    // Create Record
    public DnsRecord createRecord(ObjectNode record) {
        try {

            // Check record
            if (record == null) throw new IllegalArgumentException("Record cannot be null");

            // Create request
            HttpRequest request = requestBuilder
                    .uri(new URI(BASE_URL + zoneId + "/dns_records"))
                    .POST(HttpRequest.BodyPublishers.ofString(record.toString()))
                    .build();

            // Send request
            JsonNode response = sendRequest(request);

            // Check if creation was successful
            DnsRecord createdRecord = new DnsRecord(response.get("result"));
            var recordMap = getRecordMap();
            if (!recordMap.containsKey(createdRecord.getId())) throw new RuntimeException("Record creation failed");
            var fetchedRecord = recordMap.get(createdRecord.getId());
            if (!createdRecord.equals(fetchedRecord)) throw new RuntimeException("Created record does not match fetched record");

            // Return created record
            return createdRecord;

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException("Failed to create DNS record", e);
        }
    }
}