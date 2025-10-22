package com.example.myApp.repository;

import com.example.myApp.enity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail (String email);
    List<User> findAllByOrderByIdAsc();
    boolean existsByEmail(String email);
    Optional<User> findByEmailAndPhone(String email, String phone);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :id")
    void banUser(@Param("id") int id);


    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = true WHERE u.id = :id")
    void unbanUser(@Param("id") int id);
}
