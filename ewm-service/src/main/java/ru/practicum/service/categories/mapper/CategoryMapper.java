package ru.practicum.service.categories.mapper;

import ru.practicum.service.categories.dto.CategoryFullDto;
import ru.practicum.service.categories.dto.CategoryInDto;
import ru.practicum.service.categories.model.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {
    public static Category dtoOutToCategory(CategoryFullDto categoryFullDto) {
        return new Category(
                categoryFullDto.getId(),
                categoryFullDto.getName()
        );
    }

    public static CategoryFullDto categoryToDtoOut(Category category) {
        CategoryFullDto categoryFullDto = new CategoryFullDto();
        categoryFullDto.setId(category.getId());
        categoryFullDto.setName(category.getName());
        return categoryFullDto;
    }

    public static Category dtoInToCategory(CategoryInDto categoryInDto) {
        Category category = new Category();
        category.setName(categoryInDto.getName());
        return category;
    }

    public static List<CategoryFullDto> categoryToListDtoOut(List<Category> listCategories) {

        return listCategories.stream()
                .map(CategoryMapper::categoryToDtoOut)
                .collect(Collectors.toList());

    }
}
