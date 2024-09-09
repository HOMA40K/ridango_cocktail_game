package com.ridango.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerScoreRepository extends JpaRepository<PlayerScore, Long> {
}
