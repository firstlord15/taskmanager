package org.ithub.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ithub.taskmanager.entity.Category;
import org.ithub.taskmanager.entity.Task;
import org.ithub.taskmanager.entity.User;
import org.ithub.taskmanager.service.CategoryService;
import org.ithub.taskmanager.service.TaskService;
import org.ithub.taskmanager.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final TaskService taskService;
    private final UserService userService;

    @GetMapping
    public String getAllCategories(Model model, Principal principal) {
        String email = principal.getName();
        log.info("Получение всех категорий для пользователя: {}", email);

        User user = userService.getUserEntityByEmail(email);
        List<Category> categories = categoryService.getCategoriesByUserId(user.getId());

        model.addAttribute("categories", categories);
        return "categories/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        log.info("Показ формы для создания категории");
        model.addAttribute("category", new Category());
        return "categories/create";
    }

    @PostMapping("/create")
    public String createCategory(@ModelAttribute Category category, Principal principal) {
        String email = principal.getName();
        log.info("Создание новой категории для пользователя: {}", email);

        User user = userService.getUserEntityByEmail(email);
        category.setUser(user);
        category.setCreatedAt(LocalDate.now());

        categoryService.createCategory(category);
        log.info("Категория успешно создана");

        return "redirect:/categories";
    }

    @GetMapping("/{id}")
    public String viewCategory(@PathVariable Long id, Model model, Principal principal) {
        String email = principal.getName();
        log.info("Просмотр категории с id: {} пользователем: {}", id, email);

        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        // Проверка прав доступа
        if (!category.getUser().getEmail().equals(email)) {
            log.warn("Попытка несанкционированного доступа к категории");
            return "redirect:/categories";
        }

        List<Task> tasks = taskService.getTasksByCategoryId(id);

        model.addAttribute("category", category);
        model.addAttribute("tasks", tasks);

        return "categories/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        String email = principal.getName();

        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        // Проверка прав доступа
        if (!category.getUser().getEmail().equals(email)) {
            return "redirect:/categories";
        }

        model.addAttribute("category", category);
        return "categories/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category, Principal principal) {
        String email = principal.getName();

        Category existingCategory = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        // Проверка прав доступа
        if (!existingCategory.getUser().getEmail().equals(email)) {
            return "redirect:/categories";
        }

        // Обновляем только разрешенные поля
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());

        categoryService.updateCategory(existingCategory);

        return "redirect:/categories/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, Principal principal) {
        String email = principal.getName();

        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        // Проверка прав доступа
        if (!category.getUser().getEmail().equals(email)) {
            return "redirect:/categories";
        }

        categoryService.deleteCategory(id);

        return "redirect:/categories";
    }
}