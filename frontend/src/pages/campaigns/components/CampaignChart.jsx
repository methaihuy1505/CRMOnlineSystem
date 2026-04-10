import React from "react";

const CampaignChart = ({ leads }) => {
  // Logic lấy 5 tháng gần nhất chuyển vào đây
  const getMonthlyTrend = () => {
    const months = [
      "Th. 01", "Th. 02", "Th. 03", "Th. 04", "Th. 05", "Th. 06",
      "Th. 07", "Th. 08", "Th. 09", "Th. 10", "Th. 11", "Th. 12",
    ];
    const currentMonth = new Date().getMonth();
    const last5Months = [];

    for (let i = 4; i >= 0; i--) {
      const monthIdx = (currentMonth - i + 12) % 12;
      const count = leads.filter(
        (lead) =>
          lead.campaignId !== null &&
          new Date(lead.createdAt).getMonth() === monthIdx
      ).length;
      last5Months.push({ name: months[monthIdx], count });
    }

    const maxLeads = Math.max(...last5Months.map((m) => m.count), 1);
    return last5Months.map((m) => ({
      ...m,
      percent: (m.count / maxLeads) * 100,
    }));
  };

  return (
    <div className="bg-surface-container-lowest p-8 rounded-2xl shadow-sm border border-outline-variant/10 flex flex-col h-full">
      <h2 className="text-lg font-bold mb-8 text-on-surface">Xu hướng Leads Chiến dịch</h2>
      <div className="flex-1 flex items-end justify-between gap-2 h-64">
        {getMonthlyTrend().map((m, i) => (
          <div key={i} className="flex-1 flex flex-col items-center gap-3 group h-full justify-end">
            <div className="relative w-full flex flex-col items-center h-full justify-end">
              <span className="absolute -top-6 text-[10px] font-bold opacity-0 group-hover:opacity-100 transition-opacity text-primary">
                {m.count}
              </span>
              <div
                className={`w-full max-w-[30px] rounded-t-lg transition-all ${i === 4 ? "bg-secondary shadow-lg" : "bg-primary-fixed"}`}
                style={{ height: `${Math.max(m.percent, 5)}%` }}
              ></div>
            </div>
            <span className="text-[10px] font-bold text-on-surface-variant uppercase">
              {m.name}
            </span>
          </div>
        ))}
      </div>
      <p className="text-[10px] text-center text-on-surface-variant mt-4 italic">
        * Dữ liệu tổng hợp từ các Lead có liên hệ tới chiến dịch
      </p>
    </div>
  );
};

export default CampaignChart;