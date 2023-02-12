package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.StatInDto;
import ru.practicum.ewm.dto.StatOutDto;
import ru.practicum.ewm.model.Stat;

import java.util.List;

public interface StatsService {
    void saveHit(StatInDto statInDto);

    List<StatOutDto> getHitsByParams(String start, String end, List<String> uris, Boolean unique);

    List<Stat> getAllHits(Integer from, Integer size);
}
