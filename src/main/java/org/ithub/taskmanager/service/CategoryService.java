package org.ithub.taskmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ithub.taskmanager.entity.Category;
import org.ithub.taskmanager.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        log.info("Creating category for user: {}", category.getUser().getEmail());
        category.setCreatedAt(LocalDate.now());
        return categoryRepository.save(category);
    }

    public Category updateCategory(Category category) {
        log.info("Updating category with id: {}", category.getId());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);
        categoryRepository.deleteById(id);
    }

    public Optional<Category> getCategoryById(Long id) {
        log.info("Fetching category by id: {}", id);
        return categoryRepository.findById(id);
    }

    public List<Category> getCategoriesByUserId(Long userId) {
        log.info("Fetching categories by user id: {}", userId);
        return categoryRepository.findByUserId(userId);
    }
}
