package com.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.domain.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Admin findAdminByEmail(String email);

    void deleteByAdminId(String adminId);

    @Query("select u from Admin u where u.joinedDate > ?1")
    List<Admin> findAllByJoinedDateAfter(LocalDateTime localDateTime);

    @Query("select u from Admin u where u.joinedDate < ?1")
    List<Admin> findAllByJoinedDateBefore(LocalDateTime localDateTime);

    @Query("select u from Admin u where u.joinedDate between ?1 and ?2 order by u.joinedDate")
    List<Admin> findAllByJoinedDate(LocalDateTime beforeDate, LocalDateTime afterDate);

    // @Query("select u from Admin u inner join u.roles r where r.name in :role")
    // List<Admin> findAllByRoleName(@Param("role") String role);

    List<Admin> findAllByRolesName(String name);

    Integer countByJoinedDate(LocalDateTime localDateTime);

}
