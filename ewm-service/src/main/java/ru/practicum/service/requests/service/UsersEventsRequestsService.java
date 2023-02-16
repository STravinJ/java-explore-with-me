package ru.practicum.service.requests.service;

import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.requests.dto.RequestOutDto;
import ru.practicum.service.requests.exceptions.RequestNotFoundException;
import ru.practicum.service.users.exceptions.UserNotFoundException;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface UsersEventsRequestsService {
    RequestOutDto rejectRequest(Long userId, Long eventId, Long requestId) throws RequestNotFoundException, AccessDeniedException, UserNotFoundException;

    RequestOutDto confirmRequest(Long userId, Long eventId, Long requestId) throws UserNotFoundException, RequestNotFoundException, AccessDeniedException;

    List<RequestOutDto> findAllEventRequests(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException;
}
