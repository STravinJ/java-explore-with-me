package ru.practicum.service.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import ru.practicum.service.users.exceptions.UserRequestHimselfException;
import ru.practicum.service.users.model.User;
import ru.practicum.service.users.repository.UsersRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestsServiceImpl implements RequestsService {
    private final RequestsRepository requestsRepository;
    private final UsersRepository usersRepository;
    private final EventsRepository eventsRepository;

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
        //if (event.getState() != EventState.PUBLISHED) {
          //  throw new IllegalStateException("Event don't PUBLISHED.");
        //}
        if (event.getParticipantLimit() != 0 && (event.getParticipantLimit() - event.getConfirmedRequests()) <= 0) {
            throw new IllegalStateException("Event don't have any free slot.");
        }

        RequestState newRequestState = RequestState.PENDING;
        if (!event.getRequestModeration()) {
            newRequestState = RequestState.CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
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
