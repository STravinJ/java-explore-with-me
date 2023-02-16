package ru.practicum.service.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.service.categories.controller.admin.AdminCategoriesController;
import ru.practicum.service.categories.controller.pub.PublicCategoriesController;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.compilations.controller.admin.AdminCompilationsController;
import ru.practicum.service.compilations.controller.pub.PublicCompilationsController;
import ru.practicum.service.compilations.exceptions.CompilationNotFoundException;
import ru.practicum.service.events.controller.admin.AdminEventsController;
import ru.practicum.service.events.controller.pub.PublicEventsController;
import ru.practicum.service.events.controller.user.UsersEventsController;
import ru.practicum.service.events.exceptions.EventClosedException;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.requests.controller.UsersEventsRequestsController;
import ru.practicum.service.requests.controller.UsersRequestsController;
import ru.practicum.service.requests.exceptions.RequestNotFoundException;
import ru.practicum.service.users.controller.admin.AdminUsersController;
import ru.practicum.service.users.exceptions.UserNotFoundException;
import ru.practicum.service.users.exceptions.UserRequestHimselfException;

import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;

@RestControllerAdvice(
        assignableTypes = {
                AdminUsersController.class,
                AdminCategoriesController.class,
                AdminCompilationsController.class,
                AdminEventsController.class,
                UsersEventsController.class,
                UsersRequestsController.class,
                UsersEventsRequestsController.class,
                PublicCategoriesController.class,
                PublicCompilationsController.class,
                PublicEventsController.class
        })
@Slf4j
public class ErrorHandler {
    private ErrorResponse errorResponse;

    @ExceptionHandler({
            CategoryNotFoundException.class,
            EventNotFoundException.class,
            UserNotFoundException.class,
            RequestNotFoundException.class,
            CompilationNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final Exception e) {
        log.info("Error handleNotFound: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            MethodArgumentNotValidException.class,
            InvalidParameterException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleBadRequest(final Exception e) {
        log.info("Error handleBadRequest: {}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorMessage(
                e.getClass().getSimpleName(),
                e.getMessage(),
                "",
                "BAD_REQUEST",
                Constants.DATE_TIME_SPACE.format(LocalDateTime.now()));
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class,
            UserRequestHimselfException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleArgumentBadRequest(final Exception e) {
        log.info("Error handleArgumentBadRequest: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({
            EventClosedException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDoubleData(final Exception e) {
        log.info("Error handleDoubleData: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({
            AccessDeniedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(final Exception e) {
        log.info("Error handleForbidden: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllError(final Throwable e) {
        log.info("Error handleAllError: {}", e.getMessage());
        return new ErrorResponse("Произошла непредвиденная ошибка. " + e.getMessage());
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    private static class ErrorMessage {
        private String errors;
        private String message;
        private String reason;
        private String status;
        private String timestamp;
    }
}
