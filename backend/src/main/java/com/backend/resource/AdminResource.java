package com.backend.resource;

import static com.backend.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.backend.domain.Admin;
import com.backend.domain.AdminPrincipal;
import com.backend.domain.HttpResponse;
import com.backend.exception.ExceptionHandling;
import com.backend.exception.domain.EmailExistException;
import com.backend.exception.domain.EmailNotFoundException;
import com.backend.exception.domain.EmailValidationException;
import com.backend.exception.domain.PasswordDidNotMatchException;
import com.backend.service.AdminService;
import com.backend.utility.JWTTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(originPatterns = { "http://localhost:3000**" })
@RestController
@RequestMapping(path = { "/", "/admin" })
@Slf4j
public class AdminResource extends ExceptionHandling {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @GetMapping("/admins")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return new ResponseEntity<>(adminService.getAdminByRole(), OK);
    }

    @GetMapping("/adminsByToday")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<Admin>> getAllAdminsByToday()
            throws ParseException {
        return new ResponseEntity<>(adminService.getAllAdminsByJoinedDateByToday(), OK);
    }

    @GetMapping("/adminsByDateBefore")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<Admin>> getAllAdminsByDateBefore(@RequestBody Map<String, String> date)
            throws ParseException {
        return new ResponseEntity<>(adminService.getAllAdminsByJoinedDateBefore(date.get("date")), OK);
    }

    @GetMapping("/adminsByDateAfter")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<Admin>> getAllAdminsByDateAfter(@RequestBody Map<String, String> date)
            throws ParseException {
        return new ResponseEntity<>(adminService.getAllAdminsByJoinedDateAfter(date.get("date")), OK);
    }

    @GetMapping("/adminByEmail")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Admin> getAdminByEmail(@RequestBody Map<String, String> email)
            throws EmailValidationException {
        return new ResponseEntity<>(adminService.getAdminByEmail(email.get("email")), OK);
    }

    @GetMapping("/countAdmin")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Map<String, Integer>> countAdminsByDate(@RequestBody Map<String, String> date)
            throws ParseException {
        Map<String, Integer> count = new HashMap<>();
        count.put("count", adminService.countAdminsByJoiningDate(date.get("date")));
        return new ResponseEntity<>(count, OK);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Admin> register(@RequestBody Map<String, Object> body)
            throws AddressException, MessagingException, EmailValidationException, EmailNotFoundException,
            EmailExistException {

        log.info("Registering the new user.");
        Admin admin = adminService.register((String) body.get("firstName"), (String) body.get("lastName"),
                (String) body.get("email"), 1, (Boolean) body.get("isActive"), (Boolean) body.get("isNotLocked"));
        return new ResponseEntity<>(admin, OK);
    }

    @PostMapping("/updateAdmin")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Admin> updateAdmin(@RequestBody Map<String, Object> body) throws EmailValidationException {
        Admin admin = adminService.updateDetails((String) body.get("firstName"), (String) body.get("lastName"),
                (String) body.get("email"), (Boolean) body.get("isActive"), (Boolean) body.get("isNotLocked"));
        return new ResponseEntity<>(admin, OK);
    }

    @PostMapping("/unlockAdmin")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<HttpResponse> unlockAdmin(
            @RequestBody Map<String, String> email) throws EmailNotFoundException, EmailValidationException {
        log.warn("Unlocking the admin.");
        adminService.unlockAdmin(email.get("email"));
        return reponse(OK, "Admin unlocked successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Admin admin) throws EmailValidationException {
        log.info("Authenticating the user.");
        authenticate(admin.getEmail(), admin.getPassword());
        Map<String, Object> details = new HashMap<>();
        Admin loggedInAdmin = adminService.getAdminByEmail(admin.getEmail());
        AdminPrincipal adminPrincipal = new AdminPrincipal(loggedInAdmin);
        HttpHeaders jwtHeaders = getJwHeaders(adminPrincipal);
        details.put("message", "Sucessfully logged in.");
        details.put("user", loggedInAdmin);
        return new ResponseEntity<>(details, jwtHeaders, OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<HttpResponse> resetPassword(@RequestBody Map<String, Object> details)
            throws EmailNotFoundException, LockedException, PasswordDidNotMatchException, EmailValidationException {
        adminService.resetPassword((String) details.get("email"), (String) details.get("newPassword"),
                (String) details.get("confirmPassword"));
        return reponse(OK, "Successfully changed the password.");
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<HttpResponse> deleteAdmin(@PathVariable("id") String adminId) {
        log.warn("Deleting the admin.");
        adminService.deleteAdminById(adminId);
        return reponse(OK, "Admin deleted successfully.");
    }

    private HttpHeaders getJwHeaders(AdminPrincipal adminPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateToken(adminPrincipal));
        return headers;
    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    private ResponseEntity<HttpResponse> reponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(
                new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase(), message), httpStatus);
    }

}
