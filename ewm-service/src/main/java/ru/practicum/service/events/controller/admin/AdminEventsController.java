package ru.practicum.service.events.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.events.dto.EventInDto;
import ru.practicum.service.events.dto.EventOutDto;
import ru.practicum.service.events.exceptions.DateException;
import ru.practicum.service.events.exceptions.EventClosedException;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.events.service.admin.AdminEventsService;
import ru.practicum.service.users.exceptions.UserNotFoundException;
import ru.practicum.service.utils.Constants;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminEventsController {
    private final AdminEventsService adminEventsService;

    @GetMapping
    public List<EventOutDto> findAllEvents(
            @RequestParam(value = "users", defaultValue = "") Long[] users,
            @RequestParam(value = "states", required = false) String[] states,
            @RequestParam(value = "categories", required = false) Long[] categories,
            @RequestParam(name = "rangeStart", required = false) String rangeStart,
            @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
            @PositiveOrZero
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive
            @RequestParam(name = "size", defaultValue = Constants.PAGE_SIZE_STRING) Integer size)
            throws UserNotFoundException, CategoryNotFoundException {
        log.info("Admin findAllEvents: {},{},{},{},{},{},{}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return adminEventsService.findAllEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}/publish")
    public EventOutDto publishEvent(@PathVariable Long eventId)
            throws EventNotFoundException, EventClosedException, DateException {
        log.info("Admin publishEvent: {}", eventId);
        return adminEventsService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventOutDto rejectEvent(@Positive @PathVariable Long eventId)
            throws EventNotFoundException, EventClosedException {
        log.info("Admin Patch rejectEvent: {}", eventId);
        return adminEventsService.rejectEvent(eventId);
    }

    @PatchMapping("/{eventId}")
    public EventOutDto updateEvent(@PathVariable Long eventId,
                                   @RequestBody EventInDto eventInDto)
            throws EventNotFoundException, CategoryNotFoundException, DateException, EventClosedException {
        log.info("Admin Put updateEvent: {},{}", eventId, eventInDto);
        return adminEventsService.updateEvent(eventId, eventInDto, eventInDto.getStateAction());
    }
}
