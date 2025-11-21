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
    public boolean isValidMove(int TargetX, int TargetY, Board board) {

        if (!isWithinBoard(TargetX, TargetY)) {
            return false;
        }

        // Nếu đích đến trùng vị trí đang đứng --> Sai (không di chuyển)
        if(TargetX == this.x && TargetY == this.y) {
            return false;
        }

        // Luật của Xe:
        // - Hoặc là đi ngang (y giữ nguyên, x thay đổi)
        // - Hoặc là đi dọc (x giữ nguyên, y thay đổi)
        if(TargetX == this.x || TargetY == this.y) {
            int obstacles = board.countObstacles(this.x, this.y, TargetX, TargetY);

            // Luật của Xe: Đường đi phải thông thoáng (0 vật cản)
            if (obstacles > 0) {
                System.out.println("   [Xe] Lỗi: Có " + obstacles + " quân cản đường!");
                return false; // Bị chặn -> Không đi được
            }

            return true; // Đường thông thoáng -> OK
        }

        // Nếu không phải ngang hay dọc --> Sai luật
        return false;
    }
    @Override
    public String getSymbol() {
        return isRed ? "R_X" : "B_X"; // R = Red, B = Black, X = Xe
    }
}
