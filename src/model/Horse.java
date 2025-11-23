package model;

public class Horse extends Piece {
    public Horse(int x, int y, boolean isRed) {
        super(x, y, isRed, "Ma");
    }

    @Override
    public boolean isValidMove(int targetX, int targetY, Board board) { // Sửa Board Board -> Board board cho đúng chuẩn

        if (!isWithinBoard(targetX, targetY)) {
            return false;
        }

        int dx = Math.abs(targetX - this.x);
        int dy = Math.abs(targetY - this.y);

        // --- TRƯỜNG HỢP 1: Dọc 2, Ngang 1 (Nhảy dọc) ---
        if (dx == 1 && dy == 2) {
            // Tính tọa độ "Chân Mã" (Cản theo chiều dọc)
            // Là điểm nằm giữa Y cũ và Y mới
            int blockY = (this.y + targetY) / 2;
            int blockX = this.x; // X giữ nguyên

            // Kiểm tra xem ở chân có quân nào không?
            if (board.grid[blockY][blockX] != null) {
                // System.out.println("Mã bị cản chân dọc!"); // Debug nếu cần
                return false;
            }
            return true;
        }

        // --- TRƯỜNG HỢP 2: Ngang 2, Dọc 1 (Nhảy ngang) ---
        if (dx == 2 && dy == 1) {
            // Tính tọa độ "Chân Mã" (Cản theo chiều ngang)
            // Là điểm nằm giữa X cũ và X mới
            int blockX = (this.x + targetX) / 2;
            int blockY = this.y; // Y giữ nguyên

            // Kiểm tra chân
            if (board.grid[blockY][blockX] != null) {
                // System.out.println("Mã bị cản chân ngang!");
                return false;
            }
            return true;
        }

        // Không phải hình chữ L
        return false;
    }

    @Override
    public String getSymbol() {
        return isRed ? "R_M" : "B_M";
    }
}