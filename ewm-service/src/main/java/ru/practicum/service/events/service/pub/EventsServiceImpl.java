package ru.practicum.service.events.service.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.service.events.dto.EventPublicOutDto;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.events.mapper.EventMapper;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.model.EventState;
import ru.practicum.service.events.model.SortType;
import ru.practicum.service.events.repository.EventsRepository;
import ru.practicum.service.stats.controller.StatsClient;
import ru.practicum.service.stats.dto.StatInDto;
import ru.practicum.service.utils.Constants;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsServiceImpl implements EventsService {
    private final EventsRepository eventsRepository;
    private final StatsClient adminStatsClient;

    @Override
    public EventPublicOutDto findEventById(Long eventId, HttpServletRequest request) throws EventNotFoundException {
        if (!eventsRepository.existsByIdAndState(eventId, EventState.PUBLISHED)) {
            throw new EventNotFoundException("Event not found.");
        }
        Event event = eventsRepository.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(
                () -> new EventNotFoundException("Event not found.")
        );

        try {
            adminStatsClient.saveHit(new StatInDto(
                Constants.APP_NAME,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
            ));
        } catch (Exception err) {
            log.info(">>Hit send. Error: " + err.getMessage());
        }

        return EventMapper.eventToPublicOutDto(event);
    }

    @Override
    public List<EventPublicOutDto> findAllEvents(String text,
                                                 Long[] categories,
                                                 Boolean paid,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Boolean onlyAvailable,
                                                 SortType sortType,
                                                 Integer from,
                                                 Integer size, HttpServletRequest request) {

        Pageable pageable = PageRequest.of(from / size, size);

        LocalDateTime startDate;
        if (rangeStart != null) {
            startDate = LocalDateTime.parse(rangeStart, Constants.DATE_TIME_SPACE);
        } else {
            startDate = LocalDateTime.now();
        }
        LocalDateTime endDate;
        if (rangeStart != null) {
            endDate = LocalDateTime.parse(rangeEnd, Constants.DATE_TIME_SPACE);
        } else {
            endDate = LocalDateTime.now();
        }

        List<EventPublicOutDto> events = eventsRepository.findAllByParam(
                text,
                categories,
                paid,
                startDate,
                endDate,
                onlyAvailable,
                pageable).stream()
                .map(EventMapper::eventToPublicOutDto)
                .collect(Collectors.toList());

        for (EventPublicOutDto eventPublicOutDto : events) {
            try {
                eventPublicOutDto.setViews(adminStatsClient.getViews(eventPublicOutDto.getId()));
            } catch (Exception err) {
                log.info(">>Hit search send. Error: " + err.getMessage());
            }
        }

        switch (sortType) {
            case EVENT_DATE:
                events = events.stream()
                        .sorted(Comparator.comparing(EventPublicOutDto::getEventDate))
                        .collect(Collectors.toUnmodifiableList());
                break;
            case VIEWS:
                events = events.stream()
                        .sorted(Comparator.comparing(EventPublicOutDto::getViews))
                        .collect(Collectors.toUnmodifiableList());
                break;
            default:
                throw new IllegalArgumentException("Указан не существующий тип сортировки.");
        }

        try {
            adminStatsClient.saveHit(new StatInDto(
                Constants.APP_NAME,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
            ));
        } catch (Exception err) {
            log.info(">>Hit search send. Error: " + err.getMessage());
        }

        return events;
    }
}
