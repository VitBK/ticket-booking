package ua.epam.mishchenko.ticketbooking.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.epam.mishchenko.ticketbooking.facade.impl.BookingFacadeImpl;
import ua.epam.mishchenko.ticketbooking.model.Event;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ua.epam.mishchenko.ticketbooking.utils.Constants.DATE_FORMATTER;

/**
 * The type Events controller.
 */
@Controller
@RequestMapping("/events")
public class EventsController {

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(EventsController.class);

    /**
     * The Booking facade.
     */
    private final BookingFacadeImpl bookingFacade;

    /**
     * Instantiates a new Events controller.
     *
     * @param bookingFacade the booking facade
     */
    public EventsController(BookingFacadeImpl bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    /**
     * Show event by id model and view.
     *
     * @param id the id
     * @return the model and view
     */
    @GetMapping("/{id}")
    public ModelAndView showEventById(@PathVariable String id) {
        log.info("Showing event by id: {}", id);
        Event eventById = bookingFacade.getEventById(id);
        Map<String, Object> model = new HashMap<>();
        if (isNull(eventById)) {
            model.put("message", "Can not to get an event by id: " + id);
            log.info("Can not to get event by id: {}", id);
        } else {
            model.put("event", eventById);
            log.info("Event by id: {} successfully found", id);
        }
        return new ModelAndView("event", model);
    }

    /**
     * Is null boolean.
     *
     * @param event the event
     * @return the boolean
     */
    private boolean isNull(Event event) {
        return event == null;
    }

    /**
     * Show events by title model and view.
     *
     * @param title    the title
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the model and view
     */
    @GetMapping("/title/{title}")
    public ModelAndView showEventsByTitle(@PathVariable String title,
                                          @RequestParam int pageSize,
                                          @RequestParam int pageNum) {
        log.info("Showing events by title: {}", title);
        Map<String, Object> model = new HashMap<>();
        List<Event> eventsByTitle = bookingFacade.getEventsByTitle(title, pageSize, pageNum);
        if (eventsByTitle.isEmpty()) {
            model.put("message", "Can not to get events by title: " + title);
            log.info("Can not to get events by title: {}", title);
        } else {
            model.put("events", eventsByTitle);
            log.info("Events by title '{}' successfully found", title);
        }
        return new ModelAndView("events", model);
    }

    /**
     * Show events for day model and view.
     *
     * @param day      the day
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the model and view
     */
    @GetMapping("/day/{day}")
    public ModelAndView showEventsForDay(@PathVariable String day,
                                         @RequestParam int pageSize,
                                         @RequestParam int pageNum) {
        log.info("Showing events for day: {}", day);
        Map<String, Object> model = new HashMap<>();
        try {
            Date date = parseFromStringToDate(day);
            List<Event> eventsForDay = bookingFacade.getEventsForDay(date, pageSize, pageNum);
            if (eventsForDay.isEmpty()) {
                model.put("message", "Can not to get events for day: " + day);
                log.info("Can not to get events for day: {}", day);
            } else {
                model.put("events", eventsForDay);
                log.info("Events for day: {} successfully found", day);
            }
        } catch (RuntimeException e) {
            log.warn("Can not to get events for day={}", day, e);
            model.put("message", "Can not to parse string " + day + " to date object");
        }
        return new ModelAndView("events", model);
    }

    /**
     * Create event model and view.
     *
     * @param title the title
     * @param day   the day
     * @return the model and view
     */
    @PostMapping
    public ModelAndView createEvent(@RequestParam String title,
                                    @RequestParam String day,
                                    @RequestParam BigDecimal price) {
        log.info("Creating an event with title={} and day={} and price={}", title, day, price);
        Map<String, Object> model = new HashMap<>();
        try {
            Event event = bookingFacade.createEvent(createEventEntityWithoutId(title, day, price));
            if (isNull(event)) {
                model.put("message", "Can not to create an event");
                log.info("Can not to create an event");
            } else {
                model.put("event", event);
                log.info("The event successfully created");
            }
        } catch (RuntimeException e) {
            log.error("Can not to parse string {} to date object", day, e);
            model.put("message", "Can not to parse string " + day + " to date object");
        }
        return new ModelAndView("event", model);
    }

    /**
     * Create event entity without id event.
     *
     * @param title the title
     * @param day   the day
     * @param price the price
     * @return the event
     */
    private Event createEventEntityWithoutId(String title, String day, BigDecimal price) {
        Event event = new Event();
        event.setTitle(title);
        event.setDate(parseFromStringToDate(day));
        event.setTicketPrice(price);
        return event;
    }

    /**
     * Parse from string to date date.
     *
     * @param date the date
     * @return the date
     */
    private Date parseFromStringToDate(String date) {
        try {
            return DATE_FORMATTER.parse(date);
        } catch (ParseException e) {
            log.warn("Can not to parse string {} to date object", date);
            throw new RuntimeException("Can not to parse string " + date + " to date object", e);
        }
    }

    /**
     * Update event model and view.
     *
     * @param id    the id
     * @param title the title
     * @param day   the day
     * @return the model and view
     */
    @PutMapping
    public ModelAndView updateEvent(@RequestParam String id,
                                    @RequestParam String title,
                                    @RequestParam String day,
                                    @RequestParam BigDecimal price) {
        log.info("Updating an event with id: {}", id);
        Map<String, Object> model = new HashMap<>();
        try {
            Event event = bookingFacade.updateEvent(createEventEntityWithId(id, title, day, price));
            if (isNull(event)) {
                model.put("message", "Can not to update an event with id: " + id);
                log.info("Can not to update an event with id: {}", id);
            } else {
                model.put("event", event);
                log.info("The event with id: {} successfully updated", id);
            }
        } catch (RuntimeException e) {
            log.error("Can not to update entity with id={}, title={}, day={}", id, title, day, e);
            model.put("message", "Can not to parse string " + day + " to date object");
        }
        return new ModelAndView("event", model);
    }

    /**
     * Create event entity with id event.
     *
     * @param id    the id
     * @param title the title
     * @param day   the day
     * @param price the price
     * @return the event
     */
    private Event createEventEntityWithId(String id, String title, String day, BigDecimal price) {
        Event eventEntity = createEventEntityWithoutId(title, day, price);
        eventEntity.setId(id);
        return eventEntity;
    }

    /**
     * Delete event model and view.
     *
     * @param id the id
     * @return the model and view
     */
    @DeleteMapping("/{id}")
    public ModelAndView deleteEvent(@PathVariable String id) {
        log.info("Deleting an event with id: {}", id);
        Map<String, Object> model = new HashMap<>();
        boolean isEventDeleted = bookingFacade.deleteEvent(id);
        if (isEventDeleted) {
            model.put("message", "The event with id " + id + " successfully deleted");
            log.info("The event with id: {} successfully deleted", id);
        } else {
            model.put("message", "The event with id " + id + " not deleted");
            log.info("The event with id: {} not deleted", id);
        }
        return new ModelAndView("event", model);
    }
}
