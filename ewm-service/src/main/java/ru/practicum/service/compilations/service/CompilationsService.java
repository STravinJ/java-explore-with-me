package ru.practicum.service.compilations.service;

import ru.practicum.service.compilations.dto.CompilationInDto;
import ru.practicum.service.compilations.dto.CompilationOutDto;
import ru.practicum.service.compilations.exceptions.CompilationNotFoundException;
import ru.practicum.service.events.exceptions.EventNotFoundException;

import java.util.List;

public interface CompilationsService {
    List<CompilationOutDto> findAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationOutDto findCompilationById(Long compId) throws CompilationNotFoundException;

    CompilationOutDto addCompilation(CompilationInDto compilationInDto);

    void removeCompilation(Long compId) throws CompilationNotFoundException;

    void removeEventFromCompilation(Long compId, Long eventId) throws CompilationNotFoundException;

    CompilationOutDto addEventToCompilation(Long compId, Long eventId) throws CompilationNotFoundException, EventNotFoundException;

    CompilationOutDto addEventToCompilationDto(Long compId, CompilationInDto compilationInDto) throws CompilationNotFoundException, EventNotFoundException;

    void pinCompilation(Long compId) throws CompilationNotFoundException;

    void unPinCompilation(Long compId) throws CompilationNotFoundException;
}
