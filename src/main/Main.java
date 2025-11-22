package main;
import model.Board;
import model.Board;
// Không cần import Sĩ/Tướng lẻ tẻ vì ta test qua Board
// Nhưng nếu IDE báo đỏ thì nhớ import model.Advisor và model.General nhé

public class Main {
    public static void main(String[] args) {
        Board banCo = new Board();
        System.out.println("=== TEST FULL BÀN CỜ ===");
        banCo.printBoard(); // In ra để ngắm đội hình full 32 quân

        System.out.println("\n=== TEST TƯỚNG (GENERAL) ===");
        // Tướng Đỏ đang ở (4, 9).

        System.out.println(">>> Test 1: Tướng đi lên (4, 8) (Trong cung)");
        boolean kq1 = banCo.executeMove(4, 9, 4, 8);
        System.out.println("Kết quả: " + (kq1 ? "Thành công" : "Thất bại"));
        banCo.printBoard();

        // Giả sử Tướng đã lên (4,8). Giờ thử đi ngang ra (2, 8) (Ra khỏi cung)
        // Cung chỉ từ cột 3 đến 5. Cột 2 là ngoài cung.
        System.out.println(">>> Test 2: Tướng đi ngang ra (2, 8) (Ra khỏi cung)");
        boolean kq2 = banCo.executeMove(4, 8, 2, 8);
        System.out.println("Kết quả: " + (kq2 ? "Đi được (SAI)" : "Bị chặn (ĐÚNG)"));
        banCo.printBoard();
    }
}