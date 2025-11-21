package main;
import model.Board;
import model.Horse; // Nhớ import quân Mã để làm vật cản

public class Main {
    public static void main(String[] args) {
        Board banCo = new Board();

        System.out.println("=== BÀN CỜ BAN ĐẦU ===");
        banCo.printBoard();

        System.out.println("\n>>> Test Xe xuyên tường:");

        // 1. THIẾT LẬP TÌNH HUỐNG (SETUP)
        // Đặt giả một con Mã chặn ngang đường tại ô (0, 5)
        System.out.println("DEBUG: Đang đặt vật cản vào vị trí (0, 5)...");
        banCo.grid[5][0] = new Horse(0, 5, true);

        // In lại bàn cờ để chắc chắn vật cản đã xuất hiện
        banCo.printBoard();

        // 2. THỰC HIỆN DI CHUYỂN (ACTION)
        // Xe Đỏ (0,9) muốn phi thẳng lên (0,0) ăn Xe Đen
        // Nhưng bây giờ đã có con Mã ở (0,5) cản đường.
        boolean kq3 = banCo.executeMove(0, 9, 0, 0);

        // 3. KIỂM TRA KẾT QUẢ (ASSERT)
        System.out.println("Kết quả lệnh đi: " + (kq3 ? "Đi được (SAI)" : "Bị chặn (ĐÚNG)"));
    }
}