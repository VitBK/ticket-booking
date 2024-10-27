package ua.epam.mishchenko.ticketbooking.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import ua.epam.mishchenko.ticketbooking.model.*;
import ua.epam.mishchenko.ticketbooking.repository.EventRepository;
import ua.epam.mishchenko.ticketbooking.repository.UserAccountRepository;
import ua.epam.mishchenko.ticketbooking.repository.UserRepository;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static ua.epam.mishchenko.ticketbooking.utils.Constants.DATE_FORMATTER;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketServiceImplTest {

    @Autowired
    private TicketServiceImpl ticketService;

    @MockBean
    private TicketRepository ticketRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private UserAccountRepository userAccountRepository;

    @Test
    public void bookTicketIfUserNotExistShouldReturnNull() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        Ticket ticket = ticketService.bookTicket(1L, 1L, 1, Category.BAR);

        assertNull(ticket);
    }

    @Test
    public void bookTicketIfEventNotExistShouldReturnNull() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(eventRepository.existsById(anyLong())).thenReturn(false);

        Ticket ticket = ticketService.bookTicket(1L, 1L, 1, Category.BAR);

        assertNull(ticket);
    }

    @Test
    public void bookTicketIfTicketAlreadyBookedShouldReturnNull() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(eventRepository.existsById(anyLong())).thenReturn(true);
        when(ticketRepository.existsByEventIdAndPlaceAndCategory(anyLong(), anyInt(), any(Category.class)))
                .thenReturn(true);

        Ticket ticket = ticketService.bookTicket(1L, 1L, 1, Category.BAR);

        assertNull(ticket);
    }

    @Test
    public void bookTicketIfUserNotHaveAccountShouldReturnNull() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(eventRepository.existsById(anyLong())).thenReturn(true);
        when(ticketRepository.existsByEventIdAndPlaceAndCategory(anyLong(), anyInt(), any(Category.class)))
                .thenReturn(false);
        when(userAccountRepository.findById(anyLong())).thenReturn(Optional.empty());

        Ticket ticket = ticketService.bookTicket(1L, 1L, 1, Category.BAR);

        assertNull(ticket);
    }

    @Test
    public void bookTicketIfUserNotHaveMoneyShouldReturnNull() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(eventRepository.existsById(anyLong())).thenReturn(true);
        when(ticketRepository.existsByEventIdAndPlaceAndCategory(anyLong(), anyInt(), any(Category.class)))
                .thenReturn(false);
        when(userAccountRepository.findById(anyLong()))
                .thenReturn(Optional.of(new UserAccount(new User(), BigDecimal.ONE)));
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.of(new Event("Title", new Date(System.currentTimeMillis()), BigDecimal.TEN)));

        Ticket ticket = ticketService.bookTicket(1L, 1L, 1, Category.BAR);

        assertNull(ticket);
    }

    @Test
    public void bookTicketIfEverythingFineShouldReturnBookedTicket() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(eventRepository.existsById(anyLong())).thenReturn(true);
        when(ticketRepository.existsByEventIdAndPlaceAndCategory(anyLong(), anyInt(), any(Category.class)))
                .thenReturn(false);
        when(userAccountRepository.findById(anyLong()))
                .thenReturn(Optional.of(new UserAccount(new User(), BigDecimal.TEN)));
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.of(new Event("Title", new Date(System.currentTimeMillis()), BigDecimal.ONE)));

        Ticket ticket = ticketService.bookTicket(1L, 1L, 1, Category.BAR);

        assertNull(ticket);
    }

    @Test
    public void getBookedTicketsWithNotNullUserAndProperPageSizeAndPageNumShouldBeOk() {
        User user = new User(1L, "Alan", "alan@gmail.com");
        List<Ticket> content = Arrays.asList(
                new Ticket(1L, new User(), new Event(), 10, Category.BAR),
                new Ticket(4L, new User(), new Event(), 20, Category.BAR)
        );
        Page<Ticket> page = new PageImpl<>(content);

        when(ticketRepository.getAllByUserId(any(Pageable.class), anyLong())).thenReturn(page);

        List<Ticket> actualListOfTicketsByUser = ticketService.getBookedTickets(user, 2, 1);

        assertEquals(content, actualListOfTicketsByUser);
    }

    @Test
    public void getBookedTicketsByUserWithExceptionShouldReturnEmptyList() {
        when(ticketRepository.getAllByUserId(any(Pageable.class), anyLong())).thenThrow(RuntimeException.class);

        List<Ticket> actualListOfTicketsByUser = ticketService.getBookedTickets(new User(), 2, 1);

        assertTrue(actualListOfTicketsByUser.isEmpty());
    }

    @Test
    public void getBookedTicketsByUserWithNullUserShouldReturnEmptyList() {
        List<Ticket> actualTicketsByUser = ticketService.getBookedTickets((User) null, 1, 2);

        assertTrue(actualTicketsByUser.isEmpty());
    }

    @Test
    public void getBookedTicketsWithNotNullEventAndProperPageSizeAndPageNumShouldBeOk() throws ParseException {
        Event event = new Event(4L, "Fourth event", DATE_FORMATTER.parse("15-05-2022 21:00"), BigDecimal.ONE);
        List<Ticket> content = Arrays.asList(
                new Ticket(4L, new User(), new Event(), 20, Category.BAR),
                new Ticket(2L, new User(), new Event(), 10, Category.PREMIUM)
        );
        Page<Ticket> page = new PageImpl<>(content);

        when(ticketRepository.getAllByEventId(any(Pageable.class), anyLong())).thenReturn(page);

        List<Ticket> actualListOfTicketsByEvent = ticketService.getBookedTickets(event, 2, 1);

        assertTrue(content.containsAll(actualListOfTicketsByEvent));
    }

    @Test
    public void getBookedTicketsByEventWithExceptionShouldReturnEmptyList() {
        when(ticketRepository.getAllByEventId(any(Pageable.class), anyLong())).thenThrow(RuntimeException.class);

        List<Ticket> actualListOfTicketsByEvent = ticketService.getBookedTickets(new Event(), 2, 1);

        assertTrue(actualListOfTicketsByEvent.isEmpty());
    }

    @Test
    public void getBookedTicketsWithNullEventShouldReturnEmptyList() {
        List<Ticket> actualTicketsByEvent = ticketService.getBookedTickets((Event) null, 1, 2);

        assertTrue(actualTicketsByEvent.isEmpty());
    }

    @Test
    public void cancelTicketExistsTicketShouldReturnTrue() {
        boolean actualIsDeleted = ticketService.cancelTicket(6L);

        assertTrue(actualIsDeleted);
    }

    @Test
    public void cancelTicketWithExceptionShouldReturnFalse() {
        doThrow(new RuntimeException()).when(ticketRepository).deleteById(anyLong());

        boolean isRemoved = ticketService.cancelTicket(10L);

        assertFalse(isRemoved);
    }
}