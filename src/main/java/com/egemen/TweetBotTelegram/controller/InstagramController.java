package com.egemen.TweetBotTelegram.controller;

import com.egemen.TweetBotTelegram.entity.InstagramPost;
import com.egemen.TweetBotTelegram.service.InstagramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instagram")
public class InstagramController {
    
    private final InstagramService instagramService;

    public InstagramController(InstagramService instagramService) {
        this.instagramService = instagramService;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<InstagramPost>> getAllPosts() {
        return ResponseEntity.ok(instagramService.getAllPosts());
    }

    @GetMapping("/posts/pending")
    public ResponseEntity<List<InstagramPost>> getPendingPosts() {
        return ResponseEntity.ok(instagramService.getPendingPosts());
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<InstagramPost> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(instagramService.getPost(id));
    }

    @PostMapping("/posts/{id}/publish")
    public ResponseEntity<InstagramPost> publishPost(@PathVariable Long id) {
        return ResponseEntity.ok(instagramService.publishPost(id));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        instagramService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}