package com.veljko121.backend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.veljko121.backend.core.service.impl.CRUDService;
import com.veljko121.backend.model.Room;
import com.veljko121.backend.model.RoomReservation;
import com.veljko121.backend.repository.RoomRepository;
import com.veljko121.backend.repository.RoomReservationRepository;
import com.veljko121.backend.service.IRoomReservationService;

@Service
public class RoomReservationService extends CRUDService<RoomReservation, Integer> implements IRoomReservationService {

    private final RoomReservationRepository roomReservationRepository;
    private final RoomRepository roomRepository;
    
    @Autowired
    public RoomReservationService(RoomReservationRepository repository, RoomRepository roomRepository) {
        super(repository);
        this.roomReservationRepository = repository;
        this.roomRepository = roomRepository;
    }

    private Boolean overlaps(RoomReservation reservation, LocalDateTime startDateTime, Integer durationMinutes) {
        var reservationStartDateTime = reservation.getStartDateTime();
        var reservationEndDateTime = reservation.getEndDateTime();
        var endDateTime = startDateTime.plusMinutes(durationMinutes);

        if ((startDateTime.isAfter(reservationStartDateTime) || startDateTime.isEqual(reservationStartDateTime)) && startDateTime.isBefore(reservationEndDateTime) ||
            (endDateTime.isAfter(reservationStartDateTime)) && (endDateTime.isBefore(reservationEndDateTime) || endDateTime.isEqual(reservationEndDateTime))) {
            return true;
        }

        return false;
    }

    @Override
    public Boolean isRoomAvailable(Room room, LocalDateTime startDateTime, Integer durationMinutes) {
        for (var reservation : roomReservationRepository.findByRoom(room)) {
            if (overlaps(reservation, startDateTime, durationMinutes)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Collection<Room> findAvailableRoomsByTimespan(LocalDateTime startDateTime, Integer durationMinutes) {
        var availableRooms = new ArrayList<Room>();
        for (var room : roomRepository.findAll()) {
            if (isRoomAvailable(room, startDateTime, durationMinutes)) availableRooms.add(room);
        }
        return availableRooms;
    }
    
}
