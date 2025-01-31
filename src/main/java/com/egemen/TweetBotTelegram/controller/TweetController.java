package com.egemen.TweetBotTelegram.controller;

import com.egemen.TweetBotTelegram.entity.Tweets;
import com.egemen.TweetBotTelegram.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tweets")
public class TweetController {

    @Autowired
    private TweetService tweetService;

    @PostMapping("/generate")
    public ResponseEntity<List<Tweets>> generateTweets() {
        List<Tweets> generatedTweets = tweetService.generateTweetsForUnprocessedNews();
        return ResponseEntity.status(HttpStatus.CREATED).body(generatedTweets);
    }
}