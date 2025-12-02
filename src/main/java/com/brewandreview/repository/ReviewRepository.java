package com.brewandreview.repository;

import com.brewandreview.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCafe_CafeId(Long cafeId);

    List<Review> findByMenuItem_MenuId(Long menuId);

    List<Review> findByUser_UserId(Long userId);

    List<Review> findByEmployee_EmployeeId(Long employeeId);

    long countByUser_UserId(Long userId);
}