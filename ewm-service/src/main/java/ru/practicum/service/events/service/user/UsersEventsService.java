package ru.practicum.service.events.service.user;

import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.events.dto.EventInDto;
import ru.practicum.service.events.dto.EventOutDto;
import ru.practicum.service.events.exceptions.DateException;
import ru.practicum.service.events.exceptions.EventClosedException;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.users.exceptions.UserNotFoundException;

import java.util.List;

public interface UsersEventsService {
    EventOutDto addEvent(Long userId, EventInDto eventInDto) throws CategoryNotFoundException, UserNotFoundException, DateException;

    EventOutDto updateEvent(Long userId, EventInDto eventInDto) throws CategoryNotFoundException, UserNotFoundException, EventNotFoundException, EventClosedException, DateException;

    List<EventOutDto> findAllEvents(Long userId, Integer from, Integer size) throws UserNotFoundException;

    EventOutDto getEvent(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException;

    EventOutDto cancelEvent(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, EventClosedException;

}
