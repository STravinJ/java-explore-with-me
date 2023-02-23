package ru.practicum.service.requests.service;

import ru.practicum.service.events.dto.EventOutDto;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.requests.dto.RequestInDto;
import ru.practicum.service.requests.dto.RequestOutDto;
import ru.practicum.service.requests.exceptions.RequestNotFoundException;
import ru.practicum.service.users.exceptions.UserNotFoundException;
import ru.practicum.service.users.exceptions.UserRequestHimselfException;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface RequestsService {
    RequestOutDto addRequest(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException, UserRequestHimselfException;

    List<RequestOutDto> findAllRequests(Long userId) throws UserNotFoundException;

    RequestOutDto cancelRequest(Long userId, Long requestId) throws UserNotFoundException, RequestNotFoundException;

    List<RequestOutDto> rejectRequest(Long userId, Long eventId, Long[] requestId) throws RequestNotFoundException, AccessDeniedException, UserNotFoundException;

    List<RequestOutDto> confirmRequest(Long userId, Long eventId, Long[] requestId) throws UserNotFoundException, RequestNotFoundException, AccessDeniedException;

    List<RequestOutDto> findAllEventRequests(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException;

    EventOutDto updateRequestsStatusDto(Long userId, Long eventId, RequestInDto requestInDto) throws RequestNotFoundException, AccessDeniedException, UserNotFoundException;

}
