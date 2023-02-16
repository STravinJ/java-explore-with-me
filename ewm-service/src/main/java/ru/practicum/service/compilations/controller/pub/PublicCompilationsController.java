package ru.practicum.service.compilations.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.compilations.dto.CompilationOutDto;
import ru.practicum.service.compilations.exceptions.CompilationNotFoundException;
import ru.practicum.service.compilations.service.pub.CompilationsService;
import ru.practicum.service.utils.Constants;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCompilationsController {

    private final CompilationsService compilationsService;

    @GetMapping
    public List<CompilationOutDto> findAllCompilations(
            @RequestParam(name = "pinned", required = false) Boolean pinned,
            @PositiveOrZero
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive
            @RequestParam(name = "size", defaultValue = Constants.PAGE_SIZE_STRING) Integer size) {
        log.info("Public findAllCompilations: {},{},{}", pinned, from, size);
        return compilationsService.findAllCompilations(pinned, from, size);
    }

    @GetMapping("{compId}")
    public CompilationOutDto findCompilationById(@Positive @PathVariable Long compId) throws CompilationNotFoundException {
        log.info("Public findCompilationById: {}", compId);
        return compilationsService.findCompilationById(compId);
    }
}
