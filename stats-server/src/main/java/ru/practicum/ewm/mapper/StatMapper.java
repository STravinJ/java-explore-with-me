package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.StatInDto;
import ru.practicum.ewm.model.Constants;
import ru.practicum.ewm.model.Stat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatMapper {
    public static Stat dtoToStat(StatInDto statInDto) {

        Stat stat = new Stat();

        stat.setApp(statInDto.getApp());
        stat.setUri(statInDto.getUri());
        stat.setIp(statInDto.getIp());
        stat.setTimestamp(LocalDateTime.parse(statInDto.getTimestamp(), DateTimeFormatter.ofPattern(Constants.DATE_TIME_STRING)));

        return stat;

    }
}
