package com.project.demo.controller;

import com.project.demo.config.RabbitMQConfig;
import com.project.demo.dto.FlashSaleMessage;
import com.project.demo.entity.Campaign;
import com.project.demo.entity.Ticket;
import com.project.demo.exception.RateLimitException;
import com.project.demo.service.CampaignService;
import com.project.demo.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;
    private final RateLimitingService rateLimitingService;
    private final RabbitTemplate rabbitTemplate;

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
        // Chống Bot spam liên tục
        Bucket bucket = this.rateLimitingService.resolveBucket(userId);
        if (!bucket.tryConsume(1)) {
            throw new RateLimitException("Bạn thao tác quá nhanh, vui lòng chờ!");
        }

        // ném yc vào hàng đợi
        FlashSaleMessage flashSaleMessage = new FlashSaleMessage(id, userId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.FLASH_SALE_QUEUE, flashSaleMessage);

        return ResponseEntity.accepted().body("Yêu cầu của bạn đang được xử lý, vui lòng chờ,...");
    }
}
