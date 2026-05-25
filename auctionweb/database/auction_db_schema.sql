-- ============================================================
--  AUCTION WEB - DATABASE SCHEMA
--  Nhóm 4 | Lập trình nâng cao
--  Tạo file này bằng cách: File > Run SQL Script trong MySQL Workbench
-- ============================================================

-- Tạo database nếu chưa có
CREATE DATABASE IF NOT EXISTS auction_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE auction_db;

-- ============================================================
-- BẢNG 1: users
-- Lưu thông tin tất cả người dùng (Admin, Seller, Bidder)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    fullname    VARCHAR(100)    NOT NULL,
    email       VARCHAR(150)    NOT NULL UNIQUE,
    username    VARCHAR(50)     NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,           -- Sẽ lưu dạng BCrypt hash
    role        ENUM('ADMIN', 'SELLER', 'BIDDER')
                                NOT NULL DEFAULT 'BIDDER',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- BẢNG 2: items
-- Lưu thông tin sản phẩm đấu giá (do Seller đăng)
-- ============================================================
CREATE TABLE IF NOT EXISTS items (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    name            VARCHAR(200)    NOT NULL,
    description     TEXT,
    item_type       ENUM('ELECTRONICS', 'ART', 'VEHICLE', 'OTHER')
                                    NOT NULL DEFAULT 'OTHER',
    start_price     DECIMAL(15, 2)  NOT NULL,
    current_price   DECIMAL(15, 2)  NOT NULL,
    start_time      DATETIME        NOT NULL,
    end_time        DATETIME        NOT NULL,
    status          ENUM('OPEN', 'RUNNING', 'FINISHED', 'PAID', 'CANCELED')
                                    NOT NULL DEFAULT 'OPEN',
    seller_id       BIGINT          NOT NULL,       -- FK trỏ về users.id
    winner_id       BIGINT          NULL,           -- FK trỏ về users.id (người thắng)
    image_url       VARCHAR(500)    NULL,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_item_seller  FOREIGN KEY (seller_id) REFERENCES users(id),
    CONSTRAINT fk_item_winner  FOREIGN KEY (winner_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- BẢNG 3: bid_transactions
-- Lưu lịch sử mỗi lần đặt giá (BidTransaction)
-- ============================================================
CREATE TABLE IF NOT EXISTS bid_transactions (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    item_id     BIGINT          NOT NULL,           -- FK trỏ về items.id
    user_id     BIGINT          NOT NULL,           -- FK trỏ về users.id (người bid)
    bid_amount  DECIMAL(15, 2)  NOT NULL,
    bid_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_auto     BOOLEAN         NOT NULL DEFAULT FALSE,  -- TRUE nếu là auto-bid
    PRIMARY KEY (id),
    CONSTRAINT fk_bid_item  FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_bid_user  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- BẢNG 4: auto_bids
-- Cấu hình đấu giá tự động (tính năng nâng cao)
-- ============================================================
CREATE TABLE IF NOT EXISTS auto_bids (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    item_id     BIGINT          NOT NULL,
    user_id     BIGINT          NOT NULL,
    max_bid     DECIMAL(15, 2)  NOT NULL,           -- Giá tối đa user chấp nhận
    increment   DECIMAL(15, 2)  NOT NULL DEFAULT 10000, -- Bước giá tăng mỗi lần
    is_active   BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_autobid_item  FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_autobid_user  FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uq_autobid (item_id, user_id)        -- Mỗi user chỉ đặt 1 auto-bid / sản phẩm
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- DỮ LIỆU MẪU (Sample Data) – Dùng để test
-- ============================================================

-- Tài khoản Admin (password: Admin@123 - đã hash BCrypt)
INSERT INTO users (fullname, email, username, password, role) VALUES
('Administrator', 'admin@auctionweb.com', 'admin',
 '$2a$10$Ov5xX7VNKLSS7ZjNpPSmQeJPVHoguqm0iSTbXxKzIfp2fS9L6xLAi', 'ADMIN');

-- Tài khoản Seller mẫu (password: Seller@123)
INSERT INTO users (fullname, email, username, password, role) VALUES
('Nguyen Van An', 'seller1@gmail.com', 'seller1',
 '$2a$10$Ov5xX7VNKLSS7ZjNpPSmQeJPVHoguqm0iSTbXxKzIfp2fS9L6xLAi', 'SELLER');

-- Tài khoản Bidder mẫu (password: Bidder@123)
INSERT INTO users (fullname, email, username, password, role) VALUES
('Tran Thi Bich', 'bidder1@gmail.com', 'bidder1',
 '$2a$10$Ov5xX7VNKLSS7ZjNpPSmQeJPVHoguqm0iSTbXxKzIfp2fS9L6xLAi', 'BIDDER'),
('Le Van Cuong', 'bidder2@gmail.com', 'bidder2',
 '$2a$10$Ov5xX7VNKLSS7ZjNpPSmQeJPVHoguqm0iSTbXxKzIfp2fS9L6xLAi', 'BIDDER');

-- Sản phẩm mẫu đang đấu giá (seller_id = 2 là seller1)
INSERT INTO items (name, description, item_type, start_price, current_price, start_time, end_time, status, seller_id) VALUES
('iPhone 15 Pro Max 256GB',
 'Máy mới 100%, chưa active, full box, màu Titan Đen.',
 'ELECTRONICS', 25000000, 25000000,
 NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), 'RUNNING', 2),

('Tranh Sơn Dầu – Hồ Gươm',
 'Tranh vẽ tay bởi họa sĩ Nguyễn Minh, kích thước 80x60cm.',
 'ART', 5000000, 5000000,
 NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), 'RUNNING', 2),

('Honda Wave Alpha 2023',
 'Xe còn mới 95%, ít đi, bstp 59-X1 1234.',
 'VEHICLE', 15000000, 15000000,
 DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 4 DAY), 'OPEN', 2);

-- ============================================================
-- INDEX để tăng hiệu năng truy vấn
-- ============================================================
CREATE INDEX idx_items_status   ON items(status);
CREATE INDEX idx_items_end_time ON items(end_time);
CREATE INDEX idx_bid_item_id    ON bid_transactions(item_id);
CREATE INDEX idx_bid_user_id    ON bid_transactions(user_id);
CREATE INDEX idx_bid_time       ON bid_transactions(bid_time);

-- ============================================================
-- KIỂM TRA KẾT QUẢ
-- ============================================================
SELECT 'Database created successfully!' AS result;
SELECT table_name FROM information_schema.tables WHERE table_schema = 'auction_db';
