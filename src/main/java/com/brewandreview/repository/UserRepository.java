package com.brewandreview.repository;

import com.brewandreview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Kullanıcı adına göre arama yapan sihirli metod
    User findByUsername(String username);
}