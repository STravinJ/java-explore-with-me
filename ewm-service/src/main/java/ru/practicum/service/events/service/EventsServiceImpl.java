package ru.practicum.service.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.categories.repository.CategoriesRepository;
import ru.practicum.service.events.dto.EventInDto;
import ru.practicum.service.events.dto.EventOutDto;
import ru.practicum.service.events.dto.EventPublicOutDto;
import ru.practicum.service.events.exceptions.DateException;
import ru.practicum.service.events.exceptions.EventClosedException;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.events.mapper.EventMapper;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.model.EventState;
import ru.practicum.service.events.model.SortType;
import ru.practicum.service.events.repository.EventsRepository;
import ru.practicum.service.stats.controller.StatsClient;
import ru.practicum.service.stats.dto.StatInDto;
import ru.practicum.service.users.exceptions.UserNotFoundException;
import ru.practicum.service.users.repository.UsersRepository;
import ru.practicum.service.utils.Constants;
import ru.practicum.service.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsServiceImpl implements EventsService {
    private final EventsRepository eventsRepository;
    private final StatsClient adminStatsClient;
    private final UsersRepository usersRepository;
    private final CategoriesRepository categoriesRepository;


    @Override
    @Transactional
    public EventOutDto addEvent(Long userId, EventInDto eventInDto) throws CategoryNotFoundException, UserNotFoundException, DateException {
        if (!categoriesRepository.existsById(eventInDto.getCategory())) {
            throw new CategoryNotFoundException("Category ID not found.");
        }
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID not found.");
        }
        if (eventInDto.getLocation() == null) {
            throw new InvalidParameterException("Location is null.");
        }
        if (eventInDto.getPaid() == null) {
            throw new InvalidParameterException("Paid is null.");
        }
        Utils.checkTimeBeforeOrThrow(eventInDto.getEventDate(), Constants.USER_TIME_HOUR_BEFORE_START);

        Event event = EventMapper.dtoInToEvent(eventInDto, categoriesRepository.getReferenceById(eventInDto.getCategory()));
        event.setInitiator(usersRepository.getReferenceById(userId));
        event.setState(EventState.PENDING);
        return EventMapper.eventToOutDto(eventsRepository.saveAndFlush(event));
    }

    @Override
    @Transactional
    public EventOutDto updateEvent(Long userId, EventInDto eventInDto)
            throws CategoryNotFoundException, UserNotFoundException, EventNotFoundException, EventClosedException, DateException {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID not found.");
        }
        Event event = eventsRepository.findById(eventInDto.getEventId()).orElseThrow(
                () -> new EventNotFoundException("Event ID not found.")
        );

        if (event.getState() == EventState.PUBLISHED) {
            throw new EventClosedException("Event is published.");
        } else if (event.getState() == EventState.CANCELED) {
            event.setState(EventState.PENDING);
        }
        if (eventInDto.getEventDate() != null) {
            if (eventInDto.getEventDate().isBefore(LocalDateTime.now())) {
                throw new DateException("Event date in past.");
            }
            event.setEventDate(eventInDto.getEventDate());
        }
        Utils.checkTimeBeforeOrThrow(event.getEventDate(), Constants.USER_TIME_HOUR_BEFORE_START);
        Utils.setNotNullParamToEntity(eventInDto, event, categoriesRepository);

        return EventMapper.eventToOutDto(eventsRepository.saveAndFlush(event));
    }

    @Override
    public List<EventOutDto> findAllEvents(Long userId, Integer from, Integer size) throws UserNotFoundException {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID not found.");
        }
        Sort sort = Sort.sort(Event.class).by(Event::getEventDate).descending();
        Pageable pageable = PageRequest.of(from / size, size, sort);
        return EventMapper.eventToListOutDto(eventsRepository.findAllByInitiatorId(userId, pageable));
    }

    @Override
    public EventOutDto getEvent(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID not found.");
        }
        Event event = eventsRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event ID not found.")
        );

        return EventMapper.eventToOutDto(event);
    }

    @Override
    public EventOutDto cancelEvent(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, EventClosedException {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID not found.");
        }
        Event event = eventsRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event ID not found.")
        );
        if (event.getState() != EventState.PENDING) {
            throw new EventClosedException("Event is not pending.");
        }
        event.setState(EventState.CANCELED);

        return EventMapper.eventToOutDto(eventsRepository.saveAndFlush(event));
    }

    @Override
    public List<EventOutDto> findAllEvents(Long[] users, String[] states, Long[] categories, String rangeStart, String rangeEnd, Integer from, Integer size) throws UserNotFoundException, CategoryNotFoundException {

        List<EventState> stateList;
        if (states != null) {
            stateList = checkStatesCorrectOrThrow(states);
        } else {
            stateList = Collections.emptyList();
        }
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

        Sort sort = Sort.sort(Event.class).by(Event::getEventDate).descending();
        Pageable pageable = PageRequest.of(from / size, size, sort);
        List<Event> eventList = eventsRepository.findAllByUsersAndStatesAndCategories(users, stateList, categories, startDate, endDate, pageable);
        return EventMapper.eventToListOutDto(eventList);
    }

    private List<EventState> checkStatesCorrectOrThrow(String[] states) {
        List<EventState> stateList = new ArrayList<>();
        for (String state : states) {
            try {
                stateList.add(EventState.valueOf(state));
            } catch (IllegalArgumentException err) {
                throw new IllegalArgumentException("Stats: " + state + " not found.");
            }
        }
        return stateList;
    }

    @Override
    public EventOutDto publishEvent(Long eventId) throws EventClosedException, EventNotFoundException, DateException {
        Event event = eventsRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event ID not found.")
        );
        if (event.getState() != EventState.PENDING) {
            throw new EventClosedException("Event is not pending.");
        }
        Utils.checkTimeBeforeOrThrow(event.getEventDate(), Constants.ADMIN_TIME_HOUR_BEFORE_START);
        event.setPublishedOn(LocalDateTime.now());
        event.setState(EventState.PUBLISHED);
        return EventMapper.eventToOutDto(eventsRepository.saveAndFlush(event));
    }

    @Override
    public EventOutDto rejectEvent(Long eventId) throws EventClosedException, EventNotFoundException {
        Event event = eventsRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event ID not found.")
        );
        if (event.getState() != EventState.PENDING) {
            throw new EventClosedException("Event is not pending.");
        }
        event.setState(EventState.CANCELED);
        return EventMapper.eventToOutDto(eventsRepository.saveAndFlush(event));
    }

    @Override
    @Transactional
    public EventOutDto updateEvent(Long eventId, EventInDto eventInDto, String stateAction) throws EventNotFoundException, CategoryNotFoundException, DateException, EventClosedException {
        Event event = eventsRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event ID not found.")
        );
        if (eventInDto.getEventDate() != null) {
            if (eventInDto.getEventDate().isBefore(LocalDateTime.now())) {
                throw new DateException("Event date in past.");
            }
            event.setEventDate(eventInDto.getEventDate());
        }
        Utils.setNotNullParamToEntity(eventInDto, event, categoriesRepository);

        eventsRepository.saveAndFlush(event);

        if (stateAction.equals("PUBLISH_EVENT")) {
            publishEvent(eventId);
        } else if (stateAction.equals("REJECT_EVENT")) {
            rejectEvent(eventId);
        } else {
            return EventMapper.eventToOutDto(event);
        }

        return EventMapper.eventToOutDto(event);
    }

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
