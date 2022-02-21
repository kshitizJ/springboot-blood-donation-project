package com.backend.service;

import java.text.ParseException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.backend.domain.Admin;
import com.backend.domain.Role;
import com.backend.exception.domain.EmailExistException;
import com.backend.exception.domain.EmailNotFoundException;
import com.backend.exception.domain.EmailValidationException;
import com.backend.exception.domain.PasswordDidNotMatchException;

import org.springframework.security.authentication.LockedException;

public interface AdminService {

        Admin register(String firstName, String lastName, String email, Integer role, Boolean isActive,
                        Boolean isNotLocked) throws AddressException, MessagingException, EmailValidationException,
                        EmailNotFoundException, EmailExistException;

        List<Admin> getAllAdmins();

        List<Admin> getAllAdminsByJoinedDateByToday();

        List<Admin> getAllAdminsByJoinedDateBefore(String date) throws ParseException;

        List<Admin> getAllAdminsByJoinedDateAfter(String date);

        Admin getAdminByEmail(String email) throws EmailValidationException;

        List<Admin> getAdminByRole();

        Admin updateDetails(String firstName, String lastName, String email, Boolean isActive, Boolean isNotLocked)
                        throws EmailValidationException;

        Integer countAdminsByJoiningDate(String date) throws ParseException;

        void deleteAdminById(String adminId);

        void unlockAdmin(String email) throws EmailNotFoundException, EmailValidationException;

        void resetPassword(String email, String password, String confirmPassword)
                        throws EmailNotFoundException, LockedException, PasswordDidNotMatchException,
                        EmailValidationException;

        Role saveRole(Role role);

}
