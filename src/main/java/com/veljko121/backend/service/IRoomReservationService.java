package com.veljko121.backend.service;

import java.time.LocalDateTime;
import java.util.Collection;

import com.veljko121.backend.core.service.ICRUDService;
import com.veljko121.backend.model.Event;
import com.veljko121.backend.model.Room;
import com.veljko121.backend.model.RoomReservation;

public interface IRoomReservationService extends ICRUDService<RoomReservation, Integer> {

    Boolean isRoomAvailable(Room room, LocalDateTime startDateTime, Integer durationMinutes);

    Collection<Room> findAvailableRoomsByTimespan(LocalDateTime startDateTime, Integer durationMinutes);

    Collection<Room>  findAvailableRoomsForUpdating(Event event, LocalDateTime startDateTime, Integer durationMinutes);

    Boolean isRoomAvailableForUpdating(Room room, LocalDateTime startDateTime, Integer durationMinutes, RoomReservation roomReservation);

}
