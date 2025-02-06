package com.egemen.TweetBotTelegram.repository;

import com.egemen.TweetBotTelegram.entity.Tweets;
import com.egemen.TweetBotTelegram.enums.TweetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.param.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TweetsRepository extends JpaRepository<Tweets, Long> {
    List<Tweets> findByStatus(TweetStatus status);
    List<Tweets> findByScheduledAtBeforeAndStatus(Timestamp time, TweetStatus status);
    @Modifying
    @Query("UPDATE Tweets t SET t.status = :status WHERE t.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") TweetStatus status);
}
