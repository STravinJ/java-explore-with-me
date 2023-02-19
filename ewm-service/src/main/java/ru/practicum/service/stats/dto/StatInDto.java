package ru.practicum.service.stats.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatInDto {
    private String app;
    private Long eventId;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
