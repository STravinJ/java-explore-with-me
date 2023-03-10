package ru.practicum.service.events.mapper;

import ru.practicum.service.categories.mapper.CategoryMapper;
import ru.practicum.service.categories.model.Category;
import ru.practicum.service.events.dto.EventInDto;
import ru.practicum.service.events.dto.EventOutDto;
import ru.practicum.service.events.dto.EventPublicOutDto;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.users.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class EventMapper {
    public static Event dtoInToEvent(EventInDto eventInDto, Category category) {
        return Event.builder()
                .annotation(eventInDto.getAnnotation())
                .category(category)
                .description(eventInDto.getDescription())
                .location(LocationMapper.dtoToLocation(eventInDto.getLocation()))
                .title(eventInDto.getTitle())
                .eventDate(eventInDto.getEventDate())
                .paid(eventInDto.getPaid())
                .participantLimit(eventInDto.getParticipantLimit())
                .requestModeration(eventInDto.getRequestModeration())
                .build();
    }

    public static EventOutDto eventToOutDto(Event event) {
        return EventOutDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.categoryToDtoOut(event.getCategory()))
                .initiator(UserMapper.userToDto(event.getInitiator()))
                .location(LocationMapper.locationToDto(event.getLocation()))
                .title(event.getTitle())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .build();
    }

    public static List<EventOutDto> eventToListOutDto(List<Event> listEvents) {
        List<EventOutDto> eventOutDtoList = new ArrayList<>();
        for (Event event : listEvents) {
            eventOutDtoList.add(eventToOutDto(event));
        }
        return eventOutDtoList;
    }


    public static EventPublicOutDto eventToPublicOutDto(Event event) {
        return EventPublicOutDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.categoryToDtoOut(event.getCategory()))
                .initiator(UserMapper.userToPublicDto(event.getInitiator()))
                .location(LocationMapper.locationToDto(event.getLocation()))
                .title(event.getTitle())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .build();
    }

    public static List<EventPublicOutDto> eventToPublicListOutDto(List<Event> listEvents) {
        List<EventPublicOutDto> eventOutDtoList = new ArrayList<>();
        for (Event event : listEvents) {
            eventOutDtoList.add(eventToPublicOutDto(event));
        }
        return eventOutDtoList;
    }

}
