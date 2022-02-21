package com.backend.service;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.backend.domain.User;
import com.backend.exception.domain.EmailExistException;
import com.backend.exception.domain.EmailNotFoundException;
import com.backend.exception.domain.EmailValidationException;
import com.backend.exception.domain.ReceiverAndDonorException;

public interface UserService {

        User register(String fname, String lname, String email, Long mobileNo, String bloodGroup, Integer age,
                        Boolean receiver, Boolean donor) throws AddressException, MessagingException, EmailValidationException, EmailNotFoundException, EmailExistException, ReceiverAndDonorException;

        List<User> getAllUsers();

        List<User> getUsersByAgeAndBloodGroup(Integer age, String bloodGroup);

        List<User> getUsersByRegisteredDate(String date);

        List<User> getUsersRegisteredBeforeDate(String date);

        List<User> getReceiver(Boolean receiver);

        List<User> getDonor(Boolean donor);

        User getUserByEmail(String email);

        Long countUserByBloodGroup(String bloodGroup);

        Long countUserByAge(Integer age);

        Long countReceivers(Boolean receiver);

        Long countDonors(Boolean donor);

        User updateUser(String registeredEmail, String fname, String lname, String email, Long mobileNo,
                        String bloodGroup, Integer age, Boolean receiver, Boolean donor) throws EmailNotFoundException, EmailExistException;

        void deleteUserById(Long id);

}
