package com.mladenov.projectmanagement.repository;

import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity> {

    Page<TaskEntity> findAllByCreatedByOrAssignedTo(UserEntity createdBy, UserEntity assignedTo, Pageable pageable);

    Page<TaskEntity> findAllByProject(ProjectEntity project, Pageable pageable);
}
