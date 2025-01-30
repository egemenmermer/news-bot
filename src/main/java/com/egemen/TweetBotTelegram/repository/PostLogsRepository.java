package com.egemen.TweetBotTelegram.repository;

import com.egemen.TweetBotTelegram.entity.PostLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLogsRepository extends JpaRepository<PostLogs, Long> {
}
