package ua.epam.mishchenko.ticketbooking.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.UserAccount;
import ua.epam.mishchenko.ticketbooking.repository.UserRepository;
import ua.epam.mishchenko.ticketbooking.service.UserAccountService;

import java.math.BigDecimal;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private static final Logger log = LoggerFactory.getLogger(UserAccountServiceImpl.class);

    private final UserRepository userRepository;


    public UserAccountServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserAccount refillAccount(String userId, BigDecimal money) {
        log.info("Refilling user account for user with id: {}", userId);
        try {
            thrownRuntimeExceptionIfMoneyLessZero(money);
            throwRuntimeExceptionIfUserNotExist(userId);
            User user = userRepository.findById(userId).orElse(new User());
            UserAccount userAccount = getUserAccountAndRefillIfNotExistCreate(user, money);
            user.setAccount(userAccount);
            userRepository.save(user);
            log.info("The user account with user id {} successfully refilled", userId);
            return userAccount;
        } catch (RuntimeException e) {
            log.warn("Can not to refill account with user id: {}", userId);
            return null;
        }
    }

    private void thrownRuntimeExceptionIfMoneyLessZero(BigDecimal money) {
        if (money.compareTo(BigDecimal.ZERO) < 1) {
            throw new RuntimeException("The money can not to be less zero");
        }
    }

    private UserAccount getUserAccountAndRefillIfNotExistCreate(User user, BigDecimal money) {
        String userId = user.getId();
        UserAccount userAccount = user.getAccount();
        if (userAccount == null) {
            return createNewUserAccount(userId, money);
        }
        BigDecimal money1 = userAccount.getMoney();
        userAccount.setMoney(money1.add(money));
        return userAccount;
    }

    private UserAccount createNewUserAccount(String userId, BigDecimal money) {
        log.info("The user account with user id {} does not exist", userId);
        log.info("Creating new user account for user with id {}", userId);
        UserAccount userAccount = new UserAccount();
        userAccount.setMoney(money);
        log.info("The user account for user with id {} successfully created", userId);
        return userAccount;
    }

    private void throwRuntimeExceptionIfUserNotExist(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("The user with id " + userId + " does not exist");
        }
    }
}
