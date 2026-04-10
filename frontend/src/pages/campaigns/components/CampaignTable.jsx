import React from "react";
import Button from "../../../components/ui/Button";
import { getCampaignStatus } from "../../../utils/formatters";

const CampaignTable = ({ filteredCampaigns, isLoading, onOpenEdit, onDelete }) => {
  return (
    <section className="lg:col-span-2 bg-surface-container-low rounded-2xl overflow-hidden shadow-sm border border-outline-variant/10 flex flex-col h-full">
      <div className="p-6 border-b border-surface-variant/30 flex justify-between items-center">
        <h2 className="text-xl font-bold text-on-surface">Danh sách chi tiết</h2>
      </div>
      <div className="overflow-x-auto flex-1">
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
                <td colSpan="4" className="p-10 text-center">Đang tải...</td>
              </tr>
            ) : filteredCampaigns.length === 0 ? (
              <tr>
                <td colSpan="4" className="p-10 text-center text-on-surface-variant">
                  Chưa có chiến dịch nào.
                </td>
              </tr>
            ) : (
              filteredCampaigns.map((item) => {
                const status = getCampaignStatus(item.startDate, item.endDate);
                return (
                  <tr key={item.id} className="hover:bg-surface-container-highest/30 transition-colors group">
                    <td className="px-6 py-5 font-bold text-on-surface">{item.name}</td>
                    <td className="px-6 py-5 text-sm text-on-surface-variant">
                      {item.startDate ? new Date(item.startDate).toLocaleDateString("vi-VN") : "---"}
                      {" - "}
                      {item.endDate ? new Date(item.endDate).toLocaleDateString("vi-VN") : "---"}
                    </td>
                    <td className="px-6 py-5">
                      <span className={`inline-flex items-center gap-2 px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider ${status.color}`}>
                        <span className={`w-1.5 h-1.5 rounded-full ${status.dot}`}></span> {status.label}
                      </span>
                    </td>
                    <td className="px-6 py-5">
                      <div className="flex justify-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                        <Button variant="iconOnly" className="hover:bg-primary/10 text-primary" onClick={() => onOpenEdit(item)} title="Chỉnh sửa" icon="edit" />
                        <Button variant="iconOnly" className="hover:bg-error/10 text-error" onClick={() => onDelete(item.id, item.name)} title="Xóa" icon="delete" />
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
  );
};

export default CampaignTable;