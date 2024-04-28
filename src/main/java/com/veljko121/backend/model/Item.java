package com.veljko121.backend.model;

import com.veljko121.backend.core.enums.ItemCategory;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Entity
@Data
@Table(name = "item")
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Column(nullable = false)
    private String name;

    @NotEmpty
    @Column(nullable = false)
    private String description;

    @Column(nullable = true)
    private String authorsName;

    @Column(nullable = true)
    private String yearOfCreation;

    @NotEmpty
    @Column(nullable = false)
    private String period;

    @Enumerated(EnumType.STRING)
    private ItemCategory category;

    @NotEmpty
    @Column(nullable = false)
    private String picture;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cleaning_id")
    private Cleaning cleaning;
}
