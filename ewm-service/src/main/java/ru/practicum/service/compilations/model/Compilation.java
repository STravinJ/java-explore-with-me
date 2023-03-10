package ru.practicum.service.compilations.model;

import lombok.*;
import ru.practicum.service.events.model.Event;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "COMPILATIONS", schema = "PUBLIC")
@ToString(exclude = "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Boolean pinned;

    @ManyToMany
    @JoinTable(name = "COMPILATIONS_EVENTS",
            joinColumns = @JoinColumn(name = "COMPILATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    private List<Event> events;
}
