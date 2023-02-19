package ru.practicum.service.events.service.pub;

import ru.practicum.service.events.dto.EventPublicOutDto;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.events.model.SortType;
import ru.practicum.service.stats.dto.StatInDto;

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
}
