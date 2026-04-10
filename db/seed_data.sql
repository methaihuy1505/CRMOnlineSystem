-- ==========================================================
-- 1. DỮ LIỆU NỀN (Hệ thống, Chi nhánh, Quyền)
-- ==========================================================
INSERT INTO `branches` (`id`, `name`, `address`) VALUES
(1, 'Trụ sở chính Hà Nội', 'Quận Cầu Giấy, Hà Nội'),
(2, 'Chi nhánh TP.HCM', 'Quận 1, TP.HCM'),
(3, 'Chi nhánh Đà Nẵng', 'Quận Hải Châu, Đà Nẵng'),
(4, 'Văn phòng Cần Thơ', 'Quận Ninh Kiều, Cần Thơ'),
(5, 'Văn phòng Hải Phòng', 'Quận Hồng Bàng, Hải Phòng');

INSERT INTO `roles` (`id`, `role_name`, `code`, `is_active`) VALUES
(1, 'Quản trị viên', 'ADMIN', 1),
(2, 'Trưởng phòng Kinh doanh', 'SALE_MANAGER', 1),
(3, 'Nhân viên Kinh doanh', 'SALE_REP', 1),
(4, 'Nhân viên Chăm sóc khách hàng', 'CS_STAFF', 1),
(5, 'Cộng tác viên', 'COLLABORATOR', 1);

INSERT INTO `users` (`id`, `username`, `password`, `full_name`, `email`, `role_id`, `branch_id`, `status`) VALUES
(1, 'admin_crm', 'pass_hash_1', 'Nguyễn Quản Trị', 'admin@crm.com', 1, 1, 'ACTIVE'),
(2, 'manager_hcm', 'pass_hash_2', 'Trần Trưởng Phòng', 'manager.hcm@crm.com', 2, 2, 'ACTIVE'),
(3, 'sale_tung', 'pass_hash_3', 'Lê Thanh Tùng', 'tung.le@crm.com', 3, 2, 'ACTIVE'),
(4, 'sale_hoa', 'pass_hash_4', 'Phạm Quỳnh Hoa', 'hoa.pham@crm.com', 3, 1, 'ACTIVE'),
(5, 'cs_minh', 'pass_hash_5', 'Vũ Đức Minh', 'minh.vu@crm.com', 4, 3, 'ACTIVE');

-- ==========================================================
-- 2. DỮ LIỆU ĐỐI TƯỢNG (Lead, Customer, Contact)
-- ==========================================================
INSERT INTO `sources` (`id`, `name`) VALUES (1, 'Facebook'), (2, 'Google Ads'), (3, 'Website'), (4, 'Giới thiệu'), (5, 'Triển lãm');
INSERT INTO `lead_status` (`id`, `name`) VALUES (1, 'Mới'), (2, 'Đang liên hệ'), (3, 'Tiềm năng'), (4, 'Đã chuyển đổi'), (5, 'Tạm dừng');
INSERT INTO `customer_status` (`id`, `name`) VALUES (1, 'Đang hoạt động'), (2, 'Tạm ngưng'), (3, 'Nợ xấu'), (4, 'VIP');

INSERT INTO `leads` (`id`, `full_name`, `company_name`, `phone`, `status_id`, `assigned_to`, `branch_id`) VALUES
(1, 'Nguyễn Văn A', 'Công ty Xây dựng A', '0912000001', 1, 3, 2),
(2, 'Trần Thị B', 'Nha khoa Thẩm mỹ B', '0912000002', 2, 3, 2),
(3, 'Lê Văn C', 'Cửa hàng Nội thất C', '0912000003', 3, 4, 1),
(4, 'Phạm Thị D', 'Hộ kinh doanh D', '0912000004', 1, 4, 1),
(5, 'Hoàng Văn E', 'Công ty Logisics E', '0912000005', 2, 5, 3);

INSERT INTO `customers` (`id`, `customer_code`, `name`, `status_id`, `branch_id`, `assigned_user_id`) VALUES
(1, 'KH001', 'Tập đoàn Công nghệ FPT', 1, 1, 4),
(2, 'KH002', 'Ngân hàng Vietcombank', 1, 2, 3),
(3, 'KH003', 'Công ty Bất động sản Vin', 4, 2, 3),
(4, 'KH004', 'Chuỗi Coffee Highlands', 1, 3, 5),
(5, 'KH005', 'Nhà máy May mặc 10', 2, 5, 1);

