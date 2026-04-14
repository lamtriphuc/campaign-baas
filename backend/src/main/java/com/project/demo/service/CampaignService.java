package com.project.demo.service;

import com.project.demo.entity.Campaign;
import com.project.demo.entity.Ticket;
import com.project.demo.exception.FlashSaleException;
import com.project.demo.exception.NotFoundException;
import com.project.demo.repository.CampaignRepository;
import com.project.demo.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final TicketRepository ticketRepository;

    public Campaign createCampaign(Campaign campaign) {
        campaign.setAvailableItems(campaign.getTotalItems());
        campaign.setStatus("DRAFT");
        return campaignRepository.save(campaign);
    }

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public Campaign getCampaign(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chiến dịch!"));
    }

    @Transactional
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class, // Chỉ thử lại nếu gặp đúng lỗi này
            maxAttempts = 3,
            backoff = @Backoff(delay = 100) // Mỗi lần cách nhau 100 mili-giây
    )
    public Ticket joinFlashSale(Long campaignId, Long userId) {
        // 1. Tìm chiến dịch
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new NotFoundException("Chiến dịch ID " + campaignId + " không tồn tại!"));

        // 2. Kiểm tra trạng thái và số lượng
        if (!"ACTIVE".equals(campaign.getStatus())) {
            throw new FlashSaleException("Chiến dịch chưa bắt đầu hoặc đã kết thúc!");
        }

        if (campaign.getAvailableItems() <= 0) {
            throw new FlashSaleException("Rất tiếc, đã hết quà!");
        }

        // Trừ quà
        campaign.setAvailableItems(campaign.getAvailableItems() - 1);
        campaignRepository.save(campaign);

        Ticket ticket = Ticket.builder()
                .campaign(campaign)
                .userId(userId)
                .build();
        return ticketRepository.save(ticket);
    }
}
