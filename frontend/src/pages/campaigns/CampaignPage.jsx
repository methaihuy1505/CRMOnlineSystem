import React, { useState, useEffect } from "react";
import axios from "axios";
import { getCampaignStatus } from "../../utils/formatters"; // Thêm import này để dùng trong bộ lọc

// Import các component con
import CampaignHeader from "./components/CampaignHeader";
import CampaignFilter from "./components/CampaignFilter";
import CampaignStatGrid from "./components/CampaignStatGrid";
import CampaignTable from "./components/CampaignTable";
import CampaignChart from "./components/CampaignChart";
import CampaignFormModal from "./CampaignFormModal";

const CampaignPage = () => {
  // === 1. STATE MANAGEMENT ===
  const [campaigns, setCampaigns] = useState([]);
  const [leads, setLeads] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // State Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCampaign, setEditingCampaign] = useState(null);

  // State Filter
  const [filters, setFilters] = useState({
    keyword: "",
    status: "",
    fromDate: "",
    toDate: "",
  });

  // === 2. API CALLS ===
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

  // === 3. HANDLERS ===
  const handleOpenAdd = () => {
    setEditingCampaign(null);
    setIsModalOpen(true);
  };

  const handleOpenEdit = (campaign) => {
    setEditingCampaign(campaign);
    setIsModalOpen(true);
  };

  const handleDeleteCampaign = async (id, name) => {
    if (window.confirm(`Bạn có chắc chắn muốn xóa chiến dịch "${name}"?`)) {
      try {
        await axios.delete(`http://localhost:8080/api/v1/campaigns/${id}`);
        fetchData();
      } catch (error) {
        alert(
          "Lỗi khi xóa chiến dịch: " +
            (error.response?.data?.message || error.message),
        );
      }
    }
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  // === 4. FILTER LOGIC ===
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

  const leadsFromCampaigns = leads.filter((lead) => lead.campaignId !== null);

  // === 5. RENDER ===
  return (
    <div className="space-y-8 relative">
      <CampaignHeader onOpenAdd={handleOpenAdd} />

      <CampaignFilter filters={filters} onFilterChange={handleFilterChange} />

      <CampaignStatGrid
        campaigns={campaigns}
        leadsFromCampaigns={leadsFromCampaigns}
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <CampaignTable
          filteredCampaigns={filteredCampaigns}
          isLoading={isLoading}
          onOpenEdit={handleOpenEdit}
          onDelete={handleDeleteCampaign}
        />
        <CampaignChart leads={leads} />
      </div>

      <CampaignFormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={fetchData}
        currentCampaign={editingCampaign}
      />
    </div>
  );
};

export default CampaignPage;
