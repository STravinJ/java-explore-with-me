package ru.practicum.service.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.StatInDto;
import ru.practicum.service.client.BaseClient;

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
}
