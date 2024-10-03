package ua.epam.mishchenko.ticketbooking.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

    /**
     * The user account id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user entity.
     */
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The amount of user money.
     */
    @Column(name = "money", nullable = false)
    private BigDecimal money;

    public UserAccount() {
    }

    public UserAccount(User user, BigDecimal money) {
        this.user = user;
        this.money = money;
    }

    public UserAccount(Long id, User user, BigDecimal money) {
        this.id = id;
        this.user = user;
        this.money = money;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
