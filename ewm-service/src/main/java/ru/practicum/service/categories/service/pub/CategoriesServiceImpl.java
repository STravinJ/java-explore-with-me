package ru.practicum.service.categories.service.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.service.categories.dto.CategoryFullDto;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.categories.mapper.CategoryMapper;
import ru.practicum.service.categories.model.Category;
import ru.practicum.service.categories.repository.CategoriesRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {
    private final CategoriesRepository categoriesRepository;

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
}