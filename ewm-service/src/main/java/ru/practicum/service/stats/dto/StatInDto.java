package ru.practicum.service.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.service.utils.Constants;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class StatInDto {
    @NotNull
    private String app;
    @NotNull
    private String uri;
    @NotNull
    private String ip;
    @NotNull
    @JsonFormat(pattern = Constants.DATE_TIME_STRING)
    private String timestamp;

    public StatInDto(String app, String uri, String ip, LocalDateTime timestamp) {

        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = timestamp.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_STRING));

    }
}
