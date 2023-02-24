package ru.practicum.service.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.requests.model.Request;
import ru.practicum.service.requests.model.RequestState;

import java.util.List;

public interface RequestsRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Request r " +
            " SET r.status = ru.practicum.service.requests.model.RequestState.REJECTED " +
            " WHERE r.event.id = :eventId AND r.status = ru.practicum.service.requests.model.RequestState.PENDING " +
            " ")
    void rejectAllPendingRequestsOfEvent(Long eventId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Request r " +
            " SET r.status = ru.practicum.service.requests.model.RequestState.CONFIRMED " +
            " WHERE r.event.id IN :eventId AND r.status = ru.practicum.service.requests.model.RequestState.PENDING " +
            " ")
    void confirmAllPendingRequestOfEvents(Long[] eventId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Request r " +
            " SET r.status = ru.practicum.service.requests.model.RequestState.REJECTED " +
            " WHERE r.id IN :requestsId" +
            " ")
    void rejectAllRequests(Long[] requestsId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Request r " +
            " SET r.status = ru.practicum.service.requests.model.RequestState.CONFIRMED " +
            " WHERE r.id IN :requestsId" +
            " ")
    void confirmAllRequests(Long[] requestsId);

    @Query("SELECT r FROM Request r " +
            " JOIN Event e ON r.event.id = e.id " +
            " WHERE e.id = :eventId AND e.initiator.id = :userId "
    )
    List<Request> findAllByInitiatorIdAndEventId(Long userId, Long eventId);

    @Query("SELECT r FROM Request r " +
            " JOIN Event e ON r.event.id = e.id " +
            " WHERE e.id = :eventId AND r.status = :requestState "
    )
    List<Request> findAllByRequestStateAndEventId(Long eventId, RequestState requestState);

    @Query("SELECT r FROM Request r " +
            " JOIN Event e ON r.event.id = e.id " +
            " WHERE e.id IN :eventId AND r.status = :requestState "
    )
    List<Request> findAllByRequestStateAndEventIds(Long[] eventId, RequestState requestState);

    List<Request> findAllByIdIn(Long[] ids);

    boolean existsByRequesterIdAndEventIdAndStatus(Long userId, Long eventId, RequestState requestState);
}
