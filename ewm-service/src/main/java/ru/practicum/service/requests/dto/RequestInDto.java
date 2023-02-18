package ru.practicum.service.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.service.requests.model.RequestState;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestInDto {
    private Long[] requestIds;
    private RequestState status;
}
