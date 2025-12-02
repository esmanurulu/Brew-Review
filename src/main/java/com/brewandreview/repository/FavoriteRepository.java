package com.brewandreview.repository;

import com.brewandreview.model.Favorite;
import com.brewandreview.model.Cafe;
import com.brewandreview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser_UserId(Long userId);

    boolean existsByUserAndCafe(User user, Cafe cafe);

    void deleteByUserAndCafe(User user, Cafe cafe);
}