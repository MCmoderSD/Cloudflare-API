import de.MCmoderSD.cloudflare.core.CloudflareClient;

@SuppressWarnings("ALL")
public class Main {

    public static void main(String[] args) {

        // Cloudflare Credentials
        String zoneId = "YOUR_ZONE_ID";
        String apiToken = "YOUR_API";

        // Initialize Cloudflare Client
        CloudflareClient client = new CloudflareClient(zoneId, apiToken);

        // List DNS Records
        listDnsRecords(client);
    }

    private static void listDnsRecords(CloudflareClient client) {
        var records = client.getRecords();
        for (var record : records) {
            System.out.println("--------------------------------");
            System.out.println("ID: " + record.getId());
            System.out.println("Name: " + record.getName());
            System.out.println("Type: " + record.getType());
            System.out.println("Content: " + record.getContent());
            System.out.println("Proxiable: " + record.isProxiable());
            System.out.println("Proxied: " + record.isProxied());
            System.out.println("TTL: " + record.getTtl() + " seconds");
            System.out.println("Comment: " + record.getComment());
            System.out.println("Created On: " + record.getCreated());
            System.out.println("Modified On: " + record.getModified());
            System.out.println("--------------------------------");
        }
    }
}