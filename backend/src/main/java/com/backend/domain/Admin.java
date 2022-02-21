package com.backend.domain;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;
// import static javax.persistence.TemporalType.TIMESTAMP;

import java.time.LocalDateTime;
import java.util.ArrayList;
// import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
// import javax.persistence.ManyToOne;
// import javax.persistence.Temporal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

        @Id
        @GeneratedValue(strategy = AUTO)
        @Column(nullable = false, updatable = false)
        @JsonProperty(access = WRITE_ONLY)
        private Integer id;

        private String adminId;

        private String firstName;

        private String lastName;

        @JsonProperty(access = WRITE_ONLY)
        private String password;

        private String email;

        @Column(columnDefinition = "TIMESTAMP")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Kolkata")
        private LocalDateTime lastLoginDate;

        @Column(columnDefinition = "TIMESTAMP")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Kolkata")
        private LocalDateTime lastLoginDateDisplay;

        @Column(columnDefinition = "TIMESTAMP")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Kolkata")
        private LocalDateTime joinedDate;

        @ManyToMany(fetch = EAGER)
        @JoinTable(name = "admin_roles", joinColumns = {
                        @JoinColumn(name = "admin_id")
        }, inverseJoinColumns = {
                        @JoinColumn(name = "role_id")
        })
        private List<Role> roles = new ArrayList<>();

        private Boolean isActive;

        private Boolean isNotLocked;

}