package ru.practicum.service.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.service.users.dto.UserDto;
import ru.practicum.service.users.exceptions.UserNotFoundException;
import ru.practicum.service.users.mapper.UserMapper;
import ru.practicum.service.users.model.User;
import ru.practicum.service.users.repository.UsersRepository;

import java.security.InvalidParameterException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUsersServiceImpl implements AdminUsersService {
    private final UsersRepository usersRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new InvalidParameterException("User Email is empty.");
        }
        User user = usersRepository.save(UserMapper.dtoToUser(userDto));
        return UserMapper.userToDto(user);
    }

    @Override
    public List<UserDto> findAll(Long[] ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return UserMapper.userListToDto(usersRepository.findAllByIdIn(ids, pageable));
    }

    @Override
    public boolean existById(Long userId) {
        return usersRepository.existsById(userId);
    }

    @Override
    public void deleteUserById(Long userId) throws UserNotFoundException {
        if (!usersRepository.existsById(userId)) {
            throw new UserNotFoundException("User with was not found.");
        }
        usersRepository.deleteById(userId);
    }
}
