package ru.practicum.service.events.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.categories.repository.CategoriesRepository;
import ru.practicum.service.events.exceptions.DateException;
import ru.practicum.service.utils.Utils;
import ru.practicum.service.events.dto.EventInDto;
import ru.practicum.service.events.dto.EventOutDto;
import ru.practicum.service.events.exceptions.EventClosedException;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.events.mapper.EventMapper;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.model.EventState;
import ru.practicum.service.events.repository.EventsRepository;
import ru.practicum.service.users.exceptions.UserNotFoundException;
import ru.practicum.service.users.repository.UsersRepository;
import ru.practicum.service.utils.Constants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventsServiceImpl implements AdminEventsService {
    private final EventsRepository eventsRepository;
    private final UsersRepository usersRepository;
    private final CategoriesRepository categoriesRepository;

    @Override
    public List<EventOutDto> findAllEvents(Long[] users, String[] states, Long[] categories, String rangeStart, String rangeEnd, Integer from, Integer size) throws UserNotFoundException, CategoryNotFoundException {
        checkUsersExitOrThrow(users);

        if (categories != null) {
            checkCategoriesExitOrThrow(categories);
        } else {
            categories = new Long[0];
        }

        List<EventState> stateList;
        if (states != null) {
            stateList = checkStatesCorrectOrThrow(states);
        } else {
            stateList = List.of();
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

    private void checkCategoriesExitOrThrow(Long[] categories) throws CategoryNotFoundException {
        for (Long catId : categories) {
            if (!categoriesRepository.existsById(catId)) {
                throw new CategoryNotFoundException("Category ID: " + catId + " not found.");
            }
        }
    }

    private void checkUsersExitOrThrow(Long[] users) throws UserNotFoundException {
        for (Long userId : users) {
            if (!usersRepository.existsById(userId)) {
                throw new UserNotFoundException("User ID: " + userId + " not found.");
            }
        }
    }

    @Override
    public EventOutDto publishEvent(Long eventId) throws EventClosedException, EventNotFoundException {
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
    public EventOutDto updateEvent(Long eventId, EventInDto eventInDto) throws EventNotFoundException, CategoryNotFoundException, DateException {
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

        return EventMapper.eventToOutDto(eventsRepository.saveAndFlush(event));
    }
}
