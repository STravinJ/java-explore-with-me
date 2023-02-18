package ru.practicum.service.categories.service.admin;

import ru.practicum.service.categories.dto.CategoryFullDto;
import ru.practicum.service.categories.dto.CategoryInDto;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;

import java.nio.file.AccessDeniedException;

public interface AdminCategoriesService {
    CategoryFullDto updateCategory(Long catId, CategoryInDto categoryInDto) throws CategoryNotFoundException;

    CategoryFullDto addCategory(CategoryInDto categoryInDto);

    void removeCategory(Long catId) throws CategoryNotFoundException, AccessDeniedException;
}
