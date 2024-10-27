package ua.epam.mishchenko.ticketbooking.model;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * The type Event.
 */
@Document(collection = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Event {

    @Id
    private String id;
    private String title;
    private Date date;
    private BigDecimal ticketPrice;
    private List<Ticket> tickets;

    /**
     * Adds ticket to the tickets list.
     *
     * @param ticket the ticket
     */
    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setEventId(this.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
