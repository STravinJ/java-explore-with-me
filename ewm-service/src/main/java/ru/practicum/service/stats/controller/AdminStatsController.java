package ru.practicum.service.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminStatsController {
    private final StatsClient adminStatsClient;

    @GetMapping("/admin/stats")
    public ResponseEntity<Object> getHitsByParams(@NotNull @RequestParam(name = "start") String start,
                                          @NotNull @RequestParam(name = "end") String end,
                                          @Valid
                                          @RequestParam(name = "uris", defaultValue = "", required = false) List<String> uris,
                                          @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("getHits: {},{},{},{}", start, end, uris, unique);
        return adminStatsClient.getHitsByParams(start, end, uris, unique);
    }
}
