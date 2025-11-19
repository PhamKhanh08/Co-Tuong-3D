package model;
// Class này định nghĩa quân XE

// 1. Haàm thiết lập (Constructor)
public class Chariot extends Piece {
    public Chariot(int x, int y, boolean isRed) {
        // Gọi về class Cha (super)
        super(x, y, isRed, "Xe"); // tên quân này mặc định là "Xe"
    }

    // 2. Định nghia luật đi: Xe đi thẳng hoặc đi ngang
    @Override
    public boolean isValidMove(int TargetX, int TargetY) {
        // Nếu đích đến trùng vị trí đang đứng --> Sai (không di chuyển)
        if(TargetX == this.x && TargetY == this.y) {
            return false;
        }

        // Luật của Xe:
        // - Hoặc là đi ngang (y giữ nguyên, x thay đổi)
        // - Hoặc là đi dọc (x giữ nguyên, y thay đổi)
        if(TargetX == this.x || TargetY == this.y) {
            return true;
        }

        // Nếu không phải ngang hay dọc --> Sai luật
        return false;
    }
    // Lưu ý: Hiện tại hàm chỉ kiểm tra "Hình dáng" đường đi.
    // Việc kiểm tra đường đi có bị quân khác chặn hay không sẽ xủ lí ở giai đoạn làm Bàn cờ (Board)
}
