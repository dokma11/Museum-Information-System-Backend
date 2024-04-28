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
public class Organizer extends User{

    @Column
    private String biography;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exhibition> proposedExhibitions = new ArrayList<>();

    public void proposeExhibition(Exhibition exhibition) {
        if (proposedExhibitions == null) {
            proposedExhibitions = new ArrayList<>();
        }
        proposedExhibitions.add(exhibition);
        exhibition.setOrganizer(this);
    }

    
}
