package ru.practicum.service.events.service.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

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

        eventsRepository.incrementViews(eventId);

        StatInDto statInDto = new StatInDto();
        statInDto.setApp(Constants.APP_NAME);
        statInDto.setUri(request.getRequestURI());
        statInDto.setIp(request.getRemoteAddr());
        statInDto.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_STRING)));
        adminStatsClient.saveHit(statInDto);

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

        Sort sort;
        switch (sortType) {
            case EVENT_DATE:
                sort = Sort.sort(Event.class).by(Event::getEventDate).ascending();
                break;
            case VIEWS:
                sort = Sort.sort(Event.class).by(Event::getViews).descending();
                break;
            case RATE:
                sort = Sort.sort(Event.class).by(Event::getRate).descending();
                break;
            default:
                throw new IllegalArgumentException("Указан не существующий тип сортировки.");
        }

        Pageable pageable = PageRequest.of(from / size, size, sort);

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

        onlyAvailable = true;
        List<Event> events = eventsRepository.findAllByParam(
                text,
                categories,
                paid,
                startDate,
                endDate,
                onlyAvailable,
                pageable);

        StatInDto statInDto = new StatInDto();
        statInDto.setApp(Constants.APP_NAME);
        statInDto.setUri(request.getRequestURI());
        statInDto.setIp(request.getRemoteAddr());
        statInDto.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_STRING)));
        adminStatsClient.saveHit(statInDto);

        return EventMapper.eventToPublicListOutDto(events);
    }
}
