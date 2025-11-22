package main;

import model.Board;
import view.GamePanel;
import javax.swing.*;
// Đã xóa import thừa java.awt.event.ActionEvent

public class Main {
    public static void main(String[] args) {
        // 1. Khởi tạo Logic & Giao diện
        // Tạo bàn cờ (Model) chứa dữ liệu và luật chơi
        Board board = new Board();
        // Tạo giao diện (View) và truyền bàn cờ vào để nó biết cần vẽ cái gì
        GamePanel gamePanel = new GamePanel(board);

        // 2. Khởi tạo Cửa sổ chính (JFrame)
        JFrame window = new JFrame("Cờ Tướng AI - Pikafish Engine");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false); // Cấm kéo giãn cửa sổ để giữ đúng tỉ lệ đồ họa

        // --- TẠO MENU BAR (THANH MENU TRÊN CÙNG) ---
        JMenuBar menuBar = new JMenuBar();

        // A. Menu "Hệ thống"
        JMenu menuSystem = new JMenu("Hệ thống");

        // Mục: Ván mới
        JMenuItem itemNewGame = new JMenuItem("Ván mới (New Game)");
        // Sử dụng Lambda Expression cho gọn code
        itemNewGame.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(window,
                    "Bạn có chắc muốn chơi lại từ đầu?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                gamePanel.restartGame(); // Gọi hàm reset game bên GamePanel
            }
        });

        // Mục: Thoát
        JMenuItem itemExit = new JMenuItem("Thoát (Exit)");
        itemExit.addActionListener(e -> System.exit(0)); // Tắt chương trình

        // Thêm các mục vào menu con
        menuSystem.add(itemNewGame);
        menuSystem.addSeparator(); // Kẻ đường ngang phân cách cho đẹp
        menuSystem.add(itemExit);

        // B. Menu "Độ khó" (Thêm vào để tận dụng tính năng chỉnh time AI)
        JMenu menuLevel = new JMenu("Độ khó");

        JMenuItem itemEasy = new JMenuItem("Dễ");
        itemEasy.addActionListener(e -> gamePanel.setAIDifficulty(1000));

        JMenuItem itemMedium = new JMenuItem("Vừa");
        itemMedium.addActionListener(e -> gamePanel.setAIDifficulty(3000));

        JMenuItem itemHard = new JMenuItem("Khó");
        itemHard.addActionListener(e -> gamePanel.setAIDifficulty(5000));

        menuLevel.add(itemEasy);
        menuLevel.add(itemMedium);
        menuLevel.add(itemHard);

        // C. Menu "Thông tin"
        JMenu menuHelp = new JMenu("Thông tin");
        JMenuItem itemAbout = new JMenuItem("Giới thiệu");
        itemAbout.addActionListener(e -> {
            JOptionPane.showMessageDialog(window,
                    "Game Cờ Tướng Java\nEngine: Pikafish NNUE\nCode by: Phạm Hữu Phú Khánh",
                    "Giới thiệu", JOptionPane.INFORMATION_MESSAGE);
        });
        menuHelp.add(itemAbout);

        // Gắn các menu con vào thanh menu chính
        menuBar.add(menuSystem);
        menuBar.add(menuLevel); // Đừng quên thêm menu độ khó nhé
        menuBar.add(menuHelp);

        // Đưa thanh menu vào cửa sổ
        window.setJMenuBar(menuBar);
        // -------------------------------------------

        // Thêm GamePanel vào cửa sổ
        window.add(gamePanel);

        // Tự động co giãn cửa sổ vừa khít với nội dung bên trong (GamePanel)
        window.pack();

        // Hiển thị cửa sổ ra giữa màn hình
        window.setLocationRelativeTo(null);

        // Bật cửa sổ lên
        window.setVisible(true);
    }
}