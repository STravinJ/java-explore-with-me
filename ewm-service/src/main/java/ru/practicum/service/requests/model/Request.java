package ru.practicum.service.requests.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.users.model.User;
import ru.practicum.service.utils.Constants;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "REQUESTS", schema = "PUBLIC", uniqueConstraints = {@UniqueConstraint(columnNames = {"EVENT_ID", "REQUESTER_ID"})})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUESTER_ID")
    private User requester;
    @JsonFormat(pattern = Constants.DATE_TIME_STRING)
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    private RequestState status;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "EVENT_ID")
    private Event event;
}
