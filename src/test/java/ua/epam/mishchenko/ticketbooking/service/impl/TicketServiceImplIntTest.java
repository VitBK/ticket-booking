package ua.epam.mishchenko.ticketbooking.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ua.epam.mishchenko.ticketbooking.model.Category;
import ua.epam.mishchenko.ticketbooking.model.Ticket;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
@Sql(value = {"classpath:sql/clear-database.sql", "classpath:sql/insert-data.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:sql/clear-database.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class TicketServiceImplIntTest {

    @Autowired
    private TicketServiceImpl ticketService;

    @MockBean
    private TicketRepository ticketRepository;

    @Test
    public void bookTicketWithRuntimeExceptionShouldRollback() {
        when(ticketRepository.existsByEventIdAndPlaceAndCategory(anyLong(), anyInt(), any(Category.class)))
                .thenReturn(false);
        when(ticketRepository.save(any(Ticket.class)))
                .thenThrow(RuntimeException.class);

        Ticket ticket = ticketService.bookTicket(1, 1, 30, Category.PREMIUM);

        assertNull(ticket);

    }
}
