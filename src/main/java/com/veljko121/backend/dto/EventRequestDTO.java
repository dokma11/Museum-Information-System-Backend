package com.veljko121.backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class EventRequestDTO {
 
    @NotEmpty
    private String name;

    private String description;

    @NotNull
    private LocalDateTime startDateTime;
    
    @Positive
    @NotNull
    private Integer durationMinutes;

    @Positive
    private Integer ticketsNumber;
    
    @PositiveOrZero
    private Integer price;

    private Integer roomId;
    
}
