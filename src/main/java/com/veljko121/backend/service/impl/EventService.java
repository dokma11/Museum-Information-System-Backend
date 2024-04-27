package com.veljko121.backend.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.veljko121.backend.core.enums.EventStatus;
import com.veljko121.backend.core.exception.RoomNotAvailableException;
import com.veljko121.backend.core.service.impl.CRUDService;
import com.veljko121.backend.model.Event;
import com.veljko121.backend.repository.EventRepository;
import com.veljko121.backend.repository.RoomRepository;
import com.veljko121.backend.service.IEventService;
import com.veljko121.backend.service.IRoomReservationService;

@Service
public class EventService extends CRUDService<Event, Integer> implements IEventService {

    private final EventRepository eventRepository;
    private final RoomRepository roomRepository;
    private final IRoomReservationService roomReservationService;

    @Autowired
    public EventService(EventRepository repository, RoomRepository roomRepository, IRoomReservationService roomReservationService) {
        super(repository);
        eventRepository = repository;
        this.roomRepository = roomRepository;
        this.roomReservationService = roomReservationService;
    }

    @Override
    public Event save(Event entity) throws RoomNotAvailableException {
        var room = roomRepository.findById(entity.getRoomReservation().getRoom().getId()).orElseThrow();
        var roomReservation = entity.getRoomReservation();
        if (!roomReservationService.isRoomAvailable(room, entity.getStartDateTime(), entity.getDurationMinutes())) {
            throw new RoomNotAvailableException(roomReservation);
        }

        roomReservation.setDurationMinutes(entity.getDurationMinutes());
        roomReservation.setStartDateTime(entity.getStartDateTime());
        roomReservation = roomReservationService.save(roomReservation);

        entity.setRoomReservation(roomReservation);
        entity.setStatus(EventStatus.DRAFT);

        return super.save(entity);
    }

    @Override
    public void publish(Integer id) {
        var event = findById(id);
        event.setStatus(EventStatus.PUBLISHED);
        super.save(event);
    }

    @Override
    public Collection<Event> findPublished() {
        return eventRepository.findByStatus(EventStatus.PUBLISHED);
    }
    
}
