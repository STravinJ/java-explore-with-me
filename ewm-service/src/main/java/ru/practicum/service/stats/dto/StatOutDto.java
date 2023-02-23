package ru.practicum.service.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatOutDto {

    private String app;
    private String uri;
    private Long hits;

}
