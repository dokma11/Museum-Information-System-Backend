package com.veljko121.backend.controller.tours;

import com.veljko121.backend.core.enums.PersonalTourRequestStatus;
import com.veljko121.backend.core.service.IJwtService;
import com.veljko121.backend.dto.tours.PersonalTourRequestCreateDTO;
import com.veljko121.backend.dto.tours.PersonalTourRequestResponseDTO;
import com.veljko121.backend.dto.tours.PersonalTourRequestUpdateDTO;
import com.veljko121.backend.model.tours.PersonalTourRequest;
import com.veljko121.backend.service.IGuestService;
import com.veljko121.backend.service.IOrganizerService;
import com.veljko121.backend.service.tours.IPersonalTourRequestService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/personalTourRequests")
@RequiredArgsConstructor
public class PersonalTourRequestController {

    private final IPersonalTourRequestService personalTourRequestService;
    private final IJwtService jwtService;

    private final ModelMapper modelMapper;
    private final IGuestService guestService;
    private final IOrganizerService organizerService;

    @PostMapping
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<?> create(@RequestBody PersonalTourRequestCreateDTO requestDTO) {
        var request = modelMapper.map(requestDTO, PersonalTourRequest.class);

        var id = jwtService.getLoggedInUserId();
        request.setProposer(guestService.findById(id));
        request.setStatus(PersonalTourRequestStatus.IN_PROGRESS);

        personalTourRequestService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(requestDTO);
    }

    @PutMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<?> update(@RequestBody PersonalTourRequestUpdateDTO requestDTO) {

        var request = personalTourRequestService.findById(requestDTO.getId());

        var id = jwtService.getLoggedInUserId();
        request.setOrganizer(organizerService.findById(id));
        request.setStatus(requestDTO.getStatus());

        personalTourRequestService.update(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(requestDTO);
    }

    @GetMapping("/{guestId}")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<?> findByGuestId(@PathVariable Integer guestId) {
        List<PersonalTourRequest> requests = personalTourRequestService.findByGuestId(guestId);
        var requestResponse = requests.stream()
                .map(request -> modelMapper.map(request, PersonalTourRequestResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(requestResponse);
    }

    @GetMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<?> findAll() {
        List<PersonalTourRequest> requests = personalTourRequestService.findAll();
        var requestResponse = requests.stream()
                .map(request -> modelMapper.map(request, PersonalTourRequestResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(requestResponse);
    }

    @GetMapping("/onHold")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<?> findOnHold() {
        List<PersonalTourRequest> requests = personalTourRequestService.findInProgress();
        var requestResponse = requests.stream()
                .map(request -> modelMapper.map(request, PersonalTourRequestResponseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(requestResponse);
    }

}