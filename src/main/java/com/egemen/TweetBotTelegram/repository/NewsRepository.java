package com.egemen.TweetBotTelegram.repository;

import com.egemen.TweetBotTelegram.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    boolean existsByTitleAndDescription(String title, String description);
    List<News> findByProcessedFalseAndPostedFalse();
    List<News> findByProcessedTrueAndPostedFalse();
}
