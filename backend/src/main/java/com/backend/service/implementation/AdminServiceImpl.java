package com.backend.service.implementation;

import static com.backend.constant.SecurityConstant.EMAIL_REGEX;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.transaction.Transactional;

import com.backend.domain.Admin;
import com.backend.domain.AdminPrincipal;
import com.backend.domain.Role;
import com.backend.exception.domain.EmailExistException;
import com.backend.exception.domain.EmailNotFoundException;
import com.backend.exception.domain.EmailValidationException;
import com.backend.exception.domain.PasswordDidNotMatchException;
import com.backend.repository.AdminRepository;
import com.backend.repository.RoleRepository;
import com.backend.service.AdminService;
// import com.backend.service.EmailService;
import com.backend.service.LoginAttemptService;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Qualifier("userDetailsService")
@Slf4j
public class AdminServiceImpl implements AdminService, UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginAttemptService loginAttemptService;

    // @Autowired
    // private EmailService emailService;

    @Override
    public Admin register(String firstName, String lastName, String email, Integer role, Boolean isActive,
            Boolean isNotLocked) throws AddressException, MessagingException, EmailValidationException,
            EmailNotFoundException, EmailExistException {

        validateNewEmail(EMPTY, email);
        String password = RandomStringUtils.randomAlphanumeric(8);
        Admin admin = new Admin();
        List<Role> roles = new ArrayList<>();
        if (role == 1)
            roles.add(roleRepository.findById(2).get());
        else {
            roles.add(roleRepository.findById(1).get());
            roles.add(roleRepository.findById(2).get());
        }
        admin.setAdminId(generateAdminId());
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setEmail(email);
        admin.setJoinedDate(LocalDateTime.now());
        admin.setLastLoginDate(null);
        admin.setLastLoginDateDisplay(null);
        admin.setIsActive(isActive);
        admin.setIsNotLocked(isNotLocked);
        admin.setRoles(roles);

        log.info("Sending email to new registered admin at:  {}", email);
        System.out.println("\n\n\n Password:    " + password + "\n\n\n");
        // emailService.sendSuccessfullyRegisterMessageForAdmin(firstName, email,
        // password);
        return adminRepository.save(admin);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public List<Admin> getAllAdminsByJoinedDateByToday() {
        return adminRepository.findAllByJoinedDate(
                LocalDate.parse(new SimpleDateFormat("dd-MM-yyyy")
                        .format(new Date()), DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay(),
                LocalDate.parse(new SimpleDateFormat("dd-MM-yyyy")
                        .format(new Date()), DateTimeFormatter.ofPattern("dd-MM-yyyy")).atTime(23, 59, 59));
    }

    @Override
    public List<Admin> getAllAdminsByJoinedDateBefore(String date) throws ParseException {
        return adminRepository.findAllByJoinedDateBefore(
                LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay());
    }

    @Override
    public List<Admin> getAllAdminsByJoinedDateAfter(String date) {
        return adminRepository.findAllByJoinedDateAfter(
                LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay());
    }

    @Override
    public Admin getAdminByEmail(String email) throws EmailValidationException {
        if (!validateEmail(email)) {
            throw new EmailValidationException("Email validation failed.");
        }
        return adminRepository.findAdminByEmail(email);
    }

    @Override
    public List<Admin> getAdminByRole() {
        return adminRepository.findAllByRolesName("ROLE_SUPER_ADMIN");
    }

    @Override
    public Admin updateDetails(String firstName, String lastName, String email,
            Boolean isActive, Boolean isNotLocked) throws EmailValidationException {

        Admin admin = getAdminByEmail(email);
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setEmail(email);
        admin.setIsActive(isActive);
        admin.setIsNotLocked(isNotLocked);
        return adminRepository.save(admin);

    }

    @Override
    public Integer countAdminsByJoiningDate(String date) throws ParseException {
        return adminRepository.countByJoinedDate(
                LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).atStartOfDay());
    }

    @Override
    public void deleteAdminById(String adminId) {
        adminRepository.deleteByAdminId(adminId);
    }

    @Override
    public void unlockAdmin(String email) throws EmailNotFoundException, EmailValidationException {
        Admin admin = adminRepository.findAdminByEmail(email);
        if (admin == null) {
            throw new EmailNotFoundException("Email address doesnot exist.");
        }
        if (!validateEmail(email)) {
            throw new EmailValidationException("Email validation failed.");
        }
        admin.setIsNotLocked(true);
        adminRepository.save(admin);
    }

    @Override
    public void resetPassword(String email, String password, String confirmPassword)
            throws EmailNotFoundException, LockedException, PasswordDidNotMatchException, EmailValidationException {
        Admin admin = getAdminByEmail(email);
        if (admin == null) {
            throw new EmailNotFoundException("Email address doesnot exist.");
        } else if (!admin.getIsNotLocked()) {
            throw new LockedException(
                    "You cannot reset the password because your account is locked, try contacting admintration.");
        } else if (!password.equals(confirmPassword)) {
            throw new PasswordDidNotMatchException("Your password and confirm password didn't match.");
        } else {
            admin.setPassword(passwordEncoder.encode(password));
            adminRepository.save(admin);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.info("Finding email:  {}  in database.", email);
        Admin admin = adminRepository.findAdminByEmail(email);
        if (admin == null)
            throw new UsernameNotFoundException("Invalid Email address:  " + email);
        else {
            try {
                validateLoginAttempt(admin);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            admin.setLastLoginDateDisplay(admin.getLastLoginDate());
            admin.setLastLoginDate(LocalDateTime.now());
            adminRepository.save(admin);
            AdminPrincipal adminPrincipal = new AdminPrincipal(admin);
            return adminPrincipal;
        }

    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    private void validateLoginAttempt(Admin admin) throws ExecutionException {
        if (admin.getIsNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempt(admin.getEmail()))
                admin.setIsNotLocked(false);
            else
                admin.setIsNotLocked(true);
        } else
            loginAttemptService.evictUserFromLoginAttemptCache(admin.getEmail());
    }

    private String generateAdminId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private Admin validateNewEmail(String currentEmail, String newEmail)
            throws EmailValidationException, EmailNotFoundException, EmailExistException {
        log.info("Validating the email.");
        if (!validateEmail(currentEmail) && !validateEmail(newEmail)) {
            throw new EmailValidationException("Email validation failed.");
        }
        Admin adminByNewEmail = getAdminByEmail(newEmail);
        log.info("Checking if the current email is blank or not.");
        if (StringUtils.isNotBlank(currentEmail)) {
            Admin adminByCurrentEmail = adminRepository.findAdminByEmail(currentEmail);
            if (adminByCurrentEmail == null)
                throw new EmailNotFoundException("No admin found by email:  " + currentEmail);
            if (adminByNewEmail != null && adminByCurrentEmail.getEmail().equals(adminByNewEmail.getEmail()))
                throw new EmailExistException("Email already exist.");
            log.info("Everything is fine and updating the admin profile.");
            return adminByCurrentEmail;
        } else {
            log.info("Checking if the new email already exist in the database or not.");
            if (adminByNewEmail != null)
                throw new EmailExistException("Email already exist");
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
