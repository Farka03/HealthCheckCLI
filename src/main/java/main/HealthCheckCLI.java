package main;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HealthCheckCLI {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: HealthCheckCLI <interval> <url>");
            return;
        }

        int interval = Integer.parseInt(args[0]);
        String urlString = args[1];

        URI url;
        try {
            url = new URI(urlString);
        } catch (URISyntaxException e) {
            System.err.println("URL parsing error");
            return;
        }

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();
                if (statusCode == 200) {
                    System.out.println("Checking '" + url + "'. Result: OK(200)");
                } else {
                    System.out.println("Checking '" + url + "'. Result: ERR(" + statusCode + ")");
                }
            } catch (Exception e) {
                System.out.println("Checking '" + url + "'. Result: ERR(" + e.getMessage() + ")");
            }
        }, 0, interval, TimeUnit.SECONDS);
    }
}
