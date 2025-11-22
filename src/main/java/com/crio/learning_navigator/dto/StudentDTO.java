package com.crio.learning_navigator.dto;

// import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    @NotBlank
    @Size(min=2, message = "Student name must contain atleast 2 characters")
    private String name;

    // @NotBlank(message = "Email is required")
    // @Email(message = "Email should be valid")
    private String email;
}
