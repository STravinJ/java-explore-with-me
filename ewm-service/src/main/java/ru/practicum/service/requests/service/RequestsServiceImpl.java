package ru.practicum.service.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.events.dto.EventOutDto;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.model.EventState;
import ru.practicum.service.events.repository.EventsRepository;
import ru.practicum.service.requests.dto.RequestInDto;
import ru.practicum.service.requests.dto.RequestOutDto;
import ru.practicum.service.requests.exceptions.RequestNotFoundException;
import ru.practicum.service.requests.mapper.RequestMapper;
import ru.practicum.service.requests.model.Request;
import ru.practicum.service.requests.model.RequestState;
import ru.practicum.service.requests.repository.RequestsRepository;
import ru.practicum.service.users.exceptions.UserNotFoundException;
import ru.practicum.service.users.exceptions.UserRequestHimselfException;
import ru.practicum.service.users.model.User;
import ru.practicum.service.users.repository.UsersRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestsServiceImpl implements RequestsService {
    private final RequestsRepository requestsRepository;
    private final UsersRepository usersRepository;
    private final EventsRepository eventsRepository;

    @Override
    @Transactional
    public List<RequestOutDto> confirmRequest(Long userId, Long eventId, Long[] requestId) throws UserNotFoundException, RequestNotFoundException, AccessDeniedException {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID not found.");
        }

        List<Request> requests = requestsRepository.findAllByIdIn(requestId);
        Boolean rejectAllPendingRequest = false;
        for (Request request : requests) {
            if (rejectAllPendingRequest) {
                requestsRepository.rejectAllPendingRequest(eventId);
                throw new IllegalStateException("Event don't have any free slot.");
            }
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
            Integer confirmedRequests = 0;
            Integer participantLimit = event.getParticipantLimit();
            if (participantLimit != 0) {
                confirmedRequests = requestsRepository.findAllByRequestStateAndEventId(eventId, RequestState.CONFIRMED).size();
                if (participantLimit - confirmedRequests <= 0) {
                    requestsRepository.rejectAllPendingRequest(eventId);
                    throw new IllegalStateException("Event don't have any free slot.");
                }
            }

            request.setStatus(RequestState.CONFIRMED);
            requestsRepository.saveAndFlush(request);
            if (participantLimit != 0 && (participantLimit - confirmedRequests - 1) <= 0) {
                rejectAllPendingRequest = true;
            }

        }

        return RequestMapper.requestsToListOutDto(requests);
    }

    @Override
    public EventOutDto updateRequestsStatusDto(Long userId, Long eventId, RequestInDto requestInDto) throws UserNotFoundException, RequestNotFoundException, AccessDeniedException {

        EventOutDto eventOutDto = new EventOutDto();
        if (requestInDto.getStatus().equals(RequestState.REJECTED)) {
            eventOutDto.setRejectedRequests(rejectRequest(userId, eventId, requestInDto.getRequestIds()));
        } else if (requestInDto.getStatus().equals(RequestState.CONFIRMED)) {
            eventOutDto.setConfirmedRequests(confirmRequest(userId, eventId, requestInDto.getRequestIds()));
        } else {
            return eventOutDto;
        }
        return eventOutDto;
    }

    @Override
    public List<RequestOutDto> rejectRequest(Long userId, Long eventId, Long[] requestId) throws RequestNotFoundException, AccessDeniedException, UserNotFoundException {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID not found.");
        }
        List<Request> requests = requestsRepository.findAllByIdIn(requestId);
        for (Request request : requests) {
            if (!request.getEvent().getId().equals(eventId)) {
                throw new IllegalArgumentException("Wrong Event ID for this Request.");
            }
            if (!request.getEvent().getInitiator().getId().equals(userId)) {
                throw new AccessDeniedException("Only owner of Event can Reject Request.");
            }
            if (request.getStatus().equals(RequestState.CONFIRMED)) {
                throw new AccessDeniedException("The Request is already confirmed.");
            }
            request.setStatus(RequestState.REJECTED);
            requestsRepository.saveAndFlush(request);
        }
        return RequestMapper.requestsToListOutDto(requests);
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

    @Override
    public RequestOutDto addRequest(Long userId, Long eventId)
            throws UserNotFoundException, EventNotFoundException, UserRequestHimselfException {

        User user = usersRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User ID not found.")
        );
        Event event = eventsRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException("Event ID not found.")
        );
        if (event.getInitiator().getId().equals(userId)) {
            throw new UserRequestHimselfException("User can't request himself.");
        }
        if (event.getParticipantLimit() != 0 && (event.getParticipantLimit() - requestsRepository.findAllByRequestStateAndEventId(eventId, RequestState.CONFIRMED).size()) <= 0) {
            throw new IllegalStateException("Event don't have any free slot.");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalStateException("Event must be PUBLISHED.");
        }

        RequestState newRequestState = RequestState.PENDING;
        if (!event.getRequestModeration()) {
            newRequestState = RequestState.CONFIRMED;
        }

        Request request = new Request(
                null,
                user,
                LocalDateTime.now(),
                newRequestState,
                event
        );
        return RequestMapper.requestToOutDto(requestsRepository.saveAndFlush(request));
    }

    @Override
    public List<RequestOutDto> findAllRequests(Long userId) throws UserNotFoundException {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID not found.");
        }
        return RequestMapper.requestsToListOutDto(requestsRepository.findAllByRequesterId(userId));
    }

    @Override
    public RequestOutDto cancelRequest(Long userId, Long requestId) throws UserNotFoundException, RequestNotFoundException {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User ID not found.");
        }
        Request request = requestsRepository.findById(requestId).orElseThrow(
                () -> new RequestNotFoundException("Request ID not found.")
        );
        request.setStatus(RequestState.CANCELED);
        return RequestMapper.requestToOutDto(requestsRepository.saveAndFlush(request));
    }
}
