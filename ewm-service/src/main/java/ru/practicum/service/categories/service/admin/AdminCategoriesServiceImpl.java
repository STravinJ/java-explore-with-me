package ru.practicum.service.categories.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.categories.dto.CategoryFullDto;
import ru.practicum.service.categories.dto.CategoryInDto;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.categories.mapper.CategoryMapper;
import ru.practicum.service.categories.model.Category;
import ru.practicum.service.categories.repository.CategoriesRepository;
import ru.practicum.service.events.exceptions.EventNotFoundException;
import ru.practicum.service.events.mapper.EventMapper;
import ru.practicum.service.events.model.Event;
import ru.practicum.service.events.repository.EventsRepository;
import ru.practicum.service.utils.Utils;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class AdminCategoriesServiceImpl implements AdminCategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final EventsRepository eventsRepository;

    @Override
    public CategoryFullDto updateCategory(Long catId, CategoryInDto categoryInDto) throws CategoryNotFoundException {
        Category category = categoriesRepository.findById(catId).orElseThrow(
                () -> new CategoryNotFoundException("Category ID not found.")
        );
        category.setName(categoryInDto.getName());
        return CategoryMapper.categoryToDtoOut(categoriesRepository.save(category));
    }

    @Override
    public CategoryFullDto addCategory(CategoryInDto categoryInDto) {
        Category category = CategoryMapper.dtoInToCategory(categoryInDto);
        return CategoryMapper.categoryToDtoOut(categoriesRepository.save(category));
    }

    @Override
    @Transactional
    public void removeCategory(Long catId) throws CategoryNotFoundException, AccessDeniedException {
        checkCategoryExists(catId);
        if (eventsRepository.existsByCategoryId(catId)) {
            throw new AccessDeniedException("Category in use.");
        }
        categoriesRepository.deleteById(catId);
    }

    private void checkCategoryExists(Long catId) throws CategoryNotFoundException {
        if (!categoriesRepository.existsById(catId)) {
            throw new CategoryNotFoundException("Category ID was not found.");
        }
    }

}
