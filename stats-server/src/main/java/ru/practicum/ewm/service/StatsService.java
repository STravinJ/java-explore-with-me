package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.StatInDto;
import ru.practicum.ewm.dto.StatOutDto;

import java.util.List;

public interface StatsService {
    void saveHit(StatInDto statInDto);

    List<StatOutDto> getHitsByParams(String start, String end, List<String> uris, Boolean unique);

}
