package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.StatInDto;
import ru.practicum.ewm.dto.StatOutDto;
import ru.practicum.ewm.model.Constants;
import ru.practicum.ewm.model.Stat;
import ru.practicum.ewm.service.StatsService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public void saveHit(@RequestBody StatInDto statInDto) {

        log.info("StatsController.saveHit:\n" +
                        "app={},\n" +
                        "uri={},\n" +
                        "ip={},\n" +
                        "timestamp={}\n",
                statInDto.getApp(), statInDto.getUri(), statInDto.getIp(), statInDto.getTimestamp());

        statsService.saveHit(statInDto);

    }

    @GetMapping("/stats")
    public List<StatOutDto> getHitsByParams(@NotNull @RequestParam(name = "start") String start,
                                    @NotNull @RequestParam(name = "end") String end,
                                    @RequestParam(name = "uris", defaultValue = "", required = false) List<String> uris,
                                    @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {

        log.info("StatsController.getHitsByParams:\n" +
                        "start={},\n" +
                        "end={},\n" +
                        "uris={},\n" +
                        "unique={}\n",
                start, end, uris, unique);

        return statsService.getHitsByParams(start, end, uris, unique);

    }

}
