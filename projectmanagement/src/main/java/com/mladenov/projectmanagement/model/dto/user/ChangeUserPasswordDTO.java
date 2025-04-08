package com.mladenov.projectmanagement.model.dto.user;

import com.mladenov.projectmanagement.util.validation.FieldMatch;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldMatch(first = "newPassword", second = "confirmPassword", message = "Passwords do not match.")
public class ChangeUserPasswordDTO {
    @NotEmpty(message = "Old password is required.")
    private String oldPassword;
    @NotEmpty(message = "New password is required.")
    private String newPassword;
    @NotEmpty(message = "Confirm password is required.")
    private String confirmPassword;
}
