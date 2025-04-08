package com.mladenov.projectmanagement.repository;

import com.mladenov.projectmanagement.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<UserEntity> searchByUsername(@Param("username") String username);
}
