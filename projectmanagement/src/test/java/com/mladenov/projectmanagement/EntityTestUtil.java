package com.mladenov.projectmanagement;

import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.TaskEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import com.mladenov.projectmanagement.model.entity.UserRoleEntity;
import com.mladenov.projectmanagement.model.enums.TaskStatus;
import com.mladenov.projectmanagement.model.enums.TaskType;

public class EntityTestUtil {
    public static TaskEntity createTaskEntity(Long id, UserEntity user, ProjectEntity project) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(id);
        taskEntity.setTitle("Test " + id);
        taskEntity.setDescription("Test task description" + id);
        taskEntity.setStatus(TaskStatus.New);
        taskEntity.setType(TaskType.Bug);
        taskEntity.setCreatedBy(createUserEntity(id));
        taskEntity.setProject(project);
        return taskEntity;
    }

    public static ProjectEntity createProjectEntity(Long id) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(id);
        projectEntity.setName("Test " + id);
        projectEntity.setDescription("Test project description" + id);
        projectEntity.setOwner(createUserEntity(id));
        return projectEntity;
    }

    public static UserEntity createUserEntity(Long id) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setUsername("test" + id);
        userEntity.setPassword("123");
        userEntity.setEmail("test" + id + "@gmail.com");
        userEntity.setFirstName("Test" + id);
        userEntity.setLastName("Testov" + id);
        userEntity.setActive(true);
        userEntity.setUserRole(createUserRoleEntity("ADMIN"));
        return userEntity;
    }

    public static UserRoleEntity createUserRoleEntity(String userRole) {
        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setUserRole(userRole);
        return userRoleEntity;
    }
}
