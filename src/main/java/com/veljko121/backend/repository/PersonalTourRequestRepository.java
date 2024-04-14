package com.veljko121.backend.repository;

import com.veljko121.backend.model.PersonalTourRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonalTourRequestRepository extends JpaRepository<PersonalTourRequest, Integer> {

    @Query("SELECT r FROM PersonalTourRequest r WHERE r.proposer.id = ?1")
    List<PersonalTourRequest> findByGuestId(Integer guestId);

}
