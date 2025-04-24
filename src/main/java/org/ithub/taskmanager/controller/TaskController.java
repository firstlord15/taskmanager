package org.ithub.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ithub.taskmanager.entity.Category;
import org.ithub.taskmanager.entity.Task;
import org.ithub.taskmanager.entity.User;
import org.ithub.taskmanager.enums.TaskPriority;
import org.ithub.taskmanager.enums.TaskStatus;
import org.ithub.taskmanager.service.CategoryService;
import org.ithub.taskmanager.service.TaskService;
import org.ithub.taskmanager.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping
    public String getAllTasks(Model model, Principal principal) {
        String email = principal.getName();
        log.info("Получение всех задач для пользователя: {}", email);

        List<Task> tasks = taskService.getTasksByUserEmail(email);
        model.addAttribute("tasks", tasks);
        return "tasks/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, Principal principal) {
        String email = principal.getName();
        log.info("Показ формы для создания задачи пользователю: {}", email);

        User user = userService.getUserEntityByEmail(email);
        List<Category> categories = categoryService.getCategoriesByUserId(user.getId());

        model.addAttribute("task", new Task());
        model.addAttribute("categories", categories);
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("statuses", TaskStatus.values());

        return "tasks/create";
    }

    @PostMapping("/create")
    public String createTask(@ModelAttribute Task task,
                             @RequestParam(required = false) Long categoryId,
                             Principal principal) {
        String email = principal.getName();
        log.info("Создание новой задачи для пользователя: {}", email);

        User user = userService.getUserEntityByEmail(email);
        task.setUser(user);

        if (categoryId != null) {
            categoryService.getCategoryById(categoryId).ifPresent(task::setCategory);
        }

        task.setCreatedAt(LocalDate.now());
        task.setUpdatedAt(LocalDate.now());

        taskService.createTask(task);
        log.info("Задача успешно создана");

        return "redirect:/dashboard";
    }

    @GetMapping("/{id}")
    public String viewTask(@PathVariable Long id, Model model, Principal principal) {
        String email = principal.getName();
        log.info("Просмотр задачи с id: {} пользователем: {}", id, email);

        Task task = taskService.getTaskById(id);

        // Проверка прав доступа - пользователь может просматривать только свои задачи
        if (!task.getUser().getEmail().equals(email)) {
            log.warn("Попытка несанкционированного доступа к задаче");
            return "redirect:/dashboard";
        }

        model.addAttribute("task", task);
        return "tasks/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        String email = principal.getName();
        Task task = taskService.getTaskById(id);

        // Проверка прав доступа
        if (!task.getUser().getEmail().equals(email)) {
            return "redirect:/dashboard";
        }

        User user = userService.getUserEntityByEmail(email);
        List<Category> categories = categoryService.getCategoriesByUserId(user.getId());

        model.addAttribute("task", task);
        model.addAttribute("categories", categories);
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("statuses", TaskStatus.values());

        return "tasks/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute Task task,
                             @RequestParam(required = false) Long categoryId,
                             Principal principal) {
        String email = principal.getName();
        Task existingTask = taskService.getTaskById(id);

        // Проверка прав доступа
        if (!existingTask.getUser().getEmail().equals(email)) {
            return "redirect:/dashboard";
        }

        // Обновляем только разрешенные поля
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setStatus(task.getStatus());
        existingTask.setPriority(task.getPriority());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setUpdatedAt(LocalDate.now());

        if (categoryId != null) {
            categoryService.getCategoryById(categoryId).ifPresent(existingTask::setCategory);
        } else {
            existingTask.setCategory(null);
        }

        taskService.updateTask(existingTask);

        return "redirect:/tasks/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id, Principal principal) {
        String email = principal.getName();
        Task task = taskService.getTaskById(id);

        // Проверка прав доступа
        if (!task.getUser().getEmail().equals(email)) {
            return "redirect:/dashboard";
        }

        taskService.deleteTask(id);

        return "redirect:/dashboard";
    }
}