package ua.epam.mishchenko.ticketbooking.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Ticket {

    private String eventId;
    private Integer place;
    private Category category;

}
