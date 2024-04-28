package com.veljko121.backend.model;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class Curator extends User{
    @Column
    private String biography;

    @OneToMany(mappedBy = "curator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exhibition> exhibitions = new ArrayList<>();
}
