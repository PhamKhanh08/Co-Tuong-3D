package main;
import model.Board;
import view.GamePanel;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // 1. Khởi tạo Logic
        Board board = new Board();

        // 2. Khởi tạo Giao diện (Cửa sổ)
        JFrame window = new JFrame("Cờ Tướng Java - Version 1.0");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Nhét cái bàn cờ vẽ tay vào cửa sổ
        window.add(new GamePanel(board));

        window.pack(); // Tự động co giãn cửa sổ cho vừa bàn cờ
        window.setLocationRelativeTo(null); // Hiện giữa màn hình
        window.setResizable(false);
        window.setVisible(true); // Bật lên
    }
}