package ua.epam.mishchenko.ticketbooking.service;

import ua.epam.mishchenko.ticketbooking.model.UserAccount;

import java.math.BigDecimal;

public interface UserAccountService {

    UserAccount refillAccount(String userId, BigDecimal money);
}
