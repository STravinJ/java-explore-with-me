package ru.practicum.service.categories.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Update;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import ru.practicum.service.categories.dto.CategoryFullDto;
import ru.practicum.service.categories.dto.CategoryInDto;
import ru.practicum.service.categories.exceptions.CategoryNotFoundException;
import ru.practicum.service.categories.service.admin.AdminCategoriesService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCategoriesController {
    private final AdminCategoriesService adminCategoriesService;

    @PatchMapping("/{catid}")
    public CategoryFullDto updateCategory(@PathVariable long catid,
            @RequestBody CategoryInDto categoryInDto) throws CategoryNotFoundException {
        log.info("Admin updateCategory: {}", categoryInDto);
        return adminCategoriesService.updateCategory(catid, categoryInDto);
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public CategoryFullDto addCategory(@Valid @RequestBody CategoryInDto categoryInDto) {
        log.info("Admin addCategory: {}", categoryInDto);
        return adminCategoriesService.addCategory(categoryInDto);
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("{catId}")
    void removeCategory(@Positive @PathVariable Long catId) throws CategoryNotFoundException, AccessDeniedException {
        log.info("Admin removeCategory: {}", catId);
        adminCategoriesService.removeCategory(catId);
    }
}
