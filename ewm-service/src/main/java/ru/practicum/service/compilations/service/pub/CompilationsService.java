package ru.practicum.service.compilations.service.pub;

import ru.practicum.service.compilations.dto.CompilationOutDto;
import ru.practicum.service.compilations.exceptions.CompilationNotFoundException;

import java.util.List;

public interface CompilationsService {
    List<CompilationOutDto> findAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationOutDto findCompilationById(Long compId) throws CompilationNotFoundException;
}
