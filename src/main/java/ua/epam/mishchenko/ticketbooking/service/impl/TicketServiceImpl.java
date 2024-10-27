package ua.epam.mishchenko.ticketbooking.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ua.epam.mishchenko.ticketbooking.model.*;
import ua.epam.mishchenko.ticketbooking.repository.EventRepository;
import ua.epam.mishchenko.ticketbooking.repository.UserRepository;
import ua.epam.mishchenko.ticketbooking.service.TicketService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Ticket service.
 */
@Service
public class TicketServiceImpl implements TicketService {

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final UserRepository userRepository;

    private final EventRepository eventRepository;


    public TicketServiceImpl(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Book ticket.
     *
     * @param userId   the user id
     * @param eventId  the event id
     * @param place    the place
     * @param category the category
     * @return the ticket
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Ticket bookTicket(String userId, String eventId, int place, Category category) {
        log.info("Start booking a ticket for user with id {}, event with id event {}, place {}, category {}",
                userId, eventId, place, category);
        try {
            return processBookingTicket(userId, eventId, place, category);
        } catch (RuntimeException e) {
            log.warn("Can not to book a ticket for user with id {}, event with id {}, place {}, category {}",
                    userId, eventId, place, category, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.warn("Transaction rollback");
            return null;
        }
    }

    private Ticket processBookingTicket(String userId, String eventId, int place, Category category) {
        throwRuntimeExceptionIfUserNotExist(userId);
        throwRuntimeExceptionIfEventNotExist(eventId);
        throwRuntimeExceptionIfTicketAlreadyBooked(eventId, place, category);
        User user = getUser(userId);
        Event event = getEvent(eventId);
        throwRuntimeExceptionIfUserNotHaveEnoughMoney(user, event);
        buyTicket(user.getAccount(), event);
        Ticket ticket = saveBookedTicket(user, eventId, place, category);
        log.info("Successfully booking of the ticket: {}", ticket);
        return ticket;
    }

    private Ticket saveBookedTicket(User user, String eventId, int place, Category category) {
        Ticket ticket = createNewTicket(eventId, place, category);
        user.addTicket(ticket);
        userRepository.save(user);
        return ticket;
    }

    private void buyTicket(UserAccount userAccount, Event event) {
        userAccount.setMoney(subtractTicketPriceFromUserMoney(userAccount, event));
    }

    private BigDecimal subtractTicketPriceFromUserMoney(UserAccount userAccount, Event event) {
        return userAccount.getMoney().subtract(event.getTicketPrice());
    }

    private void throwRuntimeExceptionIfUserNotHaveEnoughMoney(User user, Event event) {
        if (!userHasEnoughMoneyForTicket(user.getAccount(), event)) {
            throw new RuntimeException(
                    "The user with id " + user.getId() +
                            " does not have enough money for ticket with event id " + event.getId()
            );
        }
    }

    private void throwRuntimeExceptionIfTicketAlreadyBooked(String eventId, int place, Category category) {
//        if (ticketRepository.existsByEventIdAndPlaceAndCategory(eventId, place, category)) {
//            throw new RuntimeException("This ticket already booked");
//        }
    }

    private Event getEvent(String eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Can not to find an event by id: " + eventId));
    }

    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Can not to find a user account by user id: " + userId));
    }

    private UserAccount getUserAccount(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Can not to find a user account by user id: " + userId))
                .getAccount();
    }

    private void throwRuntimeExceptionIfEventNotExist(String eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new RuntimeException("The event with id " + eventId + " does not exist");
        }
    }

    private void throwRuntimeExceptionIfUserNotExist(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("The user with id " + userId + " does not exist");
        }
    }

    private boolean userHasEnoughMoneyForTicket(UserAccount userAccount, Event event) {
        return userAccount.getMoney().compareTo(event.getTicketPrice()) > -1;
    }

    /**
     * Create new ticket.
     *
     * @param userId   the user id
     * @param eventId  the event id
     * @param place    the place
     * @param category the category
     * @return the ticket
     */
    private Ticket createNewTicket(String eventId, int place, Category category) {
        return new Ticket(eventId, place, category);
    }

    /**
     * Gets booked tickets.
     *
     * @param user     the user
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the booked tickets
     */
    @Override
    public List<Ticket> getBookedTickets(User user, int pageSize, int pageNum) {
        log.info("Finding all booked tickets by user {} with page size {} and number of page {}",
                user, pageSize, pageNum);
        try {
            if (isUserNull(user)) {
                log.warn("The user can not be a null");
                return new ArrayList<>();
            }
            System.out.println(user.getTickets());
            List<Ticket> ticketsByUser = user.getTickets();
            if (ticketsByUser.isEmpty()) {
                throw new RuntimeException("Can not to fina a list of booked tickets by user with id: " + user.getId());
            }
            log.info("All booked tickets successfully found by user {} with page size {} and number of page {}",
                    user, pageSize, pageNum);
            return ticketsByUser;
        } catch (RuntimeException e) {
            log.warn("Can not to find a list of booked tickets by user '{}'", user, e);
            return new ArrayList<>();
        }
    }

    /**
     * Is user null boolean.
     *
     * @param user the user
     * @return the boolean
     */
    private boolean isUserNull(User user) {
        return user == null;
    }

    /**
     * Gets booked tickets.
     *
     * @param event    the event
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the booked tickets
     */
    @Override
    public List<Ticket> getBookedTickets(Event event, int pageSize, int pageNum) {
        log.info("Finding all booked tickets by event {} with page size {} and number of page {}",
                event, pageSize, pageNum);
        try {
            if (isEventNull(event)) {
                log.warn("The event can not be a null");
                return new ArrayList<>();
            }
            List<Ticket> ticketsByEvent = event.getTickets()
            if (ticketsByEvent.isEmpty()) {
                throw new RuntimeException("Can not to fina a list of booked tickets by event with id: " + event.getId());
            }
            log.info("All booked tickets successfully found by event {} with page size {} and number of page {}",
                    event, pageSize, pageNum);
            return ticketsByEvent;
        } catch (RuntimeException e) {
            log.warn("Can not to find a list of booked tickets by event '{}'", event, e);
            return new ArrayList<>();
        }
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
     * Cancel ticket boolean.
     *
     * @param ticketId the ticket id
     * @return the boolean
     */
    @Override
    public boolean cancelTicket(String ticketId) {
//        log.info("Start canceling a ticket with id: {}", ticketId);
//        try {
//            ticketRepository.deleteById(ticketId);
//            log.info("Successfully canceling of the ticket with id: {}", ticketId);
//            return true;
//        } catch (RuntimeException e) {
//            log.warn("Can not to cancel a ticket with id: {}", ticketId, e);
//            return false;
//        }
    }
}
