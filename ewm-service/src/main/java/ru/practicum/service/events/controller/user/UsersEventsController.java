package ru.practicum.service.events.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.events.dto.EventInDto;
import ru.practicum.service.events.dto.EventInDtoStateAction;
import ru.practicum.service.events.dto.EventOutDto;
import ru.practicum.service.events.exceptions.DateException;
import ru.practicum.service.events.exceptions.EventClosedException;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.events.service.EventsService;
import ru.practicum.service.rating.exceptions.DoubleLikeException;
import ru.practicum.service.rating.exceptions.LikeNotFoundException;
import ru.practicum.service.rating.model.LikeType;
import ru.practicum.service.users.exceptions.UserNotFoundException;
import ru.practicum.service.utils.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UsersEventsController {
    private final EventsService eventsService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public EventOutDto addEvent(@Positive @PathVariable Long userId, @Valid @RequestBody EventInDto eventInDto)
            throws CategoryNotFoundException, UserNotFoundException, DateException, EventNotFoundException {
        log.info("User addEvent: {},{}", userId, eventInDto);
        return eventsService.addEvent(userId, eventInDto);
    }

    @PatchMapping
    public EventOutDto updateEvent(@Positive @PathVariable Long userId, @Valid @RequestBody EventInDto eventInDto)
            throws
            CategoryNotFoundException,
            UserNotFoundException,
            EventNotFoundException,
            EventClosedException, DateException {
        log.info("User updateEvent: {},{}", userId, eventInDto);
        return eventsService.updateEvent(userId, eventInDto);
    }

    @GetMapping
    public List<EventOutDto> findAllEvents(@Positive @PathVariable Long userId,
                                           @PositiveOrZero
                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive
                                           @RequestParam(name = "size", defaultValue = Constants.PAGE_SIZE_STRING) Integer size)
            throws UserNotFoundException {
        log.info("User findAllEvents: {},{},{}", userId, from, size);
        return eventsService.findAllEvents(userId, from, size);
    }

    @GetMapping("{eventId}")
    public EventOutDto getEvent(@Positive @PathVariable Long userId,
                                @Positive @PathVariable Long eventId)
            throws UserNotFoundException, EventNotFoundException {
        log.info("User getEvent: {},{}", userId, eventId);
        return eventsService.getEvent(userId, eventId);
    }

    @PatchMapping("{eventId}")
    public EventOutDto cancelEvent(@Positive @PathVariable Long userId,
                                   @Positive @PathVariable Long eventId,
                                   @RequestBody EventInDtoStateAction eventInDtoStateAction)
            throws
            CategoryNotFoundException,
            UserNotFoundException,
            EventNotFoundException,
            EventClosedException, DateException {
        log.info("User cancelEvent: {},{}", userId, eventId);
        if (eventInDtoStateAction.getStateAction().equals("CANCEL_REVIEW")) {
            return eventsService.cancelEvent(userId, eventId);
        } else {
            EventInDto eventInDto = new EventInDto();
            eventInDto.setEventId(eventId);
            return eventsService.updateEvent(userId, eventInDto);
        }
    }

    @PutMapping("/{eventId}/like")
    public void addLike(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId,
            @RequestParam(name = "type") String type
    ) throws UserNotFoundException,
            EventNotFoundException,
            DoubleLikeException,
            LikeNotFoundException,
            AccessDeniedException {
        LikeType likeType = LikeType.from(type)
                .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + type));
        eventsService.addLike(userId, eventId, likeType);
    }

    @DeleteMapping("/{eventId}/like")
    public void removeLike(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId,
            @RequestParam(name = "type") String type
    ) throws UserNotFoundException, EventNotFoundException, LikeNotFoundException, AccessDeniedException {
        LikeType likeType = LikeType.from(type)
                .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + type));
        eventsService.removeLike(userId, eventId, likeType);
    }

}
