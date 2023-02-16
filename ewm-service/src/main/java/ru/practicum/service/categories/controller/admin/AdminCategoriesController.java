package ru.practicum.service.categories.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.categories.dto.CategoryFullDto;
import ru.practicum.service.categories.dto.CategoryInDto;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.categories.service.admin.AdminCategoriesService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCategoriesController {
    private final AdminCategoriesService adminCategoriesService;

    @PatchMapping
    public CategoryFullDto updateCategory(@Valid @RequestBody CategoryFullDto categoryFullDto) throws CategoryNotFoundException {
        log.info("Admin updateCategory: {}", categoryFullDto);
        return adminCategoriesService.updateCategory(categoryFullDto);
    }

    @PostMapping
    public CategoryFullDto addCategory(@Valid @RequestBody CategoryInDto categoryInDto) {
        log.info("Admin addCategory: {}", categoryInDto);
        return adminCategoriesService.addCategory(categoryInDto);
    }

    @DeleteMapping("{catId}")
    void removeCategory(@Positive @PathVariable Long catId) throws CategoryNotFoundException, AccessDeniedException {
        log.info("Admin removeCategory: {}", catId);
        adminCategoriesService.removeCategory(catId);
    }
}
