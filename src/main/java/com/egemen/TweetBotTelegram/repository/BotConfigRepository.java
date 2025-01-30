package com.egemen.TweetBotTelegram.repository;

import com.egemen.TweetBotTelegram.entity.BotConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotConfigRepository extends JpaRepository<BotConfig, Long> {
}
