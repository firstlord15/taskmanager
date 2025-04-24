package org.ithub.taskmanager.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ithub.taskmanager.entity.Task;
import org.ithub.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Task createTask(Task task) {
        log.info("Creating task for user: {}", task.getUser().getEmail());
        task.setCreatedAt(LocalDate.now());
        task.setUpdatedAt(LocalDate.now());
        return taskRepository.save(task);
    }

    public Task updateTask(Task task) {
        log.info("Updating task with id: {}", task.getId());
        task.setUpdatedAt(LocalDate.now());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        log.info("Deleting task with id: {}", id);
        taskRepository.deleteById(id);
    }

    public List<Task> getTasksByUserEmail(String email) {
        log.info("Fetching tasks for user email: {}", email);
        return taskRepository.findTasksByUserEmail(email);
    }

    public Task getTaskById(Long id) {
        log.info("Fetching task by id: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Can't find task with id: " + id));
    }

    public List<Task> getTasksByUserId(Long userId) {
        log.info("Fetching tasks by user id: {}", userId);
        return taskRepository.findByUserId(userId);
    }

    public List<Task> getTasksByCategoryId(Long categoryId) {
        log.info("Fetching tasks by category id: {}", categoryId);
        return taskRepository.findByCategoryId(categoryId);
    }
}