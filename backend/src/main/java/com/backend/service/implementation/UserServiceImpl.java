package com.backend.service.implementation;

import static com.backend.constant.SecurityConstant.EMAIL_REGEX;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.transaction.Transactional;

import com.backend.domain.User;
import com.backend.exception.domain.EmailExistException;
import com.backend.exception.domain.EmailNotFoundException;
import com.backend.exception.domain.EmailValidationException;
import com.backend.exception.domain.ReceiverAndDonorException;
import com.backend.repository.UserRepository;
// import com.backend.service.EmailService;
import com.backend.service.UserService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private EmailService emailService;

    @Override
    public User register(String firstName, String lastName, String email, Long mobileNo, String bloodGroup, Integer age,
            Boolean receiver, Boolean donor) throws AddressException, MessagingException, EmailValidationException,
            EmailNotFoundException, EmailExistException, ReceiverAndDonorException {

        if (!validateEmail(email))
            throw new EmailValidationException("Email validation failed.");
        if (receiver && donor)
            throw new ReceiverAndDonorException("User can either be donor or reciever at a particular time.");
        validateNewEmail(EMPTY, email);
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setMobileNo(mobileNo);
        user.setBloodGroup(bloodGroup);
        user.setAge(age);
        user.setReceiver(receiver);
        user.setDonor(donor);
        user.setRegisterDate(LocalDateTime.now());

        // log.info("Sending email to new registered user at: {}", email);
        // emailService.sendSuccessfullyRegisterMessageForUser(firstName, email);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByAgeAndBloodGroup(Integer age, String bloodGroup) {
        return userRepository.findAllByAgeAndBloodGroup(age, bloodGroup);
    }

    @Override
    public List<User> getUsersByRegisteredDate(String date) {
        return userRepository.findAllByRegisterDate(
                LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay(),
                LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).atTime(23, 59, 59));
    }

    @Override
    public List<User> getUsersRegisteredBeforeDate(String date) {
        return userRepository.findAllByRegisterDateBefore(
                LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay());
    }

    @Override
    public List<User> getReceiver(Boolean receiver) {
        return userRepository.findAllByReceiver(receiver);
    }

    @Override
    public List<User> getDonor(Boolean donor) {
        return userRepository.findAllByDonor(donor);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public Long countUserByBloodGroup(String bloodGroup) {
        return userRepository.countByBloodGroup(bloodGroup);
    }

    @Override
    public Long countUserByAge(Integer age) {
        return userRepository.countByAge(age);
    }

    @Override
    public Long countReceivers(Boolean receiver) {
        return userRepository.countByReceiver(receiver);
    }

    @Override
    public Long countDonors(Boolean donor) {
        return userRepository.countByDonor(donor);
    }

    @Override
    public User updateUser(String registeredEmail, String firstName, String lastName, String email, Long mobileNo,
            String bloodGroup, Integer age, Boolean receiver, Boolean donor)
            throws EmailNotFoundException, EmailExistException {

        User user = validateNewEmail(registeredEmail, email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setMobileNo(mobileNo);
        user.setBloodGroup(bloodGroup);
        user.setAge(age);
        user.setReceiver(receiver);
        user.setDonor(donor);
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private User validateNewEmail(String currentEmail, String email)
            throws EmailNotFoundException, EmailExistException {
        User userByNewEmail = getUserByEmail(email);
        log.info("Checking if the current email is blank or not.");
        if (StringUtils.isNotBlank(currentEmail)) {
            User currentUser = getUserByEmail(currentEmail);
            if (currentUser == null) {
                throw new EmailNotFoundException("No user found with email address:  {}" + currentEmail);
            }
            if (userByNewEmail != null && !currentUser.getEmail().equals(userByNewEmail.getEmail())) {
                throw new EmailExistException("Email already exist");
            }
            log.info("Everything is fine and we can update the user profile.");
            return currentUser;
        } else {
            log.info("Checking if the new email id exist in the database or not.");
            if (userByNewEmail != null) {
                throw new EmailExistException("Email already exist.");
            }
            return null;
        }
    }

    private Boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        log.info("\n" + email + "  :  " + matcher.matches() + "\n");
        return matcher.matches();
    }

}
