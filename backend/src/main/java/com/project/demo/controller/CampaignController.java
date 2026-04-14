package com.project.demo.controller;

import com.project.demo.entity.Campaign;
import com.project.demo.entity.Ticket;
import com.project.demo.exception.RateLimitException;
import com.project.demo.service.CampaignService;
import com.project.demo.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;
    private final RateLimitingService rateLimitingService;

    @PostMapping
    public ResponseEntity<Campaign> create(@RequestBody Campaign campaign) {
        return ResponseEntity.ok(campaignService.createCampaign(campaign));
    }

    @GetMapping
    public ResponseEntity<List<Campaign>> getAll() {
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getById(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaign(id));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinCampaign(
            @PathVariable Long id,
            @RequestParam Long userId) { // Dùng RequestParam tạm để giả lập User ID
        // lấy bucket của User này
        Bucket bucket = this.rateLimitingService.resolveBucket(userId);

        // kiểm tra xem có lấy đc token vé không
        if (bucket.tryConsume(1)) {
            // Còn vé -> Cho phép gọi xuống Database giật quà
            Ticket ticket = campaignService.joinFlashSale(id, userId);
            return ResponseEntity.ok(ticket);
        } else {
            // Hết vé -> Ném lỗi 429 ngay lập tức, không cho đi tới Database
            throw new RateLimitException("Bạn thao tác quá nhanh, vui lòng chờ 10 giây!");
        }
    }
}
