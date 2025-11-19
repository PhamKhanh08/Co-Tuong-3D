package model;

public abstract class Piece {
    // 1. Thuộc tính chung mà quân cờ nào cũng phải có
    public int x;               // Tọa độ ngang (0-8)
    public int y;               // Tọa độ dọc (0-9)
    public boolean isRed;       // true = phe Đỏ, false = phe Đen
    public String name;         // tên Quân(VD: "Xe", "Phao")


    // 2. Hàm khởi tạo (Constructor) để khi spawn ra quân cờ có sẵn vị trí, màu sắc
    public Piece(int x, int y, boolean isRed, String name){
        this.x = x;
        this.y = y;
        this.isRed = isRed;
        this.name = name;
    }

    // 3. Phương thức trừu tượng (Abstract method)
    //Luật: Class cha ra lệnh đi (ko biết cách đi) và các class con sẽ đi theo cách đi của từng class
    // TargetX, TargetY là điểm đích muốn đến
    public abstract boolean isValidMove(int TargetX, int TargetY);

    //4. Hàm di chuyển chung (Thay đổi tọa độ)
    public void move(int TargetX, int TargetY) {
        this.x = TargetX;
        this.y = TargetY;
    }

    // 5. in thông tin quân cờ (Hỗ trợ Debug)
    @Override
    public String toString() {
        return name + (isRed ? " Đỏ" : " Đen") + " [" + x + ", " + y + "]";
    }
}