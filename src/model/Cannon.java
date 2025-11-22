package model;

public class Cannon extends Piece {
    public Cannon(int x, int y, boolean isRed) {
        super(x, y, isRed, "Phao");
    }

    @Override
    public boolean isValidMove(int targetX, int targetY, Board board) {
        // 1. Check cơ bản (Biên giới + Đứng im)
        if (!isWithinBoard(targetX, targetY)) return false;
        if (targetX == this.x && targetY == this.y) return false;

        // 2. Pháo đi Ngang hoặc Dọc (Giống Xe)
        if (targetX == this.x || targetY == this.y) {

            // Đếm số vật cản ở giữa (Hàm thần thánh bạn vừa viết)
            int obstacles = board.countObstacles(this.x, this.y, targetX, targetY);

            // Lấy thông tin ô đích đến xem có ai ở đó không?
            Piece targetPiece = board.grid[targetY][targetX];

            if (targetPiece == null) {
                // --- TRƯỜNG HỢP 1: ĐI ĐẾN Ô TRỐNG (Di chuyển) ---
                // Yêu cầu: Đường phải thoáng, KHÔNG được có vật cản (Giống Xe)
                if (obstacles == 0) {
                    return true;
                }
                // Nếu có vật cản mà đích lại trống -> Pháo không nhảy qua đầu được nếu không ăn ai
                return false;
            } else {
                // --- TRƯỜNG HỢP 2: ĐI ĐẾN Ô CÓ QUÂN (Ăn quân) ---
                // Yêu cầu: Phải có ĐÚNG 1 vật cản (Làm ngòi)
                if (obstacles == 1) {
                    return true;
                }
                // Nếu không có ngòi (0) hoặc có 2-3 ngòi -> Không ăn được
                return false;
            }
        }

        // Không phải ngang dọc
        return false;
    }

    @Override
    public String getSymbol() {
        return isRed ? "R_P" : "B_P"; // P = Pháo
    }
}
