package com.mladenov.projectmanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserRoleEntity extends BaseEntity {
    @Column(nullable = false,unique = true)
    private String userRole;
    private String description;
}
