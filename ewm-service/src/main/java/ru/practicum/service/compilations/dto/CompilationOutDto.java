package ru.practicum.service.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.service.events.dto.EventOutDto;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompilationOutDto {
    private Long id;
    private String title;
    private List<EventOutDto> events;
    private Boolean pinned;
}
