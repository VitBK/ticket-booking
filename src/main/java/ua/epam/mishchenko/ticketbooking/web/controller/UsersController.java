package ua.epam.mishchenko.ticketbooking.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.epam.mishchenko.ticketbooking.facade.impl.BookingFacadeImpl;
import ua.epam.mishchenko.ticketbooking.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Users controller.
 */
@Controller
@RequestMapping("/users")
public class UsersController {

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(UsersController.class);

    /**
     * The Booking facade.
     */
    private final BookingFacadeImpl bookingFacade;

    /**
     * Instantiates a new Users controller.
     *
     * @param bookingFacade the booking facade
     */
    public UsersController(BookingFacadeImpl bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    /**
     * Show user by id model and view.
     *
     * @param id the id
     * @return the model and view
     */
    @GetMapping("/{id}")
    public ModelAndView showUserById(@PathVariable String id) {
        log.info("Showing user by id: {}", id);
        Map<String, Object> model = new HashMap<>();
        User userById = bookingFacade.getUserById(id);
        if (isNull(userById)) {
            model.put("message", "Can not to find user by id: " + id);
            log.info("Can not to find user by id: {}", id);
        } else {
            model.put("user", userById);
            log.info("The user by id: {} successfully found", id);
        }
        return new ModelAndView("user", model);
    }

    /**
     * Is null boolean.
     *
     * @param user the user
     * @return the boolean
     */
    private boolean isNull(User user) {
        return user == null;
    }

    /**
     * Show users by name model and view.
     *
     * @param name     the name
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the model and view
     */
    @GetMapping("/name/{name}")
    public ModelAndView showUsersByName(@PathVariable String name,
                                        @RequestParam int pageSize,
                                        @RequestParam int pageNum) {
        log.info("Showing users by name: {}", name);
        Map<String, Object> model = new HashMap<>();
        List<User> usersByName = bookingFacade.getUsersByName(name, pageSize, pageNum);
        if (usersByName.isEmpty()) {
            model.put("message", "Can not to find users by name: " + name);
            log.info("Can not to find users by name: {}", name);
        } else {
            model.put("users", usersByName);
            log.info("The users by name: {} successfully found", name);
        }
        return new ModelAndView("users", model);
    }

    /**
     * Show user by email model and view.
     *
     * @param email the email
     * @return the model and view
     */
    @GetMapping("/email/{email}")
    public ModelAndView showUserByEmail(@PathVariable String email) {
        log.info("Showing the user by email: {}", email);
        Map<String, Object> model = new HashMap<>();
        User userByEmail = bookingFacade.getUserByEmail(email);
        if (isNull(userByEmail)) {
            model.put("message", "Can not to find user by email: " + email);
            log.info("Can not to find user by email: {}", email);
        } else {
            model.put("user", userByEmail);
            log.info("The user by email: {} successfully found", email);
        }
        return new ModelAndView("user", model);
    }

    /**
     * Create user model and view.
     *
     * @param name  the name
     * @param email the email
     * @return the model and view
     */
    @PostMapping
    public ModelAndView createUser(@RequestParam String name,
                                   @RequestParam String email) {
        log.info("Creating user with name={} and email={}", name, email);
        Map<String, Object> model = new HashMap<>();
        User user = bookingFacade.createUser(createUserEntityWithoutId(name, email));
        if (isNull(user)) {
            model.put("message",
                    "Can not to create user with name - " + name + " and email - " + email);
            log.info("Can not to create user with name={} and email={}", name, email);
        } else {
            model.put("user", user);
            log.info("The user successfully created");
        }
        return new ModelAndView("user", model);
    }

    /**
     * Create user entity without id user.
     *
     * @param name  the name
     * @param email the email
     * @return the user
     */
    private User createUserEntityWithoutId(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    /**
     * Update user model and view.
     *
     * @param id    the id
     * @param name  the name
     * @param email the email
     * @return the model and view
     */
    @PutMapping
    public ModelAndView updateUser(@RequestParam String id,
                                   @RequestParam String name,
                                   @RequestParam String email) {
        log.info("Updating user with id: {}", id);
        Map<String, Object> model = new HashMap<>();
        User user = bookingFacade.updateUser(createUserEntityWithId(id, name, email));
        if (isNull(user)) {
            model.put("message", "Can not to update user with id: " + id);
            log.info("Can not to update user with id: {}", id);
        } else {
            model.put("user", user);
            log.info("The user with id: {} successfully update", id);
        }
        return new ModelAndView("user", model);
    }

    /**
     * Create user entity with id user.
     *
     * @param id    the id
     * @param name  the name
     * @param email the email
     * @return the user
     */
    private User createUserEntityWithId(String id, String name, String email) {
        User user = createUserEntityWithoutId(name, email);
        user.setId(id);
        return user;
    }

    /**
     * Delete user model and view.
     *
     * @param id the id
     * @return the model and view
     */
    @DeleteMapping("/{id}")
    public ModelAndView deleteUser(@PathVariable String id) {
        log.info("Deleting the user with id: {}", id);
        Map<String, Object> model = new HashMap<>();
        boolean isUserRemoved = bookingFacade.deleteUser(id);
        if (isUserRemoved) {
            model.put("message", "The user with id: " + id + " successfully removed");
            log.info("The user with id: {} successfully removed", id);
        } else {
            model.put("message", "The user with id: " + id + " not removed");
            log.info("The user with id: {} not removed", id);
        }
        return new ModelAndView("user", model);
    }
}
