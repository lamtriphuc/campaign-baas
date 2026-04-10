package com.project.demo.controller;

import com.project.demo.entity.Campaign;
import com.project.demo.entity.Ticket;
import com.project.demo.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {
    private final CampaignService campaignService;

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
        Ticket ticket = campaignService.joinFlashSale(id, userId);
        return ResponseEntity.ok(ticket);
    }
}
