package com.mladenov.projectmanagement.repository;

import com.mladenov.projectmanagement.model.entity.TaskCommentEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, Long> {
    List<TaskCommentEntity> findAllByTask(TaskEntity task);
}
