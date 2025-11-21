package model;

public class Board {
    // Khởi tạo mảng 2 chiều 10 hàng 9 cột làm bàn cờ
    public Piece[][] grid;

    public Board() {
        grid = new Piece[10][9];
        initStandardBoard();    // Xếp quân ngay khi tạo bàn cờ
    }

    // Hàm xếp quân cờ vào vị trí ban đầu
    public void initStandardBoard() {
        // --- PHE ĐỎ (Ở dưới, y = 9) ---
        addPiece(new Chariot(0, 9, true));      // Xe trái
        addPiece(new Horse(1, 9, true));        // Mã trái
        addPiece(new Horse(7, 9, true));        // Mã phải
        addPiece(new Chariot(8,9,true));        // Xe phải

        // --- PHE XANH (Ở trên, y = 0) ---
        addPiece(new Chariot(0, 0, false));     // Xe trái
        addPiece(new Horse(1, 0, false));       // Mã trái
        addPiece(new Horse(7, 0, false));       //Mã phải
        addPiece(new Chariot(8,0,false));       // Xe phải
    }

    // Hàm phụ để đặt quân vào mảng
    private void addPiece(Piece p) {
        grid[p.y][p.x] = p;
    }

    // Hàm vẽ bàn cờ ra màn hình Console
    public void printBoard() {
        System.out.println("   0   1   2   3   4   5   6   7   8");
        System.out.println(" +---+---+---+---+---+---+---+---+---+");

        for(int y = 0; y < 10; y++) {
            System.out.print(y + "|");
            for(int x = 0; x < 9; x++) {
                Piece p = grid[y][x];
                if(p != null) {
                    System.out.print(p.getSymbol() + "|");
                }
                else {
                    System.out.print(" . |");
                }
            }
            System.out.println();

            // Vẽ khu vực sông
            if(y == 4) {
                System.out.println(" | ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ | Sông Hà");
            } else {
                System.out.println(" +---+---+---+---+---+---+---+---+---+");
            }
        }
    }

    // Hàm thực hiện nước đi trên bàn cờ
    //Trả về true nếu đi thành công, false nếu đi lỗi
    public boolean executeMove(int startX, int startY, int endX, int endY) {
        Piece piece = grid[startY][startX];

        // 1. Kiểm tra ô xuất phát có quân nào không
        if(piece == null) {
            System.out.println("Error! Ô này không có quân cờ nào!");
            return false;
        }

        // 2.Hỏi xem quân cờ nàu có đi đúng luật không (Tự check luôn có nhảy ra ngoài bàn cờ không)
        if(!piece.isValidMove(endX, endY, this)) {
            System.out.println("Error! Nước đi sai luật của quân: " + piece.name);
            return false;
        }

        // 3. Kiểm tra ăn quân (Friendly Fire)
        // Nếu ô đích có quân, và quân đó cùng màu --> Không được ăn (Cản đường)
        Piece targetPiece = grid[endY][endX];
        if(targetPiece != null && targetPiece.isRed == piece.isRed) {
            System.out.println("Error! Không ăn quân cùng phe");
            return false;
        }

        // === Nếu thỏa hết điều kiện thì tiến hành di chuyển ===

        // Bước 1: cập nhật mảng grid (Xóa chỗ cũ, gán vào chỗ mới)
        grid[startY][startX] = null;    // Nhấc quân lên, chỗ cũ RỖNG
        grid[endY][endX] = piece;       // Đặt quân xuống đích

        // Bước 2: Cập nhật tọa độ trong quân cờ (Important!)
        // Quên cập nhật thì xác đi nhưng hồn còn đó=))
        piece.move(endX, endY);

        return true;    // Thành công
    }

    // Hàm đếm số quân cờ nằm GIỮA điểm xuất phát và đích đến
    // (Không tính điểm đầu và điểm cuối)
    // Chỉ áp dụng cho đường thẳng (Ngang hoặc Dọc)

    public int countObstacles(int x1, int y1, int x2, int y2) {
        int count = 0;

        // 1. Trường hợp đi Ngang (y bằng nhau)
        if(y1 == y2) {
            int minX = Math.min(x1, x2);
            int maxX = Math.max(x1, x2);
            // Chạy từ ô kề start đến ô kề end
            for(int k = minX + 1; k < maxX; k++) {
                if(grid[y1][k] != null) { // Nếu ô đó có quân
                    count++;
                }
            }
        }

        // 2. Trường hợp đi Dọc (x bằng nhau)
        else if(x1 == x2) {
            int minY = Math.min(y1, y2);
            int maxY = Math.max(y1, y2);
            for(int k = minY + 1; k < maxY; k++) {
                if(grid[k][x1] != null) {
                    count++;
                }
            }
        }
        return count;
    }
}
