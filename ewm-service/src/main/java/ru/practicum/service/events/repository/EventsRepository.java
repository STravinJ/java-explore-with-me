package ru.practicum.service.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventsRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            " WHERE e.initiator.id IN :users " +
            " AND e.state IN :states " +
            " AND e.category.id IN :categories " +
            " AND e.eventDate BETWEEN :startDate AND :endDate "
    )
    List<Event> findAllByUsersAndStatesAndCategories(Long[] users, List<EventState> states, Long[] categories, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Boolean existsByCategoryId(Long catId);

    Boolean existsByIdAndState(Long eventId, EventState eventState);

    Optional<Event> findByIdAndState(Long eventId, EventState published);

    @Query("SELECT e FROM Event e " +
            " LEFT JOIN Request r on e.id = r.event.id AND r.status = ru.practicum.service.requests.model.RequestState.CONFIRMED" +
            " WHERE e.state = ru.practicum.service.events.model.EventState.PUBLISHED " +
            " AND (e.annotation LIKE CONCAT('%',:text,'%') OR e.description LIKE CONCAT('%',:text,'%')) " +
            " AND e.category.id IN :categories " +
            " AND e.paid = :paid " +
            " AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd) " +
            " GROUP BY e HAVING (" +
            " (:onlyAvailable = true AND e.participantLimit = 0) OR " +
            " (:onlyAvailable = true AND e.participantLimit > COUNT(r.id)) OR " +
            " (:onlyAvailable = false)" +
            ") "
    )
    List<Event> findAllByParam(String text,
                               Long[] categories,
                               Boolean paid,
                               LocalDateTime rangeStart,
                               LocalDateTime rangeEnd,
                               Boolean onlyAvailable,
                               Pageable pageable);


    int countByInitiatorId(Long userId);

    @Query("SELECT SUM(e.rate) FROM Event e " +
            " WHERE e.initiator.id = :userId"
    )
    long sumRateByInitiatorId(Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Event e " +
            " SET e.rate = e.rate + 1 " +
            " WHERE e.id = :eventId")
    void incrementRate(Long eventId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE Event e " +
            " SET e.rate = e.rate - 1 " +
            " WHERE e.id = :eventId")
    void decrementRate(Long eventId);
}
