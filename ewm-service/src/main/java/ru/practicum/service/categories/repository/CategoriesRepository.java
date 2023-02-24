package ru.practicum.service.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.service.categories.model.Category;

public interface CategoriesRepository extends JpaRepository<Category, Long> {

}
