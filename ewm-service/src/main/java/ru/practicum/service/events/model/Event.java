package ru.practicum.service.events.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.service.categories.model.Category;
import ru.practicum.service.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EVENTS", schema = "PUBLIC")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 2000)
    private String annotation;
    @CreationTimestamp
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "description", length = 7000)
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    private String title;

    @Enumerated(EnumType.STRING)
    private EventState state;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

}
