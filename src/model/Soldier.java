package model;

public class Soldier extends Piece {
    public Soldier(int x, int y, boolean isRed) {
        super(x, y, isRed, "Tot");
    }

    @Override
    public boolean isValidMove(int targetX, int targetY, Board board) {
        if (!isWithinBoard(targetX, targetY)) return false;
        if (targetX == this.x && targetY == this.y) return false;

        // Tính khoảng cách di chuyển
        int dx = Math.abs(targetX - this.x);
        int dy = Math.abs(targetY - this.y);

        // Tốt chỉ đi từng bước một (Tổng dx + dy phải bằng 1)
        // Nghĩa là hoặc đi ngang 1 ô, hoặc đi dọc 1 ô, không đi chéo, không đi 2 ô
        if (dx + dy != 1) {
            return false;
        }

        // --- PHÂN TÍCH HƯỚNG ĐI (QUAN TRỌNG) ---
        if (isRed) {
            // 1. PHE ĐỎ (Đi từ dưới lên -> y phải giảm)
            // Nếu y tăng (đi lùi) -> SAI
            if (targetY > this.y) return false;

            // 2. Kiểm tra qua sông
            // Sông nằm giữa dòng 4 và 5.
            // Nếu đang ở phần sân nhà (y > 4) -> Chỉ được đi thẳng (dx = 0)
            if (this.y > 4 && dx != 0) {
                return false; // Chưa qua sông mà đòi đi ngang -> Chặn
            }
        } else {
            // 1. PHE ĐEN (Đi từ trên xuống -> y phải tăng)
            // Nếu y giảm (đi lùi) -> SAI
            if (targetY < this.y) return false;

            // 2. Kiểm tra qua sông
            // Nếu đang ở sân nhà (y < 5) -> Chỉ được đi thẳng (dx = 0)
            if (this.y < 5 && dx != 0) {
                return false;
            }
        }

        // Nếu qua các ải trên mà không bị return false -> Hợp lệ
        return true;
    }

    @Override
    public String getSymbol() {
        return isRed ? "R_T" : "B_T"; // T = Tốt
    }
}
