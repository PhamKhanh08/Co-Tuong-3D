package main;

import model.Chariot; // 2. Gọi (Import) con Xe từ gói "model" sang để dùng

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TEST QUÂN XE (CHARIOT) ===");

        // 1. Tạo ra một con Xe Đỏ ở vị trí (0,0)
        Chariot xeDo = new Chariot(0, 0, true);
        System.out.println("Đã tạo: " + xeDo.toString());

        // 2. Test thử nước đi
        System.out.println("---------------------------");
        System.out.println("Đi dọc đến (0, 5): " + xeDo.isValidMove(0, 5)); // Phải ra true
        System.out.println("Đi ngang đến (9, 0): " + xeDo.isValidMove(9, 0)); // Phải ra true
        System.out.println("Đi chéo đến (2, 2): " + xeDo.isValidMove(2, 2)); // Phải ra false
        System.out.println("Đứng im tại chỗ (0,0): " + xeDo.isValidMove(0, 0)); // Phải ra false
    }
}