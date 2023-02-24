package ru.practicum.service.compilations.mapper;

import ru.practicum.service.compilations.dto.CompilationInDto;
import ru.practicum.service.compilations.dto.CompilationOutDto;
import ru.practicum.service.compilations.model.Compilation;
import ru.practicum.service.events.mapper.EventMapper;
import ru.practicum.service.events.model.Event;

import java.util.ArrayList;
import java.util.List;

public class CompilationMapper {
    public static Compilation dtoToCompilation(CompilationInDto compilationInDto, List<Event> events) {
        return new Compilation(
                null,
                compilationInDto.getTitle(),
                compilationInDto.getPinned(),
                events
        );
    }

    public static CompilationOutDto compilationToOutDto(Compilation compilation) {
        return new CompilationOutDto(
                compilation.getId(),
                compilation.getTitle(),
                EventMapper.eventToListOutDto(compilation.getEvents()),
                compilation.getPinned()
        );
    }

    public static List<CompilationOutDto> compilationToListOutDto(List<Compilation> compilationList) {
        List<CompilationOutDto> compilationOutDtoList = new ArrayList<>();
        for (Compilation compilation : compilationList) {
            compilationOutDtoList.add(compilationToOutDto(compilation));
        }
        return compilationOutDtoList;
    }
}
