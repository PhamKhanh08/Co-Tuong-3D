package model;

public class General extends Piece {

    public General(int x, int y, boolean isRed) {
        super(x, y, isRed, "Tuong"); // Tên là Tướng (Trùm)
    }

    @Override
    public boolean isValidMove(int targetX, int targetY, Board board) {
        if (!isWithinBoard(targetX, targetY)) return false;

        // 1. Luật hình dáng: Đi ngang hoặc dọc 1 ô
        int dx = Math.abs(targetX - this.x);
        int dy = Math.abs(targetY - this.y);

        // Tổng dx + dy phải bằng 1 (nghĩa là 1 chiều đi 1, chiều kia đi 0)
        if (dx + dy != 1) {
            return false;
        }

        // 2. Luật Cung (Palace) - Copy y hệt Sĩ
        if (targetX < 3 || targetX > 5) return false;

        if (isRed) {
            if (targetY < 7 || targetY > 9) return false;
        } else {
            if (targetY < 0 || targetY > 2) return false;
        }

        return true;
    }

    @Override
    public String getSymbol() {
        return isRed ? "R_K" : "B_K"; // K = King (Phân biệt với Tốt/Tượng)
    }
}
