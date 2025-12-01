package com.brewandreview.repository;

import com.brewandreview.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByUser_UserId(Long userId);

    List<Visit> findByCafe_CafeId(Long cafeId);

    // İstatistikler ve Kontrol İçin
    long countByUser_UserId(Long userId);

    boolean existsByUser_UserIdAndCafe_CafeId(Long userId, Long cafeId);
}