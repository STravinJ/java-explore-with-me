package ru.practicum.service.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.categories.dto.CategoryFullDto;
import ru.practicum.service.categories.dto.CategoryInDto;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.categories.mapper.CategoryMapper;
import ru.practicum.service.categories.model.Category;
import ru.practicum.service.categories.repository.CategoriesRepository;
import ru.practicum.service.events.repository.EventsRepository;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final EventsRepository eventsRepository;

    @Override
    public List<CategoryFullDto> findAllCategories(Integer from, Integer size) {
        Sort sort = Sort.sort(Category.class).by(Category::getName).ascending();
        Pageable pageable = PageRequest.of(from / size, size, sort);

        return CategoryMapper.categoryToListDtoOut(categoriesRepository.findAll(pageable).toList());
    }

    @Override
    public CategoryFullDto findCategoryById(Long catId) throws CategoryNotFoundException {
        return CategoryMapper.categoryToDtoOut(categoriesRepository.findById(catId).orElseThrow(
                () -> new CategoryNotFoundException("Category ID was not found.")
        ));
    }

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
