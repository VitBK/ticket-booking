package ua.epam.mishchenko.ticketbooking.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.epam.mishchenko.ticketbooking.facade.impl.BookingFacadeImpl;
import ua.epam.mishchenko.ticketbooking.model.Category;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Tickets controller.
 */
@Controller
@RequestMapping("/tickets")
public class TicketsController {

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(TicketsController.class);

    /**
     * The Booking facade.
     */
    private final BookingFacadeImpl bookingFacade;

    /**
     * Instantiates a new Tickets controller.
     *
     * @param bookingFacade the booking facade
     */
    public TicketsController(BookingFacadeImpl bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    /**
     * Book ticket model and view.
     *
     * @param userId   the user id
     * @param eventId  the event id
     * @param place    the place
     * @param category the category
     * @return the model and view
     */
    @PostMapping
    public ModelAndView bookTicket(@RequestParam String userId,
                                   @RequestParam String eventId,
                                   @RequestParam int place,
                                   @RequestParam Category category) {
        log.info("Booking a ticket: userId={}, eventId={}, place={}, category={}", userId, eventId, place, category);
        Map<String, Object> model = new HashMap<>();
        Ticket ticket = bookingFacade.bookTicket(userId, eventId, place, category);
        if (isNull(ticket)) {
            model.put("message", "Can not to book a ticket");
            log.info("Can not to book a ticket: userId={}, eventId={}, place={}, category={}",
                    userId, eventId, place, category);
        } else {
            model.put("ticket", ticket);
            log.info("The ticket successfully booked");
        }
        return new ModelAndView("ticket", model);
    }

    /**
     * Is null boolean.
     *
     * @param object the object
     * @return the boolean
     */
    private boolean isNull(Object object) {
        return object == null;
    }

    /**
     * Show tickets by user model and view.
     *
     * @param userId   the user id
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the model and view
     */
    @GetMapping("/user/{userId}")
    public ModelAndView showTicketsByUser(@PathVariable String userId,
                                          @RequestParam int pageSize,
                                          @RequestParam int pageNum) {
        log.info("Showing the tickets by user with id: {}", userId);
        Map<String, Object> model = new HashMap<>();
        User userById = bookingFacade.getUserById(userId);
        if (isNull(userById)) {
            model.put("message", "Can not to find a user by id: " + userId);
            log.info("Can not to find a user by id: {}", userId);
        } else {
            List<Ticket> bookedTickets = bookingFacade.getBookedTickets(userById, pageSize, pageNum);
            if (bookedTickets.isEmpty()) {
                model.put("message", "Can not to find the tickets by user with id: " + userId);
                log.info("Can not to find the tickets by user with id: {}", userId);
            } else {
                model.put("tickets", bookedTickets);
                log.info("The tickets successfully found");
            }
        }
        return new ModelAndView("tickets", model);
    }

    /**
     * Show tickets by event model and view.
     *
     * @param eventId  the event id
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the model and view
     */
    @GetMapping("/event/{eventId}")
    public ModelAndView showTicketsByEvent(@PathVariable String eventId,
                                           @RequestParam int pageSize,
                                           @RequestParam int pageNum) {
        log.info("Showing the tickets by event with id: {}", eventId);
        Map<String, Object> model = new HashMap<>();
        Event eventById = bookingFacade.getEventById(eventId);
        if (isNull(eventById)) {
            model.put("message", "Can not to find an event by id: " + eventId);
            log.info("Can not to find an event by id: {}", eventId);
        } else {
            List<Ticket> bookedTickets = bookingFacade.getBookedTickets(eventById, pageSize, pageNum);
            if (bookedTickets.isEmpty()) {
                model.put("message", "Can not to find the tickets by event with id: " + eventId);
                log.info("Can not to find the tickets by event with id: {}", eventId);
            } else {
                model.put("tickets", bookedTickets);
                log.info("The tickets successfully found");
            }
        }
        return new ModelAndView("tickets", model);
    }

    /**
     * Cancel ticket model and view.
     *
     * @param id the id
     * @return the model and view
     */
    @DeleteMapping("/{id}")
    public ModelAndView cancelTicket(@PathVariable String id) {
        log.info("Canceling ticket with id: {}", id);
        Map<String, Object> model = new HashMap<>();
        boolean isTicketCanceled = bookingFacade.cancelTicket(id);
        if (isTicketCanceled) {
            model.put("message", "The ticket with id: " + id + " successfully canceled");
            log.info("The ticket with id: {} successfully canceled", id);
        } else {
            model.put("message", "The ticket with id: " + id + " not canceled");
            log.info("The ticket with id: {} not canceled", id);
        }
        return new ModelAndView("ticket", model);
    }
}
