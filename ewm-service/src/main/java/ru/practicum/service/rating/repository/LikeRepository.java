package ru.practicum.service.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.service.rating.model.Like;
import ru.practicum.service.rating.model.LikeType;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByEventIdAndUserId(Long userId, Long eventId);

    @Query(value = "SELECT l.eventId, SUM(CASE l.type" +
            "         WHEN 'LIKE' THEN 1 ELSE -1 END) FROM Like l " +
            " WHERE l.eventId IN :eventId " +
            " GROUP BY l.eventId"
    )
    List<Object[]> findAllWithRateByEventIdIn(List<Long> eventId);

    Optional<Like> findLikeByUserIdAndEventIdAndType(Long userId, Long eventId, LikeType likeType);

    @Query("SELECT SUM(CASE l.type" +
            "         WHEN 'LIKE' THEN 1 ELSE -1 END) as rate FROM Like l " +
            " WHERE l.eventId = :eventId"
    )
    Optional<Long> findRateByEventId(Long eventId);
}