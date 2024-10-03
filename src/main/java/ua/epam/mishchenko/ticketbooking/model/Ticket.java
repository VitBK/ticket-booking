package ua.epam.mishchenko.ticketbooking.model;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * The type Ticket.
 */
@Entity
@Table(name = "tickets")
public class Ticket {

    /**
     * The Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The User entity.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The Event entity.
     */
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    /**
     * The Place.
     */
    @Column(name = "place", nullable = false)
    private Integer place;

    /**
     * The Category.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    /**
     * Instantiates a new Ticket.
     */
    public Ticket() {
    }

    /**
     * Instantiates a new Ticket.
     *
     * @param id       the id
     * @param user     the user entity
     * @param event    the event entity
     * @param place    the place
     * @param category the category
     */
    public Ticket(Long id, User user, Event event, int place, Category category) {
        this.id = id;
        this.user = user;
        this.event = event;
        this.place = place;
        this.category = category;
    }

    /**
     * Instantiates a new Ticket.
     *
     * @param user     the user entity
     * @param event    the event entity
     * @param place    the place
     * @param category the category
     */
    public Ticket(User user, Event event, int place, Category category) {
        this.user = user;
        this.event = event;
        this.place = place;
        this.category = category;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets event entity.
     *
     * @return the event entity
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Sets event entity.
     *
     * @param event the event id
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Gets user entity.
     *
     * @return the user entity
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets user entity.
     *
     * @param user the user entity
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Gets place.
     *
     * @return the place
     */
    public int getPlace() {
        return place;
    }

    /**
     * Sets place.
     *
     * @param place the place
     */
    public void setPlace(int place) {
        this.place = place;
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id) && Objects.equals(user, ticket.user) && Objects.equals(event, ticket.event) && Objects.equals(place, ticket.place) && category == ticket.category;
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    public int hashCode() {
        return Objects.hash(id, user, event, place, category);
    }

    /**
     * To string string.
     *
     * @return the string
     */
    public String toString() {
        return "{" +
                "'id' : " + id +
                ", 'userId' : " + user.getId() +
                ", 'eventId' : " + event.getId() +
                ", 'place' : " + place +
                ", 'category' : '" + category +
                "'}";
    }
}
