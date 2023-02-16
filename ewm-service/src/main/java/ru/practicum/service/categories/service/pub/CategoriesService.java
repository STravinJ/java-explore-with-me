package ru.practicum.service.categories.service.pub;

import ru.practicum.service.categories.dto.CategoryFullDto;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;

import java.util.List;

public interface CategoriesService {
    List<CategoryFullDto> findAllCategories(Integer from, Integer size);

    CategoryFullDto findCategoryById(Long catId) throws CategoryNotFoundException;
}
