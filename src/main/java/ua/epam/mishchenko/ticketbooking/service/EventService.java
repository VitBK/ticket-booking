package ua.epam.mishchenko.ticketbooking.service;

import ua.epam.mishchenko.ticketbooking.model.Event;

import java.util.Date;
import java.util.List;

/**
 * The interface Event service.
 */
public interface EventService {

    /**
     * Gets event by id.
     *
     * @param eventId the event id
     * @return the event by id
     */
    Event getEventById(String eventId);

    /**
     * Gets events by title.
     *
     * @param title    the title
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the events by title
     */
    List<Event> getEventsByTitle(String title, int pageSize, int pageNum);

    /**
     * Gets events for day.
     *
     * @param day      the day
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the events for day
     */
    List<Event> getEventsForDay(Date day, int pageSize, int pageNum);

    /**
     * Create event event.
     *
     * @param event the event
     * @return the event
     */
    Event createEvent(Event event);

    /**
     * Update event event.
     *
     * @param event the event
     * @return the event
     */
    Event updateEvent(Event event);

    /**
     * Delete event boolean.
     *
     * @param eventId the event id
     * @return the boolean
     */
    boolean deleteEvent(String eventId);
}
