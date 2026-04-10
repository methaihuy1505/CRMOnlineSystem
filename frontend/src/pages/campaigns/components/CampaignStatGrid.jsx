import React from "react";
import StatCard from "../../../components/ui/StatCard";
import { getCampaignStatus } from "../../../utils/formatters";

const CampaignStatGrid = ({ campaigns, leadsFromCampaigns }) => {
  // Tính tổng tiền
  const totalCampaignRevenue = leadsFromCampaigns.reduce(
    (sum, lead) => sum + (lead.expectedRevenue || 0),
    0,
  );

  // Tính số lượng đang diễn ra
  const ongoingCount = campaigns.filter(
    (c) => getCampaignStatus(c.startDate, c.endDate).label === "Đang diễn ra",
  ).length;

  return (
    <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
      <StatCard
        title="Tổng chiến dịch"
        value={campaigns.length}
        icon="campaign"
        color="border-primary"
      />
      <StatCard
        title="Đang diễn ra"
        value={ongoingCount}
        icon="bolt"
        color="border-secondary"
      />
      <StatCard
        title="Leads từ Chiến dịch"
        value={leadsFromCampaigns.length}
        icon="leaderboard"
        color="border-on-tertiary-container"
      />
      <StatCard
        title="Dự thu Chiến dịch"
        value={new Intl.NumberFormat("vi-VN").format(totalCampaignRevenue)}
        sub="VND"
        icon="payments"
        color="border-emerald-500"
      />
    </div>
  );
};

export default CampaignStatGrid;
