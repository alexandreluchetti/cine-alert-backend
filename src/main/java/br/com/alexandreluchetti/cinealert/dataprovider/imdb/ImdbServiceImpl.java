package br.com.alexandreluchetti.cinealert.dataprovider.imdb;

import br.com.alexandreluchetti.cinealert.entrypoint.dto.content.ContentResponse;
import br.com.alexandreluchetti.cinealert.core.model.enums.ContentType;
import br.com.alexandreluchetti.cinealert.core.service.ImdbService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class ImdbServiceImpl implements ImdbService {

    @Value("${app.imdb.api-key}")
    private String apiKey;

    @Value("${app.imdb.base-url}")
    private String baseUrl;

    @Value("${app.imdb.host}")
    private String apiHost;

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    public ImdbServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    @Override
    public List<ContentResponse> search(String query, String type, String genre, Integer year, Double minRating) {
        try {
            String url = baseUrl + "/title/find?q=" + encodeQuery(query);
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), String.class);

            JsonNode root = mapper.readTree(response.getBody());
            JsonNode results = root.path("results");

            List<ContentResponse> items = new ArrayList<>();
            if (results.isArray()) {
                for (JsonNode node : results) {
                    ContentResponse content = mapToContentResponse(node);
                    if (content != null) {
                        items.add(content);
                    }
                }
            }
            return items;
        } catch (Exception e) {
            log.error("Error searching IMDB: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public Optional<ContentResponse> getDetail(String imdbId) {
        try {
            String url = baseUrl + "/title/get-overview-details?tconst=" + imdbId + "&currentCountry=BR";
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), String.class);

            JsonNode root = mapper.readTree(response.getBody());
            return Optional.ofNullable(mapDetailToContentResponse(imdbId, root));
        } catch (Exception e) {
            log.error("Error fetching IMDB detail for {}: {}", imdbId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<ContentResponse> getTrending() {
        try {
            String url = baseUrl + "/title/get-top-rated-movies";
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), String.class);

            JsonNode root = mapper.readTree(response.getBody());
            List<ContentResponse> items = new ArrayList<>();
            if (root.isArray()) {
                int count = 0;
                for (JsonNode node : root) {
                    if (count++ >= 20)
                        break;
                    String id = node.path("id").asText("").replace("/title/", "").replace("/", "");
                    ContentResponse content = new ContentResponse(
                            null, id,
                            node.path("title").asText("Unknown"),
                            ContentType.MOVIE,
                            extractImageUrl(node.path("image")),
                            node.path("year").asInt(0),
                            safeDecimal(node.path("imDbRating").asText("0")),
                            null, null, null, null);
                    items.add(content);
                }
            }
            return items;
        } catch (Exception e) {
            log.error("Error fetching trending from IMDB: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<String> getGenres() {
        return Arrays.stream(GenreEnum.values()).map(GenreEnum::getValue).toList();
    }

    // --- Mappers ---

    private ContentResponse mapToContentResponse(JsonNode node) {
        String id = node.path("id").asText("").replace("/title/", "").replace("/", "");
        if (id.isEmpty() || !id.startsWith("tt"))
            return null;

        String titleType = node.path("titleType").asText("movie");
        ContentType type = mapType(titleType);

        return new ContentResponse(
                null, id,
                node.path("title").asText("Unknown"),
                type,
                extractImageUrl(node.path("image")),
                node.path("year").asInt(0),
                safeDecimal(node.path("imDbRating").asText("0")),
                extractGenres(node.path("genres")),
                node.path("plot").asText(null),
                null,
                node.path("runningTimeInMinutes").asInt(0) > 0 ? node.path("runningTimeInMinutes").asInt() : null);
    }

    private ContentResponse mapDetailToContentResponse(String imdbId, JsonNode root) {
        JsonNode title = root.path("title");
        if (title.isMissingNode())
            return null;

        String titleType = title.path("titleType").asText("movie");
        ContentType type = mapType(titleType);

        JsonNode ratings = root.path("ratings");
        JsonNode plot = root.path("plotSummary");

        return new ContentResponse(
                null, imdbId,
                title.path("title").asText("Unknown"),
                type,
                extractImageUrl(title.path("image")),
                title.path("year").asInt(0),
                safeDecimal(ratings.path("rating").asText("0")),
                extractGenres(title.path("genres")),
                plot.path("text").asText(null),
                null,
                title.path("runningTimeInMinutes").asInt(0) > 0 ? title.path("runningTimeInMinutes").asInt() : null);
    }

    private String extractImageUrl(JsonNode imageNode) {
        String url = imageNode.path("url").asText(null);
        if (url != null && url.contains("._V1_")) {
            // Resize to poster size
            return url.replaceAll("\\._V1_.*", "._V1_SX300.jpg");
        }
        return url;
    }

    private String extractGenres(JsonNode genresNode) {
        if (genresNode.isArray()) {
            List<String> genres = new ArrayList<>();
            for (JsonNode g : genresNode) {
                genres.add(g.asText());
            }
            return String.join(", ", genres);
        }
        return genresNode.asText(null);
    }

    private ContentType mapType(String titleType) {
        return switch (titleType.toLowerCase()) {
            case "tvmovie", "movie" -> ContentType.MOVIE;
            case "tvseries" -> ContentType.SERIES;
            case "tvminiseries" -> ContentType.MINI_SERIES;
            case "documentary" -> ContentType.DOCUMENTARY;
            default -> ContentType.MOVIE;
        };
    }

    private BigDecimal safeDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String encodeQuery(String query) {
        return query.replace(" ", "+");
    }
}
