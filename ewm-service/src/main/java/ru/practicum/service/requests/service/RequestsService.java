package ru.practicum.service.requests.service;

import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.requests.dto.RequestOutDto;
import ru.practicum.service.requests.exceptions.RequestNotFoundException;
import ru.practicum.service.users.exceptions.UserNotFoundException;
import ru.practicum.service.users.exceptions.UserRequestHimselfException;

import java.util.List;

public interface RequestsService {
    RequestOutDto addRequest(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, UserRequestHimselfException;

    List<RequestOutDto> findAllRequests(Long userId) throws UserNotFoundException;

    RequestOutDto cancelRequest(Long userId, Long requestId) throws UserNotFoundException, RequestNotFoundException;
}
