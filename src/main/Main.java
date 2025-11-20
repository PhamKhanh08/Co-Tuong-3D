package main;

import model.Chariot; // 2. Gọi (Import) con Xe từ gói "model" sang để dùng
import model.Horse; // <--- Nhớ import thêm con Mã

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TEST QUÂN MÃ (HORSE) ===");

        // 1. Tạo con Mã ở vị trí (2, 2) cho dễ nhảy
        Horse maDen = new Horse(2, 2, false);
        Horse maSatMep = new Horse(0, 2, false);
        System.out.println("Đã tạo: " + maDen.toString());

        // 2. Các nước đi HỢP LỆ (True)
        System.out.println("Nhảy chữ L lên trên (3, 4): " + maDen.isValidMove(3, 4)); // 2->3(1), 2->4(2) -> OK
        System.out.println("Nhảy chữ L sang trái (0, 3): " + maDen.isValidMove(0, 3)); // 2->0(2), 2->3(1) -> OK

        // 3. Các nước đi SAI (False)
        System.out.println("---------------------------");
        System.out.println("Đi thẳng như Xe (2, 5): " + maDen.isValidMove(2, 5));   // Sai
        System.out.println("Đi chéo như Tượng (4, 4): " + maDen.isValidMove(4, 4)); // Sai (dx=2, dy=2)
        System.out.println("Đứng im (2, 2): " + maDen.isValidMove(2, 2));           // Sai
        System.out.println("Nhảy ra khỏi bàn cờ (-1, 0): " + maSatMep.isValidMove(-1, 0));
    }
}