package ua.epam.mishchenko.ticketbooking.model;


import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

/**
 * The type User.
 */
@Document(collection = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private UserAccount account;
    private List<Ticket> tickets;

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setEventId(this.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

