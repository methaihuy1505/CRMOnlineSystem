package com.vti.crm.service;

import com.vti.crm.dto.request.CampaignRequest;
import com.vti.crm.dto.response.CampaignResponse;
import com.vti.crm.entity.Campaign;
import com.vti.crm.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public List<CampaignResponse> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CampaignResponse getCampaignById(Integer id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chiến dịch với ID: " + id));
        return mapToResponse(campaign);
    }

    @Transactional
    public CampaignResponse createCampaign(CampaignRequest request) {
        Campaign campaign = new Campaign();
        campaign.setName(request.getName());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());

        Campaign savedCampaign = campaignRepository.save(campaign);
        return mapToResponse(savedCampaign);
    }

    @Transactional
    public CampaignResponse updateCampaign(Integer id, CampaignRequest request) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chiến dịch với ID: " + id));

        campaign.setName(request.getName());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());

        Campaign updatedCampaign = campaignRepository.save(campaign);
        return mapToResponse(updatedCampaign);
    }

    @Transactional
    public void deleteCampaign(Integer id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chiến dịch với ID: " + id));

        // Thực hiện Xóa mềm (Soft Delete)
        campaign.setDeletedAt(LocalDateTime.now());
        campaignRepository.save(campaign);
    }

    private CampaignResponse mapToResponse(Campaign campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .build();
    }
}