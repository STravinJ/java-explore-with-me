package ru.practicum.service.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.service.categories.dto.CategoryFullDto;
import ru.practicum.service.events.model.EventState;
import ru.practicum.service.users.dto.UserPublicDto;
import ru.practicum.service.utils.Constants;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventPublicOutDto {
    private String annotation;
    private CategoryFullDto category;
    private UserPublicDto initiator;
    private LocationDto location;
    private String title;
    private Integer confirmedRequests;
    @JsonFormat(pattern = Constants.DATE_TIME_STRING)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = Constants.DATE_TIME_STRING)
    private LocalDateTime eventDate;
    private Long id;
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(pattern = Constants.DATE_TIME_STRING)
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private Long views;
    private Integer rate;
}
