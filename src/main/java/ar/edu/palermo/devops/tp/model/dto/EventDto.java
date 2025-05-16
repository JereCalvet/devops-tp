package ar.edu.palermo.devops.tp.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventDto(
        Long id,
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
        @NotBlank(message = "Name cannot be blank")
        String name,
        @Size(min = 10, max = 200, message = "Description must be between 10 and 200 characters")
        @NotBlank(message = "Description cannot be blank")
        String description,
        @Future(message = "Date must be in the future")
        LocalDateTime date
) {}
