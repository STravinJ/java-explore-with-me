package ru.practicum.service.categories.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryInDto {
    @NotBlank
    private String name;
}
