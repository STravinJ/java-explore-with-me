package ru.practicum.service.events.service.admin;

import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.events.dto.EventInDto;
import ru.practicum.service.events.dto.EventOutDto;
import ru.practicum.service.events.exceptions.EventClosedException;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.users.exceptions.UserNotFoundException;

import java.util.List;

public interface AdminEventsService {
    List<EventOutDto> findAllEvents(Long[] users, String[] states, Long[] categories, String rangeStart, String rangeEnd, Integer from, Integer size) throws UserNotFoundException, CategoryNotFoundException;

    EventOutDto publishEvent(Long eventId) throws EventClosedException, EventNotFoundException;

    EventOutDto rejectEvent(Long eventId) throws EventClosedException, EventNotFoundException;

    EventOutDto updateEvent(Long eventId, EventInDto eventInDto) throws EventNotFoundException, CategoryNotFoundException;
}
