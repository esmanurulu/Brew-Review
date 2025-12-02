package com.brewandreview.repository;

import com.brewandreview.model.Follow;
import com.brewandreview.model.User;
import com.brewandreview.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    // Kullanıcının takip ettiği kişileri getir
    List<Follow> findByUser_UserId(Long userId);

    // Takip ediyor mu kontrolü
    boolean existsByUserAndEmployee(User user, Employee employee);

    // Takipten çıkma islemi
    void deleteByUserAndEmployee(User user, Employee employee);

    // Beni takip edenleri bul (User olarak)
    List<Follow> findByFollowedUser_UserId(Long userId);

    // Beni takip edenleri bul (Barista olarak)
    List<Follow> findByEmployee_EmployeeId(Long employeeId);
}