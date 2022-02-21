package com.backend.resource;

import static org.springframework.http.HttpStatus.OK;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.backend.domain.HttpResponse;
import com.backend.domain.User;
import com.backend.exception.ExceptionHandling;
import com.backend.exception.domain.EmailExistException;
import com.backend.exception.domain.EmailNotFoundException;
import com.backend.exception.domain.EmailValidationException;
import com.backend.exception.domain.ReceiverAndDonorException;
import com.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = { "/user" })
@Slf4j
public class UserResource extends ExceptionHandling {

    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Getting all the users.");
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/donors")
    public ResponseEntity<List<User>> getAllDonors() {
        log.info("Getting all the donors");
        List<User> users = userService.getDonor(true);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/receivers")
    public ResponseEntity<List<User>> getAllReceivers() {
        log.info("Getting all the receivers");
        List<User> users = userService.getReceiver(true);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> registerUser(@RequestBody Map<String, Object> user) throws AddressException,
            MessagingException, EmailValidationException, EmailNotFoundException, EmailExistException,
            ReceiverAndDonorException {
        User newUser = userService.register(
                (String) user.get("firstName"),
                (String) user.get("lastName"),
                (String) user.get("email"),
                (Long) user.get("mobileNo"),
                (String) user.get("bloodGroup"),
                (Integer) user.get("age"),
                (Boolean) user.get("receiver"),
                (Boolean) user.get("donor"));
        return reponse(OK, "Successfully registered the user with email id:  " + newUser.getEmail());
    }

    @PostMapping("/usersByAgeAndBloodGroup")
    public ResponseEntity<List<User>> getUsersByAgeAndBloodGroup(@RequestBody Map<String, Object> details) {
        List<User> users = userService.getUsersByAgeAndBloodGroup((Integer) details.get("age"),
                (String) details.get("bloodGroup"));
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/usersByRegisteredDate")
    public ResponseEntity<List<User>> getUsersByRegisteredDate(@RequestBody Map<String, String> details) {
        List<User> users = userService.getUsersByRegisteredDate(details.get("date"));
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/updateUser")
    public ResponseEntity<HttpResponse> updateUser(@RequestBody Map<String, Object> details)
            throws EmailNotFoundException, EmailExistException {
        User user = userService.updateUser(
                (String) details.get("registeredEmail"),
                (String) details.get("firstName"),
                (String) details.get("lastName"),
                (String) details.get("email"),
                (Long) details.get("mobileNo"),
                (String) details.get("bloodGroup"),
                (Integer) details.get("age"),
                (Boolean) details.get("receiver"),
                (Boolean) details.get("donor"));
        return reponse(OK, "Successfully registered the user with email id:  " + user.getEmail());
    }

    private ResponseEntity<HttpResponse> reponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase(), message), httpStatus);
    }

}
