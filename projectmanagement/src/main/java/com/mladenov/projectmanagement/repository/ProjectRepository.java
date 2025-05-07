package com.mladenov.projectmanagement.repository;

import com.mladenov.projectmanagement.model.entity.ProjectEntity;
import com.mladenov.projectmanagement.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    List<ProjectEntity> findByNameContainingIgnoreCaseOrId(String name, Long id);

    Optional<ProjectEntity> findByName(String name);

    @Query("SELECT DISTINCT p FROM ProjectEntity p " +
            "WHERE p.owner = :user OR :user MEMBER OF p.teamMembers")
    List<ProjectEntity> findProjectsByUserOwnershipOrTeamMembership(@Param("user") UserEntity user);
}
