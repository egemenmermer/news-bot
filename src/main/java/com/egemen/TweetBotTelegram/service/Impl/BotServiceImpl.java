package com.egemen.TweetBotTelegram.service.Impl;

import com.egemen.TweetBotTelegram.entity.Bot;
import com.egemen.TweetBotTelegram.repository.BotRepository;
import com.egemen.TweetBotTelegram.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BotServiceImpl implements BotService {

    @Autowired
    private BotRepository botRepository;

    @Override
    public Bot createBot(Bot bot) {
        return botRepository.save(bot);
    }
}