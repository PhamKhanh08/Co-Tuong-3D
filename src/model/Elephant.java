package model;

public class Elephant extends Piece {
    public Elephant(int x, int y, boolean isRed) {
        super(x, y, isRed, "Tuong");
    }
    @Override
    public boolean isValidMove(int targetX, int targetY, Board board) {
        if (!isWithinBoard(targetX, targetY)) return false;
        if (targetX == this.x && targetY == this.y) return false;

        // 1. Luật hình dáng: Đi chéo đúng 2 ô
        int dx = Math.abs(targetX - this.x);
        int dy = Math.abs(targetY - this.y);

        if (dx != 2 || dy != 2) {
            return false; // Không phải chéo 2 ô -> Sai
        }

        // 2. Luật Qua Sông: Tượng không được sang sông
        if (isRed) {
            // Tượng Đỏ (ở dưới) không được lên hàng 4 (Sông là ranh giới 4-5)
            if (targetY < 5) return false;
        } else {
            // Tượng Đen (ở trên) không được xuống hàng 5
            if (targetY > 4) return false;
        }

        // 3. Luật "Cản mắt Tượng" (Quan trọng)
        // Tìm tọa độ điểm giữa (Mắt tượng)
        int eyeX = (this.x + targetX) / 2;
        int eyeY = (this.y + targetY) / 2;

        // Nếu có quân đứng ở mắt -> Bị chặn
        if (board.grid[eyeY][eyeX] != null) {
            System.out.println("   [Tượng] Lỗi: Bị cản mắt tại (" + eyeX + "," + eyeY + ")");
            return false;
        }

        return true;
    }

    @Override
    public String getSymbol() {
        return isRed ? "R_V" : "B_V"; // V = Voi (Để tránh trùng chữ T của Tốt/Tướng)
    }
}
