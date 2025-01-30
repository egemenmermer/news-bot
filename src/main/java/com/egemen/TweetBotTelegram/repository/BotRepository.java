package com.egemen.TweetBotTelegram.repository;

import com.egemen.TweetBotTelegram.entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotRepository extends JpaRepository<Bot, Long> {

    Optional<Bot> findById(Long botId);
}
