package ru.practicum.service.stats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.service.stats.dto.StatInDto;
import ru.practicum.service.utils.BaseClient;

import java.util.List;
import java.util.Map;

@Service
@PropertySource("classpath:application.properties")
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl) {
        super(serverUrl);
    }

    public ResponseEntity<Object> getHitsByParams(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        return get("/?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

    public void saveHit(StatInDto statInDto) {
        post("/hit", statInDto);
    }

    public Long getViews(Long eventId) {
        Map<String, Object> parameters = Map.of(
                "eventId", eventId
        );
        ResponseEntity<Object> views = get("/views/{eventId}", parameters);
        return (Long) views.getBody();
    }
}
