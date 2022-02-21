package com.backend.domain;

import static javax.persistence.GenerationType.AUTO;
// import static javax.persistence.TemporalType.TIMESTAMP;

import java.time.LocalDateTime;
// import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
// import javax.persistence.Temporal;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Long mobileNo;

    private String bloodGroup;

    private Integer age;

    private Boolean receiver;

    private Boolean donor;

    /**
     * 
     * @Temporal specifies the date type. It has the single
     *           parameter value of type TemporalType. It can be either DATE, TIME
     *           or TIMESTAMP, depending on the underlying SQL type that we want to
     *           use for the mapping.
     * 
     */
    @Column(columnDefinition = "TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss", timezone = "Asia/Kolkata")
    private LocalDateTime registerDate;

}
