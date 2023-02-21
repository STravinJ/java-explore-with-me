package ru.practicum.service.categories.service;

import ru.practicum.service.categories.dto.CategoryFullDto;
import ru.practicum.service.categories.dto.CategoryInDto;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface CategoriesService {
    List<CategoryFullDto> findAllCategories(Integer from, Integer size);

    CategoryFullDto findCategoryById(Long catId) throws CategoryNotFoundException;

    CategoryFullDto updateCategory(Long catId, CategoryInDto categoryInDto) throws CategoryNotFoundException;

    CategoryFullDto addCategory(CategoryInDto categoryInDto);

    void removeCategory(Long catId) throws CategoryNotFoundException, AccessDeniedException;
}
