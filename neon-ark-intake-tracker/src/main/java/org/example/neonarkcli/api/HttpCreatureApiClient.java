package org.example.neonarkcli.api;

import org.example.neonarkcli.model.Creature;
import org.example.neonarkcli.model.Observation;
import org.example.neonarkcli.model.SystemUser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Real HTTP adapter — makes actual calls to the Spring Boot backend.
 * Uses Java's built-in HttpClient (no external dependencies needed).
 * JSON parsing is done manually to avoid adding a library dependency.
 */
public class HttpCreatureApiClient {

    private final String baseUrl;
    private final HttpClient http;

    public HttpCreatureApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.http    = HttpClient.newHttpClient();
    }

    // ── 1. List all creatures ─────────────────────────────────────────────────
    public ApiResult<List<Creature>> listAll() {
        try {
            HttpRequest req = get("/api/creatures");
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                return ApiResult.ok(parseCreatureList(res.body()), res.statusCode());
            }
            return ApiResult.error(extractMessage(res.body()), res.statusCode());
        } catch (Exception e) {
            return ApiResult.error("Could not connect to server: " + e.getMessage(), 0);
        }
    }

    // ── 2. Get one creature by id ─────────────────────────────────────────────
    public ApiResult<Creature> getById(long id) {
        try {
            HttpRequest req = get("/api/creatures/" + id);
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                return ApiResult.ok(parseCreature(res.body()), res.statusCode());
            }
            return ApiResult.error(extractMessage(res.body()), res.statusCode());
        } catch (Exception e) {
            return ApiResult.error("Could not connect to server: " + e.getMessage(), 0);
        }
    }

    // ── 3. Register new creature ──────────────────────────────────────────────
    public ApiResult<Creature> create(String name, String species, String dangerLevel,
                                       String condition, long habitatId) {
        try {
            String body = String.format(
                    "{\"name\":\"%s\",\"species\":\"%s\",\"dangerLevel\":\"%s\"," +
                    "\"condition\":\"%s\",\"habitatId\":%d}",
                    name, species, dangerLevel, condition, habitatId);

            HttpRequest req = post("/api/creatures", body);
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 201) {
                return ApiResult.ok(parseCreature(res.body()), res.statusCode());
            }
            return ApiResult.error(extractMessage(res.body()), res.statusCode());
        } catch (Exception e) {
            return ApiResult.error("Could not connect to server: " + e.getMessage(), 0);
        }
    }

    // ── 4. Rename creature ────────────────────────────────────────────────────
    public ApiResult<String> rename(long id, String newName) {
        try {
            String body = String.format("{\"newName\":\"%s\"}", newName);
            HttpRequest req = put("/api/creatures/" + id + "/name", body);
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                String oldName = extractField(res.body(), "oldName");
                String renamedTo = extractField(res.body(), "newName");
                return ApiResult.ok("Renamed from \"" + oldName + "\" to \"" + renamedTo + "\"", res.statusCode());
            }
            return ApiResult.error(extractMessage(res.body()), res.statusCode());
        } catch (Exception e) {
            return ApiResult.error("Could not connect to server: " + e.getMessage(), 0);
        }
    }

    // ── 5. Soft delete creature ───────────────────────────────────────────────
    public ApiResult<String> softDelete(long id) {
        try {
            HttpRequest req = delete("/api/creatures/" + id);
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                return ApiResult.ok(extractField(res.body(), "message"), res.statusCode());
            }
            return ApiResult.error(extractMessage(res.body()), res.statusCode());
        } catch (Exception e) {
            return ApiResult.error("Could not connect to server: " + e.getMessage(), 0);
        }
    }

    // ── 6. Get creature with observations ─────────────────────────────────────
    public ApiResult<CreatureWithObservations> getWithObservations(long id) {
        try {
            HttpRequest req = get("/api/creatures/" + id + "/observations");
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                return ApiResult.ok(parseCreatureWithObservations(res.body()), res.statusCode());
            }
            return ApiResult.error(extractMessage(res.body()), res.statusCode());
        } catch (Exception e) {
            return ApiResult.error("Could not connect to server: " + e.getMessage(), 0);
        }
    }

    // ── 7. Find creatures by feeding time ─────────────────────────────────────
    public ApiResult<FeedingResult> getByFeedingTime(String time) {
        try {
            HttpRequest req = get("/api/feedings?time=" + time);
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                String message   = extractField(res.body(), "message");
                String creatures = extractArrayBlock(res.body(), "creatures");
                List<Creature> list = creatures.equals("[]") ? new ArrayList<>() : parseCreatureList(creatures);
                return ApiResult.ok(new FeedingResult(message, list), res.statusCode());
            }
            return ApiResult.error(extractMessage(res.body()), res.statusCode());
        } catch (Exception e) {
            return ApiResult.error("Could not connect to server: " + e.getMessage(), 0);
        }
    }

    // ── 8. List all users (admin) ─────────────────────────────────────────────
    public ApiResult<List<SystemUser>> getAllUsers() {
        try {
            HttpRequest req = get("/api/admin/users");
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) {
                return ApiResult.ok(parseUserList(res.body()), res.statusCode());
            }
            return ApiResult.error(extractMessage(res.body()), res.statusCode());
        } catch (Exception e) {
            return ApiResult.error("Could not connect to server: " + e.getMessage(), 0);
        }
    }

    // ── HTTP helpers ──────────────────────────────────────────────────────────

    private HttpRequest get(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Accept", "application/json")
                .GET()
                .build();
    }

    private HttpRequest post(String path, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private HttpRequest put(String path, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private HttpRequest delete(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .DELETE()
                .build();
    }

    // ── Simple JSON parsers (no external library needed) ─────────────────────

    private List<Creature> parseCreatureList(String json) {
        List<Creature> list = new ArrayList<>();
        String[] objects = splitJsonObjects(json);
        for (String obj : objects) {
            if (!obj.isBlank()) list.add(parseCreature(obj));
        }
        return list;
    }

    private Creature parseCreature(String json) {
        return new Creature(
                parseLong(json, "id"),
                extractField(json, "name"),
                extractField(json, "species"),
                extractField(json, "dangerLevel"),
                extractField(json, "condition"),
                extractField(json, "status"),
                extractField(json, "habitatName"),
                extractField(json, "createdAt")
        );
    }

    private CreatureWithObservations parseCreatureWithObservations(String json) {
        Creature creature = parseCreature(json);
        List<Observation> obs = new ArrayList<>();
        String obsArray = extractArrayBlock(json, "observations");
        if (!obsArray.equals("[]")) {
            for (String obj : splitJsonObjects(obsArray)) {
                if (!obj.isBlank()) {
                    obs.add(new Observation(
                            parseLong(obj, "id"),
                            extractField(obj, "authorName"),
                            extractField(obj, "notes"),
                            extractField(obj, "observedAt")
                    ));
                }
            }
        }
        return new CreatureWithObservations(creature, obs);
    }

    private List<SystemUser> parseUserList(String json) {
        List<SystemUser> list = new ArrayList<>();
        for (String obj : splitJsonObjects(json)) {
            if (!obj.isBlank()) {
                list.add(new SystemUser(
                        parseLong(obj, "id"),
                        extractField(obj, "fullName"),
                        extractField(obj, "email"),
                        extractField(obj, "phone"),
                        extractField(obj, "role")
                ));
            }
        }
        return list;
    }

    // Extract a string field value from a JSON object string
    private String extractField(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (start >= json.length()) return "";

        if (json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            return end == -1 ? "" : json.substring(start + 1, end);
        } else {
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(start, end).trim();
        }
    }

    private long parseLong(String json, String key) {
        try { return Long.parseLong(extractField(json, key)); }
        catch (NumberFormatException e) { return 0; }
    }

    // Extract message from error response
    private String extractMessage(String json) {
        String msg = extractField(json, "message");
        return msg.isBlank() ? json : msg;
    }

    // Extract a JSON array block by key
    private String extractArrayBlock(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return "[]";
        start = json.indexOf('[', start);
        if (start == -1) return "[]";
        int depth = 0, i = start;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') { depth--; if (depth == 0) return json.substring(start, i + 1); }
            i++;
        }
        return "[]";
    }

    // Split a JSON array into individual object strings
    private String[] splitJsonObjects(String json) {
        String trimmed = json.trim();
        if (trimmed.startsWith("[")) trimmed = trimmed.substring(1);
        if (trimmed.endsWith("]"))   trimmed = trimmed.substring(0, trimmed.length() - 1);

        List<String> objects = new ArrayList<>();
        int depth = 0, start = 0;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (c == '{') { if (depth == 0) start = i; depth++; }
            else if (c == '}') { depth--; if (depth == 0) objects.add(trimmed.substring(start, i + 1)); }
        }
        return objects.toArray(new String[0]);
    }

    // ── Inner result types ────────────────────────────────────────────────────

    public static class ApiResult<T> {
        public final boolean success;
        public final T data;
        public final String error;
        public final int statusCode;

        private ApiResult(boolean success, T data, String error, int statusCode) {
            this.success    = success;
            this.data       = data;
            this.error      = error;
            this.statusCode = statusCode;
        }

        public static <T> ApiResult<T> ok(T data, int statusCode) {
            return new ApiResult<>(true, data, null, statusCode);
        }

        public static <T> ApiResult<T> error(String error, int statusCode) {
            return new ApiResult<>(false, null, error, statusCode);
        }
    }

    public static class CreatureWithObservations {
        public final Creature creature;
        public final List<Observation> observations;

        public CreatureWithObservations(Creature creature, List<Observation> observations) {
            this.creature     = creature;
            this.observations = observations;
        }
    }

    public static class FeedingResult {
        public final String message;
        public final List<Creature> creatures;

        public FeedingResult(String message, List<Creature> creatures) {
            this.message   = message;
            this.creatures = creatures;
        }
    }
}
