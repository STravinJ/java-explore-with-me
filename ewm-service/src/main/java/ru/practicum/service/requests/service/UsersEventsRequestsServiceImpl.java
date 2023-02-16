package ru.practicum.service.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.repository.EventsRepository;
import ru.practicum.service.requests.dto.RequestOutDto;
import ru.practicum.service.requests.exceptions.RequestNotFoundException;
import ru.practicum.service.requests.mapper.RequestMapper;
import ru.practicum.service.requests.model.Request;
import ru.practicum.service.requests.model.RequestState;
import ru.practicum.service.requests.repository.RequestsRepository;
import ru.practicum.service.users.exceptions.UserNotFoundException;
import ru.practicum.service.users.repository.UsersRepository;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersEventsRequestsServiceImpl implements UsersEventsRequestsService {
    private final RequestsRepository requestsRepository;
    private final UsersRepository usersRepository;
    private final EventsRepository eventsRepository;

    @Override
    @Transactional
    public RequestOutDto confirmRequest(Long userId, Long eventId, Long requestId) throws UserNotFoundException, RequestNotFoundException, AccessDeniedException {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID not found.");
        }
        Request request = requestsRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFoundException("Request ID not found.")
        );
        if (request.getStatus() != RequestState.PENDING) {
            throw new IllegalStateException("Request status can be PENDING.");
        }
        if (!request.getEvent().getId().equals(eventId)) {
            throw new IllegalArgumentException("Wrong Event ID for this Request.");
        }
        if (!request.getEvent().getInitiator().getId().equals(userId)) {
            throw new AccessDeniedException("Only owner of Event can Reject Request.");
        }
        Event event = request.getEvent();
        if (event.getParticipantLimit() != 0 && (event.getParticipantLimit() - event.getConfirmedRequests()) <= 0) {
            requestsRepository.rejectAllPendingRequest(eventId);
            throw new IllegalStateException("Event don't have any free slot.");
        }

        if (request.getStatus() == RequestState.PENDING) {
            request.setStatus(RequestState.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            requestsRepository.saveAndFlush(request);
        }

        return RequestMapper.requestToOutDto(request);
    }

    @Override
    public RequestOutDto rejectRequest(Long userId, Long eventId, Long requestId) throws RequestNotFoundException, AccessDeniedException, UserNotFoundException {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID not found.");
        }
        Request request = requestsRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFoundException("Request ID not found.")
        );
        if (!request.getEvent().getId().equals(eventId)) {
            throw new IllegalArgumentException("Wrong Event ID for this Request.");
        }
        if (!request.getEvent().getInitiator().getId().equals(userId)) {
            throw new AccessDeniedException("Only owner of Event can Reject Request.");
        }
        request.setStatus(RequestState.REJECTED);
        return RequestMapper.requestToOutDto(requestsRepository.saveAndFlush(request));
    }

    @Override
    public List<RequestOutDto> findAllEventRequests(Long userId, Long eventId) throws UserNotFoundException, EventNotFoundException {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID not found.");
        }
        if (!eventsRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event ID not found.");
        }
        return RequestMapper.requestsToListOutDto(requestsRepository.findAllByInitiatorIdAndEventId(userId, eventId));
    }
}
