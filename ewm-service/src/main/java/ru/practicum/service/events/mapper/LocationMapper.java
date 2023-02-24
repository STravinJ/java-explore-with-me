package ru.practicum.service.events.mapper;

import ru.practicum.service.events.dto.LocationDto;
import ru.practicum.service.events.model.Location;

public class LocationMapper {

    public static LocationDto locationToDto(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }

    public static Location dtoToLocation(LocationDto locationDto) {
        return new Location(
                null,
                locationDto.getLat(),
                locationDto.getLon()
        );
    }
}
