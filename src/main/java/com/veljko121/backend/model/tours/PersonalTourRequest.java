package com.veljko121.backend.model.tours;

import com.veljko121.backend.core.enums.PersonalTourRequestStatus;
import com.veljko121.backend.model.Guest;
import com.veljko121.backend.model.Exhibition;
import com.veljko121.backend.model.Organizer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "personal_tour_request")
public class PersonalTourRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime occurrenceDateTime;

    @NotEmpty
    @Column(nullable = false)
    private String guestNumber;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "personal_tour_request_id")
    private List<Exhibition> exhibitions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "proposer_id")
    private Guest proposer;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'ON_HOLD'")
    private PersonalTourRequestStatus status;

    @Column(nullable = false)
    private String denialReason;

    @NotEmpty
    @Column(nullable = false)
    private String proposerContactPhone;

}
