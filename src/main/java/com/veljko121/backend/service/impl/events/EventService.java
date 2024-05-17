package com.veljko121.backend.service.impl.events;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.stereotype.Service;

import com.veljko121.backend.core.enums.EventStatus;
import com.veljko121.backend.core.exception.RoomNotAvailableException;
import com.veljko121.backend.core.service.impl.CRUDService;
import com.veljko121.backend.model.Organizer;
import com.veljko121.backend.model.events.Event;
import com.veljko121.backend.model.events.EventInvitation;
import com.veljko121.backend.repository.CuratorRepository;
import com.veljko121.backend.repository.RoomRepository;
import com.veljko121.backend.repository.events.EventInvitationRepository;
import com.veljko121.backend.repository.events.EventPictureRepository;
import com.veljko121.backend.repository.events.EventRepository;
import com.veljko121.backend.service.IRoomReservationService;
import com.veljko121.backend.service.events.IEventService;

@Service
public class EventService extends CRUDService<Event, Integer> implements IEventService {

    private final EventRepository eventRepository;
    private final RoomRepository roomRepository;
    private final EventPictureRepository eventPictureRepository;
    private final EventInvitationRepository eventInvitationRepository;
    private final CuratorRepository curatorRepository;

    private final IRoomReservationService roomReservationService;

    public EventService(EventRepository repository, RoomRepository roomRepository, IRoomReservationService roomReservationService, EventPictureRepository eventPictureRepository, EventInvitationRepository eventInvitationRepository, CuratorRepository curatorRepository) {
        super(repository);
        this.eventRepository = repository;
        this.roomRepository = roomRepository;
        this.roomReservationService = roomReservationService;
        this.eventPictureRepository = eventPictureRepository;
        this.eventInvitationRepository = eventInvitationRepository;
        this.curatorRepository = curatorRepository;
    }

    @Override
    public Event save(Event entity) throws RoomNotAvailableException {
        var room = roomRepository.findById(entity.getRoomReservation().getRoom().getId()).orElseThrow();
        var roomReservation = entity.getRoomReservation();
        
        LocalDateTime endDateTime = entity.getStartDateTime().plusMinutes(entity.getDurationMinutes());
        roomReservation.setEndDateTime(endDateTime);
        roomReservation.setStartDateTime(entity.getStartDateTime());

        if (!roomReservationService.isRoomAvailable(room, entity.getStartDateTime(), entity.getDurationMinutes())) {
            throw new RoomNotAvailableException(roomReservation);
        }

        roomReservation = roomReservationService.save(roomReservation);
        for (var eventPicture : entity.getPictures()) {
            eventPictureRepository.save(eventPicture);
        }

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

    @Override
    public Collection<Event> findByOrganizer(Organizer organier) {
        return eventRepository.findByOrganizer(organier);
    }

    @Override
    public void archive(Integer id) {
        var event = findById(id);
        event.setStatus(EventStatus.DRAFT);
        super.save(event);
    }

    @Override
    public Event update(Event entity) {
        var oldEvent = eventRepository.findById(entity.getId()).orElseThrow();
        var room = roomRepository.findById(entity.getRoomReservation().getRoom().getId()).orElseThrow();
        var roomReservation = oldEvent.getRoomReservation();

        var id = oldEvent.getId();
        
        if (!roomReservationService.isRoomAvailableForUpdating(room, entity.getStartDateTime(), entity.getDurationMinutes(), roomReservation)) {
            throw new RoomNotAvailableException(roomReservation);
        }

        roomReservation.setEndDateTime(entity.getStartDateTime().plusMinutes(entity.getDurationMinutes()));
        roomReservation.setStartDateTime(entity.getStartDateTime());
        roomReservation = roomReservationService.save(roomReservation);
        for (var eventPicture : oldEvent.getPictures()) {
            eventPictureRepository.delete(eventPicture);
        }
        for (var eventPicture : entity.getPictures()) {
            eventPictureRepository.save(eventPicture);
        }

        entity.setRoomReservation(roomReservation);
        entity.setStatus(EventStatus.DRAFT);
        entity.setId(id);

        return super.save(entity);
    }

    @Override
    public void inviteCurators(Integer eventId, Collection<Integer> curatorIds) {
        var event = findById(eventId);
        var eventInvitations = new ArrayList<EventInvitation>();
        for (var curatorId : curatorIds) {
            var curator = curatorRepository.findById(curatorId).orElseThrow();
            var eventInvitation = new EventInvitation();
            eventInvitation.setCurator(curator);
            eventInvitation.setEvent(event);
            eventInvitations.add(eventInvitation);
        }
        this.eventInvitationRepository.saveAll(eventInvitations);
    }
    
}