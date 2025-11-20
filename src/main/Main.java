package main;
import model.Board;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== GAME CỜ TƯỚNG (CONSOLE VERSION) ===");

        // 1. Khởi tạo bàn cờ
        Board banCo = new Board();

        // 2. In ra màn hình xem thử
        banCo.printBoard();
    }
}