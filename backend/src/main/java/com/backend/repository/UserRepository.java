package com.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByEmail(String email);

    List<User> findAllByAgeAndBloodGroup(Integer age, String bloodGroup);

    @Query("select u from User u where u.registerDate between ?1 and ?2 order by u.registerDate")
    List<User> findAllByRegisterDate(LocalDateTime beforeDate, LocalDateTime afterDate);

    List<User> findAllByReceiver(Boolean receiver);

    List<User> findAllByDonor(Boolean donor);

    @Query("select u from User u where u.registerDate < ?1")
    List<User> findAllByRegisterDateBefore(LocalDateTime date);

    @Query("select u from User u where u.registerDate > ?1")
    List<User> findAllByRegisterDateAfter(LocalDateTime date);

    // @Query("SELECT COUNT(u) FROM User u WHERE u.blood_group=?1")
    Long countByBloodGroup(String bloodGroup);

    // @Query("SELECT COUNT(u) FROM User u WHERE u.age=?1")
    Long countByAge(Integer age);

    Long countByReceiver(Boolean receiver);

    Long countByDonor(Boolean donor);

    void deleteById(Long id);

}
