package ru.practicum.service.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.events.dto.EventOutDto;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.requests.dto.RequestInDto;
import ru.practicum.service.requests.dto.RequestOutDto;
import ru.practicum.service.requests.exceptions.RequestNotFoundException;
import ru.practicum.service.requests.model.RequestState;
import ru.practicum.service.requests.service.RequestsService;
import ru.practicum.service.requests.service.UsersEventsRequestsService;
import ru.practicum.service.users.exceptions.UserNotFoundException;
import ru.practicum.service.users.exceptions.UserRequestHimselfException;

import javax.validation.constraints.Positive;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Validated
public class UsersRequestsController {
    private final RequestsService requestsService;
    private final UsersEventsRequestsService usersEventsRequestsService;

    @GetMapping("/events/{eventId}/requests")
    public List<RequestOutDto> findAllEventRequests(@Positive @PathVariable Long userId,
                                                    @Positive @PathVariable Long eventId)
            throws UserNotFoundException, EventNotFoundException {
        return usersEventsRequestsService.findAllEventRequests(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventOutDto findAllEventRequestsDto(@Positive @PathVariable Long userId,
                                               @Positive @PathVariable Long eventId,
                                               @RequestBody RequestInDto requestInDto)
            throws UserNotFoundException, AccessDeniedException, RequestNotFoundException, EventNotFoundException {
        List<RequestOutDto> requestOutDtoList = new ArrayList<>();
        EventOutDto eventOutDto = new EventOutDto();
        for (Long reqId : requestInDto.getRequestIds()) {
            if (requestInDto.getStatus().equals(RequestState.REJECTED)) {
                requestOutDtoList.add(usersEventsRequestsService.rejectRequest(userId, eventId, reqId));
            } else if (requestInDto.getStatus().equals(RequestState.CONFIRMED)) {
                requestOutDtoList.add(usersEventsRequestsService.confirmRequest(userId, eventId, reqId));
            } else {
                return eventOutDto;
            }
        }
        if (requestInDto.getStatus().equals(RequestState.REJECTED)) {
            eventOutDto.setRejectedRequests(requestOutDtoList);
        } else {
            eventOutDto.setConfirmedRequests(requestOutDtoList);
        }
        return eventOutDto;
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/confirm")
    public RequestOutDto confirmRequest(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @PathVariable Long reqId)
            throws RequestNotFoundException, AccessDeniedException, UserNotFoundException {
        return usersEventsRequestsService.confirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/reject")
    public RequestOutDto rejectRequest(@PathVariable Long userId,
                                       @PathVariable Long eventId,
                                       @PathVariable Long reqId)
            throws RequestNotFoundException, AccessDeniedException, UserNotFoundException {
        return usersEventsRequestsService.rejectRequest(userId, eventId, reqId);
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/requests")
    public RequestOutDto addRequest(@PathVariable Long userId,
                                    @RequestParam Long eventId)
            throws UserNotFoundException, EventNotFoundException, UserRequestHimselfException {
        return requestsService.addRequest(userId, eventId);
    }

    @GetMapping("/requests")
    public List<RequestOutDto> findAllRequests(@PathVariable Long userId)
            throws UserNotFoundException {
        return requestsService.findAllRequests(userId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public RequestOutDto cancelRequest(@PathVariable Long userId,
                                       @PathVariable Long requestId)
            throws UserNotFoundException, RequestNotFoundException {
        return requestsService.cancelRequest(userId, requestId);
    }
}