INSERT INTO `contacts` (`id`, `customer_id`, `first_name`, `last_name`, `job_title`, `is_primary`) VALUES
(1, 1, 'Nam', 'Hoàng', 'Giám đốc IT', 1),
(2, 2, 'Lan', 'Nguyễn', 'Trưởng phòng Thu mua', 1),
(3, 3, 'Thắng', 'Lê', 'Chuyên viên Pháp chế', 0),
(4, 4, 'Hương', 'Phạm', 'Quản lý vận hành', 1),
(5, 5, 'Dũng', 'Vũ', 'Kế toán trưởng', 1);

-- ==========================================================
-- 3. CHỨC NĂNG CHÍNH (Task, Task Note, Activities)
-- ==========================================================

-- Chèn TASKS (Công việc)
INSERT INTO `tasks` (`id`, `title`, `description`, `priority`, `status`, `assigned_to`, `relate_type`, `relate_id`, `created_by`) VALUES
(1, 'Gọi điện tư vấn Lead A', 'Tư vấn giải pháp CRM cho dự án xây dựng', 'HIGH', 'PENDING', 3, 'LEAD', 1, 1),
(2, 'Gửi hợp đồng cho VCB', 'Hợp đồng bảo trì hệ thống năm 2026', 'URGENT', 'PENDING', 3, 'CUSTOMER', 2, 3),
(3, 'Demo tính năng cho Vin', 'Họp online demo module Opportunity', 'MEDIUM', 'COMPLETED', 3, 'CUSTOMER', 3, 3),
(4, 'Xử lý khiếu nại Highlands', 'Khách báo lỗi đăng nhập tài khoản admin', 'HIGH', 'PENDING', 5, 'CUSTOMER', 4, 5),
(5, 'Kiểm tra thông tin Lead D', 'Xác thực mã số thuế và địa chỉ văn phòng', 'LOW', 'PENDING', 4, 'LEAD', 4, 1);

-- Chèn TASK_NOTES (Ghi chú chi tiết trong Task)
INSERT INTO `task_notes` (`task_id`, `user_id`, `content`) VALUES
(1, 3, 'Khách đang bận họp, hẹn gọi lại lúc 2h chiều nay.'),
(1, 3, 'Đã gọi lại, khách yêu cầu gửi thêm Profile năng lượng qua Zalo.'),
(2, 3, 'Đã gửi bản thảo hợp đồng lần 1, chờ bộ phận pháp chế check.'),
(3, 3, 'Demo thành công, khách rất ưng ý tính năng báo cáo Dashboard.'),
(4, 5, 'Đã reset password cho khách, đang theo dõi thêm.');

-- Chèn ACTIVITIES (Lịch sử tương tác/Hoạt động)
-- Activities gắn với Task (Activity ID 1, 2, 3) và Activities độc lập (ID 4, 5)
INSERT INTO `activities` (`task_id`, `parent_id`, `parent_type`, `contact_id`, `activity_type`, `subject`, `description`, `status`, `assigned_to`, `created_by`) VALUES
(1, 1, 'LEAD', NULL, 'CALL', 'Tư vấn lần 1', 'Đã trao đổi về nhu cầu quản lý nhân sự', 'HELD', 3, 3),
(2, 2, 'CUSTOMER', 2, 'EMAIL_QUOTE', 'Gửi báo giá VCB', 'Gửi kèm bảng chiết khấu 10% cho đối tác chiến lược', 'HELD', 3, 3),
(3, 3, 'CUSTOMER', 3, 'MEETING', 'Họp Demo Vin', 'Họp tại văn phòng VinHomes Central Park', 'HELD', 3, 3),
(NULL, 1, 'CUSTOMER', 1, 'CALL', 'Thăm hỏi định kỳ', 'Gọi điện hỏi thăm tình hình sử dụng sau 1 tháng', 'HELD', 4, 4),
(NULL, 5, 'LEAD', NULL, 'EMAIL_TRANSACTION', 'Gửi tài liệu giới thiệu', 'Gửi trọn bộ catalog sản phẩm 2026', 'PLANNED', 5, 5);