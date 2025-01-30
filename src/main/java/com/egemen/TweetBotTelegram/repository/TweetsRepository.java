package com.egemen.TweetBotTelegram.repository;

import com.egemen.TweetBotTelegram.entity.Tweets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetsRepository extends JpaRepository<Tweets, Long> {
}
