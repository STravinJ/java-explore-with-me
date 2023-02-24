package ru.practicum.service.categories.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CategoryFullDto {
    @NotNull(message = "id should not be empty")
    private Long id;
    @NotBlank(message = "name should not be empty")
    private String name;
}
