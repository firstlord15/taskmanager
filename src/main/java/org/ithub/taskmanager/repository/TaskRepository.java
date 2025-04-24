package org.ithub.taskmanager.repository;

import org.ithub.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    List<Task> findByCategoryId(Long categoryId);
    @Query("SELECT t FROM Task t WHERE t.user.email = :email")
    List<Task> findTasksByUserEmail(@Param("email") String email);
}
