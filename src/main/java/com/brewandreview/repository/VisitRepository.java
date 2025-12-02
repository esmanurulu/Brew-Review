package com.brewandreview.repository;

import com.brewandreview.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    // 1. Kullanıcının Ziyaret Geçmişi
    List<Visit> findByUser_UserId(Long userId);

    // 2. Bir Kafenin Ziyaretçileri
    List<Visit> findByCafe_CafeId(Long cafeId);

    // 3. İstatistikler ve Kontrol İçin
    long countByUser_UserId(Long userId);

    // Kullanıcının kafeye gidip gitmediğini kontrol et (Yorum yapabilmek için)
    boolean existsByUser_UserIdAndCafe_CafeId(Long userId, Long cafeId);

    // --- YENİ EKLENEN: YOĞUNLUK İSTATİSTİKLERİ ---

    // 4. En Yoğun Günü Bul (Pazartesi, Salı vb.)
    // (Hangi gün kaç ziyaret var sayar, en yükseği getirir)
    @Query(value = "SELECT DAYNAME(visit_date) as day, COUNT(*) as count " +
            "FROM Visit WHERE cafe_id = :cafeId " +
            "GROUP BY day ORDER BY count DESC LIMIT 1", nativeQuery = true)
    List<Object[]> findBusiestDay(@Param("cafeId") Long cafeId);

    // 5. En Yoğun Saati Bul (14:00, 18:00 vb.)
    // (Hangi saat aralığında kaç ziyaret var sayar, en yükseği getirir)
    @Query(value = "SELECT HOUR(visit_time) as hour, COUNT(*) as count " +
            "FROM Visit WHERE cafe_id = :cafeId " +
            "GROUP BY hour ORDER BY count DESC LIMIT 1", nativeQuery = true)
    List<Object[]> findBusiestHour(@Param("cafeId") Long cafeId);
}