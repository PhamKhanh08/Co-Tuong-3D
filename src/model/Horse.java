package model;

public class Horse extends Piece {
    public Horse(int x, int y, boolean isRed) {
        super(x, y, isRed, "Ma");
    }

    @Override
    public boolean isValidMove(int TargetX, int TargetY) {

        if (!isWithinBoard(TargetX, TargetY)) {
            return false;
        }
        // 1. Tính khoảng cách di chuyển theo X và Y (Dùng trị tuyệt đối)
        int dx = Math.abs(TargetX - this.x);
        int dy = Math.abs(TargetY - this.y);

        // 2. Kiểm tra hình dáng chữ L (2x1 hoặc 1x2)
        if(dx == 1 && dy == 2) return true;     // Trường hợp 1: Ngang 1 Dọc 2
        if(dx == 2 && dy == 1) return true;     // Trường hợp 2: Ngang 2 Dọc 1

        // Không phải chữ L --> Sai luật
        return false;
    }

    @Override
    public String getSymbol() {
        return isRed ? "R_M" : "B_M"; // M = Mã
    }
}
