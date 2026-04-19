package com.project.demo.service;

import com.project.demo.dto.FlashSaleMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleWorker {
    private final CampaignService campaignService;

    // Annotation này biến hàm này thành một người lắng nghe liên tục
    public void processFlashSaleQueue(FlashSaleMessage flashSaleMessage) {
        log.info(" Bắt đầu xử lý cho User ID: {} tại Campaign: {}", flashSaleMessage.getUserId(), flashSaleMessage.getCampaignId());

        try {
            campaignService.joinFlashSale(flashSaleMessage.getCampaignId(), flashSaleMessage.getUserId());
        } catch (Exception e) {
            log.error(" Xử lý thất bại cho User {}: {}", flashSaleMessage.getUserId(), e.getMessage());
        }
    }
}
