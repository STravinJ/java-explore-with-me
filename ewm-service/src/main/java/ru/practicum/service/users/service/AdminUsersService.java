package ru.practicum.service.users.service;

import ru.practicum.service.users.dto.UserDto;
import ru.practicum.service.users.exceptions.UserNotFoundException;

import java.util.List;

public interface AdminUsersService {
    UserDto createUser(UserDto userDto);

    List<UserDto> findAll(Long[] ids, Integer from, Integer size);

    boolean existById(Long userId);

    void deleteUserById(Long userId) throws UserNotFoundException;
}