package com.brewandreview.repository;

import com.brewandreview.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    // kullanıcının ziyaret geçmişi
    List<Visit> findByUser_UserId(Long userId);

    // bir kafenin ziyaretçileri
    List<Visit> findByCafe_CafeId(Long cafeId);

    // istatistikler ve kntrol
    long countByUser_UserId(Long userId);

    // kullanıcının kafeye gidip gitmediğinin kontrolunu yapıyoruz (degerlendirme yapabilme sartı)
    boolean existsByUser_UserIdAndCafe_CafeId(Long userId, Long cafeId);

    // en yogun gun
    @Query(value = "SELECT DAYNAME(visit_date) as day, COUNT(*) as count " +
            "FROM Visit WHERE cafe_id = :cafeId " +
            "GROUP BY day ORDER BY count DESC LIMIT 1", nativeQuery = true)
    List<Object[]> findBusiestDay(@Param("cafeId") Long cafeId);

    // en yogun saat
    @Query(value = "SELECT HOUR(visit_time) as hour, COUNT(*) as count " +
            "FROM Visit WHERE cafe_id = :cafeId " +
            "GROUP BY hour ORDER BY count DESC LIMIT 1", nativeQuery = true)
    List<Object[]> findBusiestHour(@Param("cafeId") Long cafeId);
}