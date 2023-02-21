package ru.practicum.service.events.service;

import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.events.dto.EventInDto;
import ru.practicum.service.events.dto.EventOutDto;
import ru.practicum.service.events.dto.EventPublicOutDto;
import ru.practicum.service.events.exceptions.DateException;
import ru.practicum.service.events.exceptions.EventClosedException;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.events.model.SortType;
import ru.practicum.service.users.exceptions.UserNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventsService {
    EventPublicOutDto findEventById(Long eventId, HttpServletRequest request) throws EventNotFoundException;

    List<EventPublicOutDto> findAllEvents(String text,
                                          Long[] categories,
                                          Boolean paid,
                                          String rangeStart,
                                          String rangeEnd,
                                          Boolean onlyAvailable,
                                          SortType sortType,
                                          Integer from,
                                          Integer size,
                                          HttpServletRequest request);

    EventOutDto addEvent(Long userId, EventInDto eventInDto) throws CategoryNotFoundException, UserNotFoundException, DateException;

    EventOutDto updateEvent(Long userId, EventInDto eventInDto) throws CategoryNotFoundException, UserNotFoundException, EventNotFoundException, EventClosedException, DateException;

    List<EventOutDto> findAllEvents(Long userId, Integer from, Integer size) throws UserNotFoundException;

    EventOutDto getEvent(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException;

    EventOutDto cancelEvent(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, EventClosedException;

    List<EventOutDto> findAllEvents(Long[] users, String[] states, Long[] categories, String rangeStart, String rangeEnd, Integer from, Integer size) throws UserNotFoundException, CategoryNotFoundException;

    EventOutDto publishEvent(Long eventId) throws EventClosedException, EventNotFoundException, DateException;

    EventOutDto rejectEvent(Long eventId) throws EventClosedException, EventNotFoundException;

    EventOutDto updateEvent(Long eventId, EventInDto eventInDto, String stateAction) throws EventNotFoundException, CategoryNotFoundException, DateException, EventClosedException;
}
