package org.ithub.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ithub.taskmanager.entity.Category;
import org.ithub.taskmanager.entity.Task;
import org.ithub.taskmanager.enums.TaskStatus;
import org.ithub.taskmanager.service.CategoryService;
import org.ithub.taskmanager.service.TaskService;
import org.ithub.taskmanager.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final TaskService taskService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        String email = principal.getName();
        log.info("Загрузка dashboard для пользователя с email: {}", email);

        List<Task> tasks = taskService.getTasksByUserEmail(email);
        model.addAttribute("tasks", tasks);

        // Добавляем информацию о категориях
        List<Category> categories = categoryService.getCategoriesByUserId(userService.getUserEntityByEmail(email).getId());
        model.addAttribute("categories", categories);

        // Добавляем счетчики для разных статусов задач
        model.addAttribute("inProgressCount", countTasksByStatus(tasks, TaskStatus.IN_PROGRESS));
        model.addAttribute("toDoCount", countTasksByStatus(tasks, TaskStatus.TO_DO));
        model.addAttribute("doneCount", countTasksByStatus(tasks, TaskStatus.DONE));

        log.info("Количество задач, полученных для пользователя {}: {}", email, tasks.size());
        log.info("Количество категорий, полученных для пользователя {}: {}", email, categories.size());

        return "dashboard";
    }

    private long countTasksByStatus(List<Task> tasks, TaskStatus status) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .count();
    }
}