# Ứng Dụng Đấu Giá Trực Tuyến (Auction Web)

## 1. Mô tả bài toán và phạm vi hệ thống
*   **Bài toán:** Xây dựng một nền tảng hỗ trợ các hoạt động đấu giá trực tuyến chuyên nghiệp. Hệ thống giải quyết bài toán giao dịch tài sản giữa người bán và người mua một cách minh bạch, cho phép đấu giá đồng thời (Concurrent Bidding), tự động trả giá (Auto-bidding / Proxy Bidding), và chống gian lận/bắn tỉa giờ chót (Anti-sniping).
*   **Phạm vi hệ thống:** Ứng dụng Desktop (Client-Server Architecture) cung cấp đầy đủ các nghiệp vụ quản lý phiên đấu giá, theo dõi giá và cập nhật theo thời gian thực (real-time). Hệ thống phục vụ 3 đối tượng người dùng chính:
    *   **Admin:** Quản trị viên quản lý toàn bộ phiên đấu giá và người dùng.
    *   **Seller:** Người bán đăng tải sản phẩm (Nghệ thuật - Art, Điện tử - Electronics, Phương tiện - Vehicle) và tạo phiên đấu giá.
    *   **Bidder:** Người mua tham gia theo dõi và trả giá (thủ công hoặc tự động).

## 2. Công nghệ sử dụng, môi trường chạy và yêu cầu cài đặt
*   **Công nghệ sử dụng:**
    *   **Backend:** Spring Boot 3 (Spring Web, Spring Security, Spring Data JPA, Spring WebSocket). Xử lý đa luồng (Async) và Optimistic Locking.
    *   **Frontend:** JavaFX (Controls, FXML) xây dựng giao diện Desktop.
    *   **Database:** MySQL (Cloud Railway) cho dữ liệu chính & H2 Database (Runtime/Testing).
    *   **Công cụ & Thư viện:** Java 17, Maven (Build Tool), Lombok (Giảm boilerplate code), JaCoCo (Code Coverage 100% Core API), JSON.
*   **Môi trường chạy:** Hỗ trợ đa nền tảng Windows / macOS / Linux.
*   **Yêu cầu cài đặt:**
    *   Java Development Kit (JDK) 17 trở lên.
    *   Apache Maven 3.x.
    *   Môi trường kết nối Internet ổn định (do ứng dụng liên kết trực tiếp tới Database trên Cloud Railway).

## 3. Cấu trúc thư mục hoặc các module chính
Dự án áp dụng mô hình kiến trúc MVC kết hợp, chia mã nguồn thành 2 module chính hoạt động song song trong cùng một tiến trình (Hybrid):
```text
auctionweb/
├── src/main/java/com/nhom4project/auctionweb/
│   ├── backend/        # Module Server (Chạy ngầm đa luồng)
│   │   ├── config/     # Cấu hình WebSocket, Security
│   │   ├── controller/ # REST API Endpoints (Admin, Auction, Auth, Item)
│   │   ├── model/      # Entities (User, Auction, Item, AutoBidConfig...)
│   │   ├── repository/ # Spring Data JPA Repositories truy vấn DB
│   │   └── service/    # Logic nghiệp vụ lõi (Xử lý Lock tranh chấp, Anti-sniping)
│   └── frontend/       # Module Client (Hiển thị giao diện JavaFX)
│       ├── app/        # Chứa MainLauncher khởi chạy Spring Boot & JavaFX
│       ├── controller/ # Xử lý sự kiện giao diện (Dashboard, Signin, Signup)
│       └── utils/      # Các hàm tiện ích giao diện
├── src/main/resources/ # Tài nguyên tĩnh
│   ├── application.properties # Cấu hình Server Port, Database Credentials
│   ├── fxml/           # Các file giao diện thiết kế bằng Scene Builder
│   └── style/          # Các file CSS tạo kiểu cho giao diện
└── pom.xml             # File cấu hình Maven quản lý dependencies và build plugins
```

## 4. Vị trí các file `.jar`
*   **Đường dẫn file build:** Sau khi biên dịch và đóng gói ứng dụng, file thực thi `.jar` sẽ được tạo ra tại thư mục `target/` của dự án.
*   **Tên file chính thức:** `auctionweb-0.0.1-SNAPSHOT.jar`
*   *(Bạn có thể tự tay tạo ra file này bằng cách gõ lệnh: `mvn clean package -DskipTests`)*

## 5. Hướng dẫn chạy Server/Client theo thứ tự cụ thể
Dự án được thiết kế đặc biệt thông qua lớp `MainLauncher` để khởi động cả **Backend** và **Frontend** cùng một lúc chỉ với **một thao tác chạy duy nhất**. MainLauncher sẽ cấp phát một luồng nền khởi động Spring Boot trước, rồi mới bật giao diện JavaFX.

**Cách 1: Chạy qua IDE (Khuyên dùng để đọc và dev code)**
1. Mở project `auctionweb` bằng IDE (IntelliJ IDEA, Eclipse, hoặc VS Code).
2. Đợi Maven tải xong toàn bộ thư viện.
3. Chạy file class chính tại: `src/main/java/com/nhom4project/auctionweb/frontend/app/MainLauncher.java`.

**Cách 2: Chạy thông qua Terminal / Command Prompt bằng Maven**
1. Mở Terminal tại thư mục `auctionweb` (nơi chứa file `pom.xml`).
2. Gõ lệnh: `mvn spring-boot:run`

**Cách 3: Chạy ứng dụng đã đóng gói (.jar) trên môi trường thực tế**
1. Mở Terminal tại thư mục `auctionweb`.
2. Đóng gói dự án: `mvn clean package -DskipTests`
3. Khởi chạy hệ thống: `java -jar target/auctionweb-0.0.1-SNAPSHOT.jar`

## 6. Danh sách chức năng đã hoàn thành
*   [x] **Authentication & Authorization:** Đăng ký, đăng nhập và phân quyền bảo mật 3 cấp (Admin, Seller, Bidder).
*   [x] **Quản lý sản phẩm đa hình:** Hỗ trợ các mặt hàng đặc thù kế thừa từ lớp cha Item (Art, Electronics, Vehicle).
*   [x] **Concurrent Bidding (Đấu giá đồng thời):** Ngăn chặn xung đột dữ liệu khi hàng nghìn người cùng trả giá bằng cơ chế Lock (`ReentrantLock`) kết hợp Database Optimistic Locking (`@Version`).
*   [x] **Anti-sniping (Chống bắn tỉa giờ chót):** Thuật toán tự động gia hạn thêm 60 giây nếu hệ thống ghi nhận có lượt đặt giá trong 30 giây cuối cùng của phiên.
*   [x] **Auto-bidding (Đấu giá tự động Proxy):** Cấu hình giá trần (`MaxBid`) và bước giá. Server tự chạy ngầm đa luồng (Async) cạnh tranh giá thay mặt người dùng.
*   [x] **Real-time Updates (Cập nhật thời gian thực):** Tích hợp công nghệ WebSocket đẩy dữ liệu cập nhật giá trực tiếp xuống màn hình Client ngay lập tức (Áp dụng Observer Pattern).
*   [x] **Theo dõi lịch sử:** Lưu trữ minh bạch toàn bộ các lượt đặt giá (`BidTransaction`) và kết quả chung cuộc (`AuctionHistory`).

## 7. Link báo cáo PDF và video demo
*   **Báo cáo PDF:** [Nhóm của bạn hãy chèn link file báo cáo thiết kế PDF tại đây]
*   **Video Demo:** [Nhóm của bạn hãy chèn link Video Demo sản phẩm tại đây]
