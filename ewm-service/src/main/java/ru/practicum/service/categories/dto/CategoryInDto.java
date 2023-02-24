package ru.practicum.service.categories.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CategoryInDto {
    @NotBlank
    @NotNull
    private String name;
}
