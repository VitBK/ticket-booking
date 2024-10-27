package ua.epam.mishchenko.ticketbooking.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import ua.epam.mishchenko.ticketbooking.facade.impl.BookingFacadeImpl;
import ua.epam.mishchenko.ticketbooking.model.Event;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventsControllerTest {

    private EventsController eventsController;

    @Mock
    private BookingFacadeImpl bookingFacade;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        eventsController = new EventsController(bookingFacade);
    }

    @Test
    public void showEventByIdWithExistingEventIdShouldReturnModelAndViewWithEvent() {
        Event event = new Event("1", "Test event", new Date(System.currentTimeMillis()), BigDecimal.ONE);

        when(bookingFacade.getEventById(anyLong())).thenReturn(event);

        ModelAndView actualModelAndView = eventsController.showEventById(1L);

        verify(bookingFacade, times(1)).getEventById(anyLong());

        assertEquals("event", actualModelAndView.getViewName());
        assertTrue(actualModelAndView.getModelMap().containsAttribute("event"));
        assertFalse(actualModelAndView.getModelMap().containsAttribute("message"));
    }

    @Test
    public void showEventByIdWithNotExistingEventIdShouldReturnModelAndViewWithMessage() {
        when(bookingFacade.getEventById(anyLong())).thenReturn(null);

        ModelAndView actualModelAndView = eventsController.showEventById(1L);

        verify(bookingFacade, times(1)).getEventById(anyLong());

        ModelMap actualModelMap = actualModelAndView.getModelMap();

        assertEquals("event", actualModelAndView.getViewName());
        assertFalse(actualModelMap.containsAttribute("event"));
        assertTrue(actualModelMap.containsAttribute("message"));
        assertEquals("Can not to get an event by id: 1", actualModelMap.getAttribute("message"));
    }

    @Test
    public void showEventsByTitleWithExistingEventTitleShouldReturnModelAndViewWithListOfEvents() {
        Event event = new Event(1L, "Test event", new Date(System.currentTimeMillis()), BigDecimal.ONE);

        when(bookingFacade.getEventsByTitle(anyString(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(event));

        ModelAndView actualModelAndView = eventsController.showEventsByTitle("Test Title", 1, 1);

        verify(bookingFacade, times(1)).getEventsByTitle(anyString(), anyInt(), anyInt());

        assertEquals("events", actualModelAndView.getViewName());
        assertTrue(actualModelAndView.getModelMap().containsAttribute("events"));
        assertFalse(actualModelAndView.getModelMap().containsAttribute("message"));
    }

    @Test
    public void showEventsByTitleWithNotExistingEventTitleShouldReturnModelAndViewWithMessage() {
        when(bookingFacade.getEventsByTitle(anyString(), anyInt(), anyInt())).thenReturn(new ArrayList<>());

        ModelAndView actualModelAndView = eventsController.showEventsByTitle("Not Existing Title", 1, 1);

        verify(bookingFacade, times(1)).getEventsByTitle(anyString(), anyInt(), anyInt());

        ModelMap actualModelMap = actualModelAndView.getModelMap();

        assertEquals("events", actualModelAndView.getViewName());
        assertFalse(actualModelMap.containsAttribute("events"));
        assertTrue(actualModelMap.containsAttribute("message"));
        assertEquals("Can not to get events by title: Not Existing Title", actualModelMap.getAttribute("message"));
    }

    @Test
    public void showEventsForDayWithCorrectDateFormatAndExistingDayShouldReturnModelAndViewWithListOfEvents() {
        Event event = new Event(1L, "Test event", new Date(System.currentTimeMillis()), BigDecimal.ONE);

        when(bookingFacade.getEventsForDay(any(), anyInt(), anyInt())).thenReturn(Collections.singletonList(event));

        ModelAndView actualModelAndView = eventsController.showEventsForDay("18-05-2022 15:30", 1, 1);

        verify(bookingFacade, times(1)).getEventsForDay(any(), anyInt(), anyInt());

        ModelMap actualModelMap = actualModelAndView.getModelMap();

        assertEquals("events", actualModelAndView.getViewName());
        assertTrue(actualModelMap.containsAttribute("events"));
        assertFalse(actualModelMap.containsAttribute("message"));
    }

    @Test
    public void showEventsForDayWithCorrectDateFormatAndNotExistingDayShouldReturnModelAndViewWithMessage() {
        when(bookingFacade.getEventsForDay(any(), anyInt(), anyInt())).thenReturn(new ArrayList<>());

        ModelAndView actualModelAndView = eventsController.showEventsForDay("18-05-2022 15:30", 1, 1);

        verify(bookingFacade, times(1)).getEventsForDay(any(), anyInt(), anyInt());

        ModelMap actualModelMap = actualModelAndView.getModelMap();

        assertEquals("events", actualModelAndView.getViewName());
        assertFalse(actualModelMap.containsAttribute("events"));
        assertTrue(actualModelMap.containsAttribute("message"));
        assertEquals("Can not to get events for day: 18-05-2022 15:30", actualModelMap.getAttribute("message"));
    }

    @Test
    public void showEventsForDayWithWrongDateFormatAndExistingDayShouldReturnModelAndViewWithMessage() {
        ModelAndView actualModelAndView = eventsController.showEventsForDay("18.05.2022 15:30", 1, 1);

        verify(bookingFacade, times(0)).getEventsForDay(any(), anyInt(), anyInt());

        ModelMap actualModelMap = actualModelAndView.getModelMap();

        assertEquals("events", actualModelAndView.getViewName());
        assertFalse(actualModelMap.containsAttribute("events"));
        assertTrue(actualModelMap.containsAttribute("message"));
        assertEquals("Can not to parse string 18.05.2022 15:30 to date object", actualModelMap.getAttribute("message"));

    }

    @Test
    public void createEventWithCorrectFormatOfDateShouldReturnModelAndViewWithEvent() {
        Event event = new Event(1L, "Test event", new Date(System.currentTimeMillis()), BigDecimal.ONE);

        when(bookingFacade.createEvent(any())).thenReturn(event);

        ModelAndView actualModelAndView = eventsController.createEvent("Test event", "18-05-2022 15:30", BigDecimal.TEN);

        verify(bookingFacade, times(1)).createEvent(any());

        ModelMap actualModelMap = actualModelAndView.getModelMap();

        assertEquals("event", actualModelAndView.getViewName());
        assertTrue(actualModelMap.containsAttribute("event"));
        assertFalse(actualModelMap.containsAttribute("message"));
    }

    @Test
    public void createEventWithCorrectFormatOfDateShouldReturnModelAndViewWithMessage() {
        when(bookingFacade.createEvent(any())).thenReturn(null);

        ModelAndView actualModelAndView = eventsController.createEvent("Test event", "18-05-2022 15:30", BigDecimal.ZERO);

        verify(bookingFacade, times(1)).createEvent(any());

        ModelMap actualModelMap = actualModelAndView.getModelMap();

        assertEquals("event", actualModelAndView.getViewName());
        assertFalse(actualModelMap.containsAttribute("event"));
        assertTrue(actualModelMap.containsAttribute("message"));
        assertEquals("Can not to create an event", actualModelMap.getAttribute("message"));
    }

    @Test
    public void createEventWithWrongFormatOfDateShouldReturnModelAndViewWithMessage() {
        ModelAndView actualModelAndView = eventsController.createEvent("Test event", "18.05.2022 15:30", BigDecimal.ZERO);

        verify(bookingFacade, times(0)).createEvent(any());

        ModelMap actualModelMap = actualModelAndView.getModelMap();

        assertEquals("event", actualModelAndView.getViewName());
        assertFalse(actualModelMap.containsAttribute("event"));
        assertTrue(actualModelMap.containsAttribute("message"));
        assertEquals("Can not to parse string 18.05.2022 15:30 to date object", actualModelMap.getAttribute("message"));
    }

    @Test
    public void updateEventWithCorrectDateFormatShouldReturnModelAndViewWithEvent() {
        Event event = new Event(1L, "Test event", new Date(System.currentTimeMillis()), BigDecimal.ONE);

        when(bookingFacade.updateEvent(any())).thenReturn(event);

        ModelAndView actualModelAndView = eventsController.updateEvent(1L, "Test title", "18-05-2022 15:30", BigDecimal.ZERO);

        verify(bookingFacade, times(1)).updateEvent(any());

        ModelMap actualModelMap = actualModelAndView.getModelMap();

        assertEquals("event", actualModelAndView.getViewName());
        assertTrue(actualModelMap.containsAttribute("event"));
        assertFalse(actualModelMap.containsAttribute("message"));
    }

    @Test
    public void updateEventWithCorrectDateFormatShouldReturnModelAndViewWithMessage() {
        when(bookingFacade.updateEvent(any())).thenReturn(null);

        ModelAndView actualModelAndView = eventsController.updateEvent(1L, "Test title", "18-05-2022 15:30", BigDecimal.ZERO);

        verify(bookingFacade, times(1)).updateEvent(any());

        ModelMap actualModelMap = actualModelAndView.getModelMap();

        assertEquals("event", actualModelAndView.getViewName());
        assertFalse(actualModelMap.containsAttribute("event"));
        assertTrue(actualModelMap.containsAttribute("message"));
        assertEquals("Can not to update an event with id: 1", actualModelMap.getAttribute("message"));
    }

    @Test
    public void updateEventWithWrongDateFormatShouldReturnModelAndViewWithMessage() {
        ModelAndView actualModelAndView = eventsController.updateEvent(1L, "Test title", "18.05-2022 15:30", BigDecimal.ZERO);

        verify(bookingFacade, times(0)).updateEvent(any());

        ModelMap actualModelMap = actualModelAndView.getModelMap();

        assertEquals("event", actualModelAndView.getViewName());
        assertFalse(actualModelMap.containsAttribute("event"));
        assertTrue(actualModelMap.containsAttribute("message"));
        assertEquals("Can not to parse string 18.05-2022 15:30 to date object", actualModelMap.getAttribute("message"));
    }

    @Test
    public void deleteEventWithExistingEventIdShouldReturnModelAndViewWithPositiveMessage() {
        when(bookingFacade.deleteEvent(anyLong())).thenReturn(true);

        ModelAndView actualModelAndView = eventsController.deleteEvent(1L);

        verify(bookingFacade, times(1)).deleteEvent(anyLong());

        assertEquals("event", actualModelAndView.getViewName());
        assertTrue(actualModelAndView.getModelMap().containsAttribute("message"));
        assertEquals("The event with id 1 successfully deleted", actualModelAndView.getModelMap().getAttribute("message"));
    }

    @Test
    public void deleteEventWithExistingEventIdShouldReturnModelAndViewWithNegativeMessage() {
        when(bookingFacade.deleteEvent(anyLong())).thenReturn(false);

        ModelAndView actualModelAndView = eventsController.deleteEvent(1L);

        verify(bookingFacade, times(1)).deleteEvent(anyLong());

        assertEquals("event", actualModelAndView.getViewName());
        assertTrue(actualModelAndView.getModelMap().containsAttribute("message"));
        assertEquals("The event with id 1 not deleted", actualModelAndView.getModelMap().getAttribute("message"));
    }
}