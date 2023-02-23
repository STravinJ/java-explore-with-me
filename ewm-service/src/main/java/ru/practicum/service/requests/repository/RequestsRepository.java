package ru.practicum.service.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.requests.model.Request;
import ru.practicum.service.requests.model.RequestState;
import ru.practicum.service.users.model.User;

import java.util.List;

public interface RequestsRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequesterId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Request r " +
            " SET r.status = ru.practicum.service.requests.model.RequestState.REJECTED " +
            " WHERE r.event.id = :eventId AND r.status = ru.practicum.service.requests.model.RequestState.PENDING " +
            " ")
    void rejectAllPendingRequest(Long eventId);

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

    List<Request> findAllByIdIn(Long[] ids);

    boolean existsByRequesterIdAndEventIdAndStatus(Long userId, Long eventId, RequestState requestState);
}
