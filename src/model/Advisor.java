package model;

public class Advisor extends Piece {

    public Advisor(int x, int y, boolean isRed) {
        super(x, y, isRed, "Si");
    }

    @Override
    public boolean isValidMove(int targetX, int targetY, Board board) {
        if (!isWithinBoard(targetX, targetY)) return false;

        // 1. Luật hình dáng: Đi chéo đúng 1 ô
        int dx = Math.abs(targetX - this.x);
        int dy = Math.abs(targetY - this.y);

        if (dx != 1 || dy != 1) {
            return false; // Không phải chéo 1 ô
        }

        // 2. Luật Cung (Palace) - Quan trọng nhất
        // Chiều ngang: Phải nằm trong khoảng [3, 5]
        if (targetX < 3 || targetX > 5) return false;

        // Chiều dọc: Tùy phe
        if (isRed) {
            // Đỏ: Hàng 7, 8, 9
            if (targetY < 7 || targetY > 9) return false;
        } else {
            // Đen: Hàng 0, 1, 2
            if (targetY < 0 || targetY > 2) return false;
        }

        return true;
    }

    @Override
    public String getSymbol() {
        return isRed ? "R_S" : "B_S"; // S = Sĩ
    }
}