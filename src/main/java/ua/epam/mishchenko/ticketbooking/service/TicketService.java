package ua.epam.mishchenko.ticketbooking.service;

import ua.epam.mishchenko.ticketbooking.model.Category;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.User;

import java.util.List;

/**
 * The interface Ticket service.
 */
public interface TicketService {

    /**
     * Book ticket ticket.
     *
     * @param userId   the user id
     * @param eventId  the event id
     * @param place    the place
     * @param category the category
     * @return the ticket
     */
    Ticket bookTicket(String userId, String eventId, int place, Category category);

    /**
     * Gets booked tickets.
     *
     * @param user     the user
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the booked tickets
     */
    List<Ticket> getBookedTickets(User user, int pageSize, int pageNum);

    /**
     * Gets booked tickets.
     *
     * @param event    the event
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the booked tickets
     */
    List<Ticket> getBookedTickets(Event event, int pageSize, int pageNum);

    /**
     * Cancel ticket boolean.
     *
     * @param ticketId the ticket id
     * @return the boolean
     */
    boolean cancelTicket(String ticketId);
}