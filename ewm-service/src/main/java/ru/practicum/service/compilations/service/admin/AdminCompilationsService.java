package ru.practicum.service.compilations.service.admin;

import ru.practicum.service.compilations.dto.CompilationInDto;
import ru.practicum.service.compilations.dto.CompilationOutDto;
import ru.practicum.service.compilations.exceptions.CompilationNotFoundException;
import ru.practicum.service.events.exceptions.EventNotFoundException;

public interface AdminCompilationsService {

    CompilationOutDto addCompilation(CompilationInDto compilationInDto);

    void removeCompilation(Long compId) throws CompilationNotFoundException;

    void removeEventFromCompilation(Long compId, Long eventId) throws CompilationNotFoundException;

    CompilationOutDto addEventToCompilation(Long compId, Long eventId) throws CompilationNotFoundException, EventNotFoundException;

    void pinCompilation(Long compId) throws CompilationNotFoundException;

    void unPinCompilation(Long compId) throws CompilationNotFoundException;
}
