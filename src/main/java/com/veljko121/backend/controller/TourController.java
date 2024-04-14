package com.veljko121.backend.controller;

import com.veljko121.backend.core.service.IJwtService;

import com.veljko121.backend.dto.tours.TourCreateDTO;
import com.veljko121.backend.dto.tours.TourUpdateDTO;
import com.veljko121.backend.model.Tour;
import com.veljko121.backend.service.IOrganizerService;
import com.veljko121.backend.service.ITourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ORGANIZER')")
public class TourController {

    private final ITourService tourService;
    private final IJwtService jwtService;
    private final IOrganizerService organizerService;

    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TourCreateDTO tourCreateDTO) {
        var tour = modelMapper.map(tourCreateDTO, Tour.class);

        var id = jwtService.getLoggedInUserId();
        tour.setOrganizer(organizerService.findById(id));

        tourService.save(tour);

        return ResponseEntity.status(HttpStatus.CREATED).body(tourCreateDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ORGANIZER', 'CURATOR', 'GUEST')")
    public ResponseEntity<?> findAll() {
        List<Tour> tours = tourService.findAll();
        var tourResponse = tours.stream()
                .map(tour -> modelMapper.map(tour, Tour.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(tourResponse);
    }

    @GetMapping("/organizers/{organizerId}")
    public ResponseEntity<?> findByOrganizerId(@PathVariable Integer organizerId) {
        List<Tour> tours = tourService.findByOrganizerId(organizerId);
        var tourResponse = tours.stream()
                .map(tour -> modelMapper.map(tour, Tour.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(tourResponse);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody TourUpdateDTO tourUpdateDTO) {
        var tour = modelMapper.map(tourUpdateDTO, Tour.class);

        var id = jwtService.getLoggedInUserId();
        tour.setOrganizer(organizerService.findById(id));

        tourService.update(tour);

        return ResponseEntity.status(HttpStatus.CREATED).body(tourUpdateDTO);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        Tour tour = tourService.findById(id);

        if (tour != null) {
            tourService.delete(tour);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
