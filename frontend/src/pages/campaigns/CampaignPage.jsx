import React, { useState, useEffect } from "react";
import axios from "axios";

const CampaignPage = () => {
  const [campaigns, setCampaigns] = useState([]);
  const [leads, setLeads] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // State cho bộ lọc
  const [filters, setFilters] = useState({
    keyword: "",
    status: "",
    fromDate: "",
    toDate: "",
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [campRes, leadRes] = await Promise.all([
        axios.get("http://localhost:8080/api/v1/campaigns"),
        axios.get("http://localhost:8080/api/v1/leads"),
      ]);
      setCampaigns(campRes.data);
      setLeads(leadRes.data);
    } catch (error) {
      console.error("Lỗi khi tải dữ liệu:", error);
    } finally {
      setIsLoading(false);
    }
  };

  // Logic tính toán trạng thái chiến dịch
  const getCampaignStatus = (start, end) => {
    const now = new Date();
    const startDate = new Date(start);
    const endDate = new Date(end);

    if (now < startDate) {
      return {
        label: "Sắp tới",
        color: "bg-primary-fixed text-on-primary-fixed-variant",
        dot: "bg-primary",
      };
    } else if (now > endDate) {
      return {
        label: "Đã kết thúc",
        color: "bg-surface-container-highest text-on-surface-variant",
        dot: "bg-outline",
      };
    } else {
      return {
        label: "Đang diễn ra",
        color: "bg-secondary-container/15 text-secondary",
        dot: "bg-secondary",
      };
    }
  };

  // === THUẬT TOÁN TÍNH XU HƯỚNG LEAD CỦA CHIẾN DỊCH ===
  // Chỉ tính những Lead có campaignId (không tính Lead tự nhiên)
  const getMonthlyTrend = () => {
    const months = [
      "Th. 01",
      "Th. 02",
      "Th. 03",
      "Th. 04",
      "Th. 05",
      "Th. 06",
      "Th. 07",
      "Th. 08",
      "Th. 09",
      "Th. 10",
      "Th. 11",
      "Th. 12",
    ];
    const currentMonth = new Date().getMonth();
    const last5Months = [];

    for (let i = 4; i >= 0; i--) {
      const monthIdx = (currentMonth - i + 12) % 12;
      const count = leads.filter(
        (lead) =>
          lead.campaignId !== null && // Phải thuộc một chiến dịch nào đó
          new Date(lead.createdAt).getMonth() === monthIdx,
      ).length;
      last5Months.push({ name: months[monthIdx], count });
    }

    const maxLeads = Math.max(...last5Months.map((m) => m.count), 1);
    return last5Months.map((m) => ({
      ...m,
      percent: (m.count / maxLeads) * 100,
    }));
  };

  // Xử lý lọc dữ liệu chiến dịch hiển thị trong bảng
  const filteredCampaigns = campaigns.filter((c) => {
    const status = getCampaignStatus(c.startDate, c.endDate).label;
    const matchKeyword = c.name
      .toLowerCase()
      .includes(filters.keyword.toLowerCase());
    const matchStatus = filters.status === "" || status === filters.status;
    const matchDate =
      (!filters.fromDate ||
        new Date(c.startDate) >= new Date(filters.fromDate)) &&
      (!filters.toDate || new Date(c.endDate) <= new Date(filters.toDate));
    return matchKeyword && matchStatus && matchDate;
  });

  // === LOGIC TÍNH TOÁN STATS QUAN TRỌNG ===

  // 1. Chỉ lấy những Leads có gắn campaignId
  const leadsFromCampaigns = leads.filter((lead) => lead.campaignId !== null);

  // 2. Tổng doanh thu dự kiến CHỈ từ các chiến dịch
  const totalCampaignRevenue = leadsFromCampaigns.reduce(
    (sum, lead) => sum + (lead.expectedRevenue || 0),
    0,
  );

  return (
    <div className="space-y-8">
      {/* Header Section */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 className="text-[2.75rem] font-bold text-primary leading-tight tracking-tight">
            Chiến dịch Marketing
          </h1>
          <p className="text-on-surface-variant">
            Theo dõi hiệu quả chuyển đổi từ các chương trình quảng bá.
          </p>
        </div>
        <button className="flex items-center gap-2 px-6 py-2.5 bg-gradient-to-br from-primary to-primary-container text-white rounded-lg font-bold shadow-lg active:scale-95 transition-all">
          <span className="material-symbols-outlined">add</span> Thêm chiến dịch
        </button>
      </div>

      {/* Filter Section */}
      <div className="bg-surface-container-low p-6 rounded-xl grid grid-cols-1 md:grid-cols-4 gap-4 shadow-sm border border-outline-variant/10">
        <input
          type="text"
          placeholder="Tìm tên chiến dịch..."
          className="rounded-lg border-outline-variant text-sm"
          value={filters.keyword}
          onChange={(e) => setFilters({ ...filters, keyword: e.target.value })}
        />
        <select
          className="rounded-lg border-outline-variant text-sm"
          value={filters.status}
          onChange={(e) => setFilters({ ...filters, status: e.target.value })}
        >
          <option value="">Tất cả trạng thái</option>
          <option value="Sắp tới">Sắp tới</option>
          <option value="Đang diễn ra">Đang diễn ra</option>
          <option value="Đã kết thúc">Đã kết thúc</option>
        </select>
        <input
          type="date"
          className="rounded-lg border-outline-variant text-sm"
          onChange={(e) => setFilters({ ...filters, fromDate: e.target.value })}
        />
        <input
          type="date"
          className="rounded-lg border-outline-variant text-sm"
          onChange={(e) => setFilters({ ...filters, toDate: e.target.value })}
        />
      </div>

      {/* Stats Grid - ĐÃ CẬP NHẬT LOGIC CHECK CAMPAIGN_ID */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <StatCard
          title="Tổng chiến dịch"
          value={campaigns.length}
          icon="campaign"
          color="border-primary"
        />
        <StatCard
          title="Đang diễn ra"
          value={
            campaigns.filter(
              (c) =>
                getCampaignStatus(c.startDate, c.endDate).label ===
                "Đang diễn ra",
            ).length
          }
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

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Table Section */}
        <section className="lg:col-span-2 bg-surface-container-low rounded-2xl overflow-hidden shadow-sm border border-outline-variant/10">
          <div className="p-6 border-b border-surface-variant/30 flex justify-between items-center">
            <h2 className="text-xl font-bold text-on-surface">
              Danh sách chi tiết
            </h2>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-surface-container-high/50 text-[0.75rem] font-bold uppercase text-on-surface-variant">
                <tr>
                  <th className="px-6 py-4">Chiến dịch</th>
                  <th className="px-6 py-4">Thời gian</th>
                  <th className="px-6 py-4">Trạng thái</th>
                  <th className="px-6 py-4 text-center">Hành động</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-surface-variant/30">
                {isLoading ? (
                  <tr>
                    <td colSpan="4" className="p-10 text-center">
                      Đang tải...
                    </td>
                  </tr>
                ) : (
                  filteredCampaigns.map((item) => {
                    const status = getCampaignStatus(
                      item.startDate,
                      item.endDate,
                    );
                    return (
                      <tr
                        key={item.id}
                        className="hover:bg-surface-container-highest/30 transition-colors group"
                      >
                        <td className="px-6 py-5 font-bold text-on-surface">
                          {item.name}
                        </td>
                        <td className="px-6 py-5 text-sm text-on-surface-variant">
                          {new Date(item.startDate).toLocaleDateString("vi-VN")}{" "}
                          - {new Date(item.endDate).toLocaleDateString("vi-VN")}
                        </td>
                        <td className="px-6 py-5">
                          <span
                            className={`inline-flex items-center gap-2 px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${status.color}`}
                          >
                            <span
                              className={`w-1.5 h-1.5 rounded-full ${status.dot}`}
                            ></span>{" "}
                            {status.label}
                          </span>
                        </td>
                        <td className="px-6 py-5">
                          <div className="flex justify-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                            <button className="p-1.5 hover:bg-primary/10 text-primary rounded-lg">
                              <span className="material-symbols-outlined">
                                edit
                              </span>
                            </button>
                            <button className="p-1.5 hover:bg-error/10 text-error rounded-lg">
                              <span className="material-symbols-outlined">
                                delete
                              </span>
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </section>

        {/* Chart Section - CẬP NHẬT LOGIC CHỈ TÍNH LEAD CÓ CHIẾN DỊCH */}
        <div className="bg-surface-container-lowest p-8 rounded-2xl shadow-sm border border-outline-variant/10">
          <h2 className="text-lg font-bold mb-8 text-on-surface">
            Xu hướng Leads Chiến dịch
          </h2>
          <div className="h-64 flex items-end justify-between gap-2">
            {getMonthlyTrend().map((m, i) => (
              <div
                key={i}
                className="flex-1 flex flex-col items-center gap-3 group"
              >
                <div className="relative w-full flex flex-col items-center">
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
      </div>
    </div>
  );
};

const StatCard = ({ title, value, icon, color, sub }) => (
  <div
    className={`bg-surface-container-lowest p-6 rounded-xl border-l-4 ${color} shadow-sm`}
  >
    <p className="text-[0.7rem] font-bold text-on-surface-variant uppercase tracking-widest mb-2">
      {title}
    </p>
    <div className="flex items-center justify-between">
      <div className="flex items-baseline gap-1">
        <h3 className="text-2xl font-bold text-on-surface">{value}</h3>
        {sub && <span className="text-[10px] font-bold opacity-60">{sub}</span>}
      </div>
      <span className="material-symbols-outlined opacity-20 text-3xl">
        {icon}
      </span>
    </div>
  </div>
);

export default CampaignPage;
