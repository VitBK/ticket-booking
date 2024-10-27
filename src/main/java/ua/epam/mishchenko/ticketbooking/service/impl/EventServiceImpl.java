package ua.epam.mishchenko.ticketbooking.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.repository.EventRepository;
import ua.epam.mishchenko.ticketbooking.service.EventService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The type Event service.
 */
@Service
public class EventServiceImpl implements EventService {

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    /**
     * The event repository.
     */
    private final EventRepository eventRepository;

    /**
     * Instantiates a new EventServiceImpl.
     *
     * @param eventRepository the event repository
     */
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Gets event by id.
     *
     * @param eventId the event id
     * @return the event by id
     */
    @Override
    public Event getEventById(String eventId) {
        log.info("Finding an event by id: {}", eventId);
        try {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Can not to find an event by id: " + eventId));
            log.info("Event with id {} successfully found ", eventId);
            return event;
        } catch (RuntimeException e) {
            log.warn("Can not to find an event by id: " + eventId);
            return null;
        }
    }

    /**
     * Gets events by title.
     *
     * @param title    the title
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the events by title
     */
    @Override
    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        log.warn("Finding all events by title {} with page size {} and number of page {}",
                title, pageSize, pageNum);
        try {
            if (title.isEmpty()) {
                log.warn("The title can not be empty");
                return new ArrayList<>();
            }
            Page<Event> eventsByTitle = eventRepository.getAllByTitle(PageRequest.of(pageNum - 1, pageSize), title);
            if (!eventsByTitle.hasContent()) {
                throw new RuntimeException("Can not to find a list of events by title: " + title);
            }
            log.info("All events successfully found by title {} with page size {} and number of page {}",
                    title, pageSize, pageNum);
            return eventsByTitle.getContent();
        } catch (RuntimeException e) {
            log.warn("Can not to find a list of events by title {}", title, e);
            return new ArrayList<>();
        }
    }

    /**
     * Gets events for day.
     *
     * @param day      the day
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the events for day
     */
    @Override
    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        log.info("Finding all events for day {} with page size {} and number of page {}",
                day, pageSize, pageNum);
        try {
            if (day == null) {
                log.warn("The day can not be null");
                return new ArrayList<>();
            }
            Page<Event> eventsByTitle = eventRepository.getAllByDate(PageRequest.of(pageNum - 1, pageSize), day);
            if (!eventsByTitle.hasContent()) {
                throw new RuntimeException("Can not to find a list of events for day: " + day);
            }
            log.info("All events successfully found for day {} with page size {} and number of page {}",
                    day, pageSize, pageNum);

            return eventsByTitle.getContent();
        } catch (RuntimeException e) {
            log.warn("Can not to find a list of events for day {}", day, e);
            return new ArrayList<>();
        }
    }

    /**
     * Create event.
     *
     * @param event the event
     * @return the event
     */
    @Override
    public Event createEvent(Event event) {
        log.info("Start creating an event: {}", event);
        try {
            if (isEventNull(event)) {
                log.warn("The event can not be null");
                return null;
            }
            if (eventExistsByTitleAndDay(event)) {
                log.warn("These title and day are already exists for one event");
                return null;
            }
            event = eventRepository.save(event);
            log.info("Successfully creation of the event: {}", event);
            return event;
        } catch (RuntimeException e) {
            log.warn("Can not to create an event: {}", event, e);
            return null;
        }
    }

    private boolean eventExistsByTitleAndDay(Event event) {
        return eventRepository.existsByTitleAndDate(event.getTitle(), event.getDate());
    }

    /**
     * Is event null boolean.
     *
     * @param event the event
     * @return the boolean
     */
    private boolean isEventNull(Event event) {
        return event == null;
    }

    /**
     * Update event.
     *
     * @param event the event
     * @return the event
     */
    @Override
    public Event updateEvent(Event event) {
        log.info("Start updating an event: {}", event);
        try {
            if (isEventNull(event)) {
                throw new RuntimeException("The event can not be null");
            }
            if (!eventExistsById(event)) {
                throw new RuntimeException("This event does not exist");
            }
            if (eventExistsByTitleAndDay(event)) {
                throw new RuntimeException("These title and day are already exists for one event");
            }
            event = eventRepository.save(event);
            log.info("Successfully updating of the event: {}", event);
            return event;
        } catch (RuntimeException e) {
            log.warn("Can not to update an event: {}", event, e);
            return null;
        }
    }

    private boolean eventExistsById(Event event) {
        return eventRepository.existsById(event.getId());
    }

    /**
     * Delete event boolean.
     *
     * @param eventId the event id
     * @return the boolean
     */
    @Override
    public boolean deleteEvent(String eventId) {
        log.info("Start deleting an event with id: {}", eventId);
        try {
            eventRepository.deleteById(eventId);
            log.info("Successfully deletion of the event with id: {}", eventId);
            return true;
        } catch (RuntimeException e) {
            log.warn("Can not to delete an event with id: {}", eventId, e);
            return false;
        }
    }
}