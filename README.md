# CRM Online System 

> **Hệ thống Quản trị Quan hệ Khách hàng (CRM) phân cấp quy mô tập đoàn, tối ưu hóa luồng dữ liệu từ tiếp cận (Lead) đến chốt thương vụ (Opportunity).**

---

## 1. Giới thiệu dự án
Dự án tập trung vào việc xây dựng một hệ thống CRM mạnh mẽ cho doanh nghiệp đa chi nhánh. Hệ thống giải quyết các bài toán về:
- **Điều phối tự động:** Tự động gán khách hàng cho chi nhánh dựa trên vùng địa lý.
- **Quản lý bán hàng chuyên nghiệp:** Theo dõi chặt chẽ Pipeline, sản phẩm, thuế và chiết khấu.
- **Nâng suất nhân sự:** Giao việc và truy vết nhật ký tương tác thực tế (Traceability).

## 2. Các Tính năng Cốt lõi

### Quản trị Tổ chức & Phân quyền (RBAC)
- **Cơ cấu đa tầng:** Quản lý theo Chi nhánh (Branch) -> Phòng ban (Team) -> Nhân viên (User).
- **Phân quyền chức năng:** Kiểm soát chi tiết hành động qua `permission_key` (Xem, Thêm, Sửa, Xóa).
- **Bảo mật dữ liệu:** Nhân viên chỉ được tiếp cận dữ liệu khách hàng trong phạm vi chi nhánh hoặc nhóm của mình.

### Quản lý Khách hàng Tiềm năng (Lead)
- **Territory Management:** Sử dụng bảng `branch_provinces` để tự động điều phối Lead về chi nhánh quản lý ngay khi khách hàng để lại thông tin.
- **Communication Details:** Lưu trữ đa kênh liên lạc (Zalo, Skype, Facebook, Email, Phone) một cách linh hoạt.

### Quản lý Thương vụ & Bán hàng (Opportunity)
- **Sales Pipeline:** Quản lý cơ hội qua các giai đoạn (`stages`) với xác suất thành công (`probability`) và lý do thất bại (`lost_reasons`).
- **Snapshot Pricing:** Lưu trữ tên sản phẩm, đơn giá, thuế VAT và chiết khấu tại thời điểm bán để bảo toàn dữ liệu lịch sử tài chính.

### Vận hành & Năng suất (Tasks & Activities)
- **Task Scheduling:** Giao việc có ngày bắt đầu, kết thúc và mức độ ưu tiên (`priority`).
- **Activity Logging:** Nhật ký tương tác thực tế (Call, Meeting, Note) liên kết trực tiếp với Task qua `task_id` để đo lường hiệu suất thực tế.

## 3. Kiến trúc Cơ sở dữ liệu (Database Schema)

Hệ thống sử dụng MySQL với cấu trúc chuẩn hóa cao (3NF) kết hợp Phi chuẩn hóa (Denormalization) có kiểm soát để tối ưu hiệu suất báo cáo.

### Các Module chính:
1. **Module System:** `users`, `roles`, `permissions`, `branches`, `teams`, `system_settings`.
2. **Module CRM:** `leads`, `customers`, `contacts`, `communication_details`, `provinces`.
3. **Module Sales:** `products`, `uoms`, `opportunities`, `opportunity_product`, `opportunity_stages`.
4. **Module Operations:** `tasks`, `activities`, `customer_feedback`.

## 4. Điểm nhấn Kỹ thuật (Key Technical Points)
- **Traceability:** Khả năng truy vết từ một Activity (Hành động thực tế) ngược về Task (Mục tiêu ban đầu) giúp quản lý đánh giá đúng nỗ lực của nhân viên.
- **Data Integrity:** Sử dụng Foreign Keys và Triggers để đảm bảo tính toàn vẹn dữ liệu giữa các chi nhánh.
- **Polymorphic Relationship:** Bảng `communication_details` cho phép gán thông tin liên lạc cho nhiều loại thực thể (Lead, Customer, Contact) chỉ với một cấu trúc bảng duy nhất.

