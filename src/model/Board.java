package model;

public class Board {
    // Khởi tạo mảng 2 chiều 10 hàng 9 cột làm bàn cờ
    public Piece[][] grid;

    public boolean gameOver = false; // true = Chiếu hết
    public boolean isRedTurn = true; // true = Lượt Đỏ, false = Lượt Đen

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

        // Pháo Đỏ (Hàng 7, Cột 1 và 7) - Lưu ý y=7 là hàng của Pháo đỏ (gần hàng 9)
        addPiece(new Cannon(1, 7, true));
        addPiece(new Cannon(7, 7, true));

        // Pháo Đen (Hàng 2, Cột 1 và 7)
        addPiece(new Cannon(1, 2, false));
        addPiece(new Cannon(7, 2, false));

        // --- THÊM 5 TỐT ĐỎ (Hàng 6 - tính theo index mảng, tức là dòng thứ 7 từ trên xuống) ---
        // Vị trí: 0, 2, 4, 6, 8
        for (int i = 0; i <= 8; i += 2) {
            addPiece(new Soldier(i, 6, true));
        }

        // --- THÊM 5 TỐT ĐEN (Hàng 3) ---
        for (int i = 0; i <= 8; i += 2) {
            addPiece(new Soldier(i, 3, false));
        }

        // --- THÊM 4 CON TƯỢNG ---
        // Tượng Đỏ
        addPiece(new Elephant(2, 9, true));
        addPiece(new Elephant(6, 9, true));

        // Tượng Đen
        addPiece(new Elephant(2, 0, false));
        addPiece(new Elephant(6, 0, false));

        // --- THÊM SĨ ---
        // Sĩ Đỏ (Hàng 9, Cột 3 và 5)
        addPiece(new Advisor(3, 9, true));
        addPiece(new Advisor(5, 9, true));
        // Sĩ Đen (Hàng 0, Cột 3 và 5)
        addPiece(new Advisor(3, 0, false));
        addPiece(new Advisor(5, 0, false));

        // --- THÊM TƯỚNG (KING) ---
        // Tướng Đỏ (Hàng 9, Cột 4 - Giữa cung)
        addPiece(new General(4, 9, true));
        // Tướng Đen (Hàng 0, Cột 4 - Giữa cung)
        addPiece(new General(4, 0, false));
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
        if(gameOver) return false;      // HẾT game thì chặn di chuyển
        // 1. Kiểm tra ô xuất phát có quân nào không
        if(piece == null) {
            return false;
        }

        // 2.Hỏi xem quân cờ nàu có đi đúng luật không (Tự check luôn có nhảy ra ngoài bàn cờ không)
        if(!piece.isValidMove(endX, endY, this)) {
            return false;
        }


        // 3. Kiểm tra ăn quân (Friendly Fire)
        // Nếu ô đích có quân, và quân đó cùng màu --> Không được ăn (Cản đường)
        Piece targetPiece = grid[endY][endX];
        if(targetPiece != null && targetPiece.isRed == piece.isRed) {
            return false;
        }
        // 4. Check luật đi
        if (piece.isRed != isRedTurn) {
            return false;
        }

        Piece target = grid[endY][endX];
        if (target != null && target.isRed == piece.isRed) return false;

        // --- CHECK ĂN TƯỚNG (WIN CONDITION) ---
        if (target != null && target instanceof General) {
            gameOver = true; // Kết thúc game!
            System.out.println("GAME OVER! Phe " + (piece.isRed ? "ĐỎ" : "ĐEN") + " thắng!");
        }
        // ---------------------------------------

        // === Nếu thỏa hết điều kiện thì tiến hành di chuyển ===
        // Bước 1: cập nhật mảng grid (Xóa chỗ cũ, gán vào chỗ mới)
        grid[startY][startX] = null;    // Nhấc quân lên, chỗ cũ RỖNG
        grid[endY][endX] = piece;       // Đặt quân xuống đích

        // Bước 2: Cập nhật tọa độ trong quân cờ (Important!)
        // Quên cập nhật thì xác đi nhưng hồn còn đó=))
        piece.move(endX, endY);

        // --- ĐỔI LƯỢT ---
        isRedTurn = !isRedTurn;

        return true;    // Thành công
    }
    //  Hàm reset ván cờ
    public void resetBoard() {
        grid = new Piece[10][9]; // Xóa bàn cờ cũ
        initStandardBoard();     // Xếp lại quân
        isRedTurn = true;        // Đỏ đi trước
        gameOver = false;        // Game chưa kết thúc
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

    // Chuyển bàn cờ hiện tại thành chuỗi FEN chuẩn quốc tế
    public String getFen() {
        StringBuilder sb = new StringBuilder();

        // 1. Duyệt từng hàng từ 9 về 0 (FEN tính từ hàng trên cùng xuống)
        // Lưu ý: Trong code của mình, hàng 0 là trên cùng (đen), hàng 9 là dưới cùng (đỏ).
        // FEN chuẩn cũng đi từ hàng 0 đến hàng 9.
        for (int y = 0; y < 10; y++) {
            int emptyCount = 0;
            for (int x = 0; x < 9; x++) {
                Piece p = grid[y][x];

                if (p == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        sb.append(emptyCount);
                        emptyCount = 0;
                    }
                    // Lấy ký tự đại diện (Xe đỏ = R, Xe đen = r)
                    sb.append(getFenChar(p));
                }
            }
            if (emptyCount > 0) {
                sb.append(emptyCount);
            }
            if (y < 9) {
                sb.append("/"); // Dấu ngăn cách hàng
            }
        }

        // 2. Thêm lượt đi (w = Red/Trắng đi, b = Black/Đen đi)
        // Lưu ý: FEN quốc tế dùng 'w' (Red) và 'b' (Black)
        sb.append(isRedTurn ? " w" : " b");

        // 3. Các thông số phụ (tạm thời để mặc định)
        // - - : Không có quyền nhập thành hay bắt tốt qua đường (không áp dụng cho cờ tướng nhưng cần có cho đủ format)
        // 0 : Số nước đi không ăn quân/tốt (halfmove clock)
        // 1 : Số thứ tự nước đi (fullmove number)
        sb.append(" - - 0 1");

        return sb.toString();
    }

    // Hàm phụ: Lấy ký tự FEN của từng quân
    private char getFenChar(Piece p) {
        // Lấy tên class để phân biệt chính xác loại quân
        // (Dùng instanceof an toàn hơn check string name)
        char c = ' ';

        if (p instanceof Chariot) c = 'r';      // Rook (Xe)
        else if (p instanceof Horse) c = 'n';   // Knight (Mã)
        else if (p instanceof Elephant) c = 'b';// Elephant/Bishop (Tượng)
        else if (p instanceof Advisor) c = 'a'; // Advisor (Sĩ)
        else if (p instanceof General) c = 'k'; // King (Tướng)
        else if (p instanceof Cannon) c = 'c';  // Cannon (Pháo)
        else if (p instanceof Soldier) c = 'p'; // Pawn (Tốt)

        // Quân Đỏ -> VIẾT HOA. Quân Đen -> viết thường
        return p.isRed ? Character.toUpperCase(c) : c;
    }
}
