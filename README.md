Viewed yeucau-BTL.txt:1-218

Dựa trên yêu cầu đồ án hệ thống đấu giá trực tuyến của bạn, đây là lộ trình các bước chi tiết mà bạn cần thực hiện ở phía **Backend** để đạt được kết quả và điểm số tốt nhất:

---

## 🎯 Hướng dẫn chạy Code (Dành cho các thành viên trong nhóm)

Nếu bạn vừa mới tải (clone) Code này về máy và gặp lỗi **BUILD FAILURE** hoặc lỗi **Access denied for user 'root'**, lý do là vì Mật khẩu và cấu hình MySQL trên máy bạn khác với máy người tạo ra code. Bạn hãy làm đúng 3 bước sau trước khi chạy:

**1. Khởi tạo Database:**
* Mở phần mềm quản lý MySQL (XAMPP / MySQL Workbench / DataGrip,...)
* Tạo một schema/database mới tinh tên là `auction_db`.

**2. Đổi mật khẩu trong Code cho khớp với máy bạn:**
* Mở file mã nguồn: `src/main/resources/application.properties`
* Tìm đến dòng số 5 và 6:
```properties
spring.datasource.username=root
spring.datasource.password=123456
```
* Sửa `123456` thành mật khẩu MySQL cài trên máy bạn. (Lưu ý: Nếu dùng XAMPP mặc định không có mật khẩu thì bạn xoá trắng đi, chỉ để lại `spring.datasource.password=`)

**3. Khởi chạy hệ thống:**
* Chạy máy chủ trước: Click đúp vào file `run-server.bat` (hoặc mở Terminal gõ `mvn spring-boot:run`).
* Chạy giao diện App JavaFX sau: Click đúp vào file `run-client.bat`.

---

### Giai đoạn 1: Thiết kế & Khởi tạo (Foundation)
1. **Khởi tạo Project**: Tùy vào yêu cầu môn học, bạn hãy tạo dự án Maven hoặc Gradle. Bạn có thể sử dụng Framework như **Spring Boot** (giúp tiết kiệm rất nhiều công sức tạo REST API & kết nối DB) hoặc **Java thuần** (sử dụng thư viện Socket/Javalin) nếu giảng viên bắt buộc.
2. **Thiết kế Cơ sở dữ liệu / Entity Classes**:
   - `User`: id, username, password, role (ADMIN, SELLER, BIDDER).
   - `Item`: Khai báo dưới dạng *Abstract Class* hoặc *Interface* rồi áp dụng tính Kế thừa cho `Electronics`, `Art`, `Vehicle`,... Các thuộc tính chung: name, startPrice, currentPrice, startTime, endTime, status (OPEN, RUNNING, FINISHED).
   - `BidTransaction`: id, itemId, userId, bidAmount, timestamp.
3. **Cấu trúc thư mục (MVC)**:
   - `Controller/API`: Nơi điều hướng REST request và Socket API.
   - `Service`: Chứa business logic tổng (kiểm tra điều kiện bid, logic gia hạn).
   - `Repository/DAO`: Nơi truy vấn cơ sở dữ liệu (áp dụng Interface).

### Giai đoạn 2: Các API Quản trị Cơ bản
4. **API Đăng ký / Đăng nhập (Auth)**:
   - Viết API cấp Token (JWT) hoặc dùng Session khi người dùng đăng nhập.
   - Xử lý phân quyền chặn truy cập: Admin mới được quản lý hệ thống, Bidder chỉ được bid, Seller được đăng bán nhưng không được bid chính sản phẩm của mình.
5. **API Quản lý Sản phẩm (Seller)**: Xây dựng các tính năng Thêm, Sửa, Xóa thông tin sản phẩm. 

### Giai đoạn 3: Core Bidding & Xử lý Đồng thời (Quan trọng nhất)
6. **API Xử lý Đặt giá (Place Bid)**:
   - *Logic xác định hợp lệ*: Phiên phải đang ở trạng thái `RUNNING`, `bidAmount` phải lớn hơn `currentPrice`, thời gian hiện tại chưa vượt quá `endTime`.
   - **Xử lý Đấu giá đồng thời (Concurrent Bidding)** - *Yêu cầu lấy điểm tuyệt đối*: 
     - Khi nhiều người cùng click "Bid" ở cùng 1 phần ngàn giây, bạn phải khóa dữ liệu an toàn để tránh bị Lost Update (người bid sau đè người bid trước làm hao hụt lịch sử) hay Race Condition (Nhiều người cùng thắng).
     - *Giải pháp*: Dùng `Optimistic Locking` (@Version trong JPA Database) hoặc `Pessimistic Locking` (Lock nguyên dòng trong DB). Hoặc nếu dùng in-memory objects, phải dùng từ khóa `synchronized` hoặc các class `java.util.concurrent.locks.ReentrantLock`.
7. **Tự động thay đổi trạng thái (Background Scheduler)**:
   - Lập lịch một Background Task cứ mỗi X giây kiểm tra dữ liệu 1 lần:
     - `startTime <= now`: Chuyển item trạng thái sang `RUNNING`.
     - `endTime <= now`: Chuyển sang `FINISHED`, lấy user bid cao nhất đánh dấu là Winner.

### Giai đoạn 4: Realtime Update với Observer Pattern
8. **WebSockets / Server Sockets**:
   - Không để Client gọi hỏi (polling) server liên tục, mà Server phải chủ động đẩy dữ liệu xuống thông qua Socket.
   - Áp dụng **Observer Pattern**: Bất cứ khi nào 1 Bid diễn ra thành công (Subject thay đổi), Server tự động *broadcast* (thông báo) gói JSON gồm `newPrice`, `winnerName` xuống toàn bộ những Client đang xem chi tiết Item đó (Observers). Giao diện JavaFX/Client nhận thông điệp và tự động vẽ lại line chart đồ thị giá.

### Giai đoạn 5: Phát triển Tính năng nâng cao (Tùy chọn ăn điểm)
9. **Anti-sniping (Chống Bid giờ chót)**:
   - Trong hàm tiếp nhận Bid, check: `if (endTime - now <= X giây) -> endTime = endTime + Y giây`.
   - Nhớ kết nối với mục số (8) để broadcast thời gian đếm ngược mới cho user thấy.
10. **Auto-Bidding (Đấu giá tự động)**:
    - Khi có người A đặt bid, Service phải tìm xem trong CSDL có user B nào đang cài đặt `maxBid` lớn hơn giá trị hiện tại không.
    - Nếu có, tự sinh ra một Bot/Task trong Code để đấu giá đè lên người A cộng thêm khoản `increment` cho user B. Tránh vòng lặp vô tận thì có thể lưu cấu trúc dữ liệu PriorityQueue cho AutoBid.

### Giai đoạn 6: Hoàn thiện
11. **Unit Test với JUnit**: Viết test cho hàm xử lý Bid xem khi chạy đa luồng đồng thời (concurrent threads) có bị lỗi rollback hay lost update không.
12. **Áp dụng Design Patterns**:
    - `Singleton`: Quản lý object Auction Manager Database Connection.
    - `Factory Method`: Hàm sinh Item cụ thể `ItemFactory.createItem(type="Art")`.
    - `Observer` + `Strategy`.

**Bạn đang dự định xây dựng backend này thuần bằng Java Core (dùng `java.net.Socket`, JDBC) hay được phép sử dụng các nền tảng cao hơn như Spring Boot + Spring Data JPA + WebSockets?** Tùy thuộc vào công cụ bạn dùng mà cách code phần xử lý truy cập đồng thời và quản lý TCP liên lạc sẽ khác nhau đấy.