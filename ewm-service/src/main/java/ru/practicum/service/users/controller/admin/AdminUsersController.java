package ru.practicum.service.users.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.utils.Constants;
import ru.practicum.service.users.dto.UserDto;
import ru.practicum.service.users.exceptions.UserNotFoundException;
import ru.practicum.service.users.service.AdminUsersService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminUsersController {
    private final AdminUsersService adminUsersService;

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping()
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("createUser: {}", userDto);
        return adminUsersService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getUsersByIdInByPages(@RequestParam Long[] ids,
                                               @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                               @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("(Admin)UserController.getUsersByIdInByPages: received request to get all users");
        return adminUsersService.findAll(ids, from, size);
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("{userId}")
    public void deleteUser(@Valid @Positive @PathVariable Long userId) throws UserNotFoundException {
        log.info("deleteUser: {}", userId);
        adminUsersService.deleteUserById(userId);
    }
}
