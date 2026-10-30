    package app.utils;

    import app.exceptions.MotorAPIException;
    import com.fasterxml.jackson.databind.ObjectMapper;

    import java.net.URI;
    import java.net.http.HttpClient;
    import java.net.http.HttpRequest;
    import java.net.http.HttpResponse;

    public class APIReader {
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final HttpClient client = HttpClient.newHttpClient();

        public <T> T get(String url, Class<T> responseType, String header, String headerValue) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header(header, headerValue)
                    .GET()
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if(response.statusCode() == 404) {
                    return null;
                }

                if(response.statusCode() != 200) {
                    throw new MotorAPIException("MotorAPI request failed with status " + response.statusCode(), null);
                }
                return objectMapper.readValue(response.body(), responseType);

            } catch (Exception e) {
                throw new RuntimeException("API Request failed", e);
            }
        }
    }