package model;

public class Board {
    // Khởi tạo mảng 2 chiều 10 hàng 9 cột làm bàn cờ
    public Piece[][] grid;

    public boolean gameOver = false; // true = Chiếu hết
    public boolean isRedTurn = true; // true = Lượt Đỏ, false = Lượt Đen

    public int lastSrcX = -1;
    public int lastSrcY = -1;
    public int lastDstX = -1;
    public int lastDstY = -1;

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
        if (gameOver) return false;

        Piece piece = grid[startY][startX];
        if (piece == null) return false;

        // 1. Check lượt đi
        if (piece.isRed != isRedTurn) return false;

        // 2. Check luật đi cơ bản của quân cờ
        if (!piece.isValidMove(endX, endY, this)) return false;

        // 3. Check ăn quân cùng màu
        Piece target = grid[endY][endX];
        if (target != null && target.isRed == piece.isRed) return false;

        // ---KIỂM TRA XEM ĐI XONG TƯỚNG CÓ CHẾT KHÔNG ---
        // Nếu nước đi này làm lộ mặt tướng hoặc vẫn để tướng bị chiếu -> KHÔNG ĐƯỢC ĐI
        if (isKingInDangerAfterMove(piece, endX, endY)) {
            System.out.println("Lỗi: Nước đi không hợp lệ! Tướng đang bị chiếu!");
            // Phát âm thanh cảnh báo (nếu muốn)
            return false;
        }
        // ------------------------------------------------------------------

        // Check Win (Ăn Tướng)
        if (target != null && target instanceof General) {
            gameOver = true;
            System.out.println("GAME OVER!");
            //view.SoundManager.play("win.wav");
        }

        // THỰC HIỆN DI CHUYỂN THẬT
        grid[startY][startX] = null;
        grid[endY][endX] = piece;
        piece.move(endX, endY);

        /* Âm thanh
        boolean isCapture = (target != null);
        if (isCapture) {
            view.SoundManager.play("capture.wav");
        } else {
            view.SoundManager.play("move.wav");
        }*/

        // Lưu vết
        lastSrcX = startX; lastSrcY = startY;
        lastDstX = endX; lastDstY = endY;

        isRedTurn = !isRedTurn;
        return true;
    }

    // ----------------------------------------------------------------
    // CÁC HÀM HỖ TRỢ LOGIC CHỐNG TỰ SÁT
    // ----------------------------------------------------------------

    /**
     * Giả lập nước đi và kiểm tra xem Tướng phe mình có bị ăn không
     */
    private boolean isKingInDangerAfterMove(Piece piece, int endX, int endY) {
        // 1. Sao lưu trạng thái cũ
        int startX = piece.x;
        int startY = piece.y;
        Piece originalTarget = grid[endY][endX]; // Quân bị ăn (nếu có)

        // 2. Di chuyển thử (Giả vờ)
        grid[startY][startX] = null;
        grid[endY][endX] = piece;
        piece.x = endX;
        piece.y = endY;

        // 3. Kiểm tra: Sau khi đi xong, Tướng phe mình có bị ai chiếu không?
        boolean inDanger = isKingChecked(piece.isRed);

        // 4. Hoàn trả trạng thái cũ (Undo move)
        piece.x = startX;
        piece.y = startY;
        grid[startY][startX] = piece;
        grid[endY][endX] = originalTarget;

        return inDanger; // Trả về true nếu nước đi này là tự sát
    }

    /*
     * Kiểm tra xem Tướng của phe 'isRed' có đang bị quân địch chiếu không
     */
    private boolean isKingChecked(boolean isRed) {
        // A. Tìm vị trí Tướng của phe mình
        int kingX = -1, kingY = -1;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = grid[y][x];
                if (p != null && p.isRed == isRed && p instanceof General) {
                    kingX = x;
                    kingY = y;
                    break;
                }
            }
        }

        // (Trường hợp hiếm: Không tìm thấy tướng -> coi như thua/bị chiếu)
        if (kingX == -1) return true;

        // B. Duyệt tất cả quân địch, xem có con nào ăn được Tướng mình không
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = grid[y][x];
                // Nếu là quân địch
                if (p != null && p.isRed != isRed) {
                    // Nếu quân địch này có thể đi đến vị trí Tướng -> BỊ CHIẾU!
                    if (p.isValidMove(kingX, kingY, this)) {
                        return true;
                    }
                }
            }
        }

        // C. Kiểm tra luật "Lộ mặt tướng" (Hai tướng nhìn thấy nhau)
        // Tìm Tướng địch
        int enemyKingX = -1, enemyKingY = -1;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = grid[y][x];
                if (p != null && p.isRed != isRed && p instanceof General) {
                    enemyKingX = x;
                    enemyKingY = y;
                    break;
                }
            }
        }

        // Nếu 2 tướng cùng cột và không có quân cản -> BỊ CHIẾU (Lộ mặt)
        if (kingX == enemyKingX) {
            if (countObstacles(kingX, kingY, enemyKingX, enemyKingY) == 0) {
                return true;
            }
        }

        return false; // An toàn
    }

    // ... (Hàm resetBoard, countObstacles, getFen... giữ nguyên như cũ) ...
    public void resetBoard() {
        grid = new Piece[10][9];
        initStandardBoard();
        isRedTurn = true;
        gameOver = false;
        lastSrcX = -1; lastSrcY = -1;
        lastDstX = -1; lastDstY = -1;
    }

    public int countObstacles(int x1, int y1, int x2, int y2) {
        int count = 0;
        if (y1 == y2) {
            int minX = Math.min(x1, x2);
            int maxX = Math.max(x1, x2);
            for (int k = minX + 1; k < maxX; k++) {
                if (grid[y1][k] != null) count++;
            }
        } else if (x1 == x2) {
            int minY = Math.min(y1, y2);
            int maxY = Math.max(y1, y2);
            for (int k = minY + 1; k < maxY; k++) {
                if (grid[k][x1] != null) count++;
            }
        }
        return count;
    }

    public String getFen() {
        StringBuilder sb = new StringBuilder();
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
                    sb.append(getFenChar(p));
                }
            }
            if (emptyCount > 0) sb.append(emptyCount);
            if (y < 9) sb.append("/");
        }
        sb.append(isRedTurn ? " w" : " b");
        sb.append(" - - 0 1");
        return sb.toString();
    }

    private char getFenChar(Piece p) {
        char c = ' ';
        if (p instanceof Chariot) c = 'r';
        else if (p instanceof Horse) c = 'n';
        else if (p instanceof Elephant) c = 'b';
        else if (p instanceof Advisor) c = 'a';
        else if (p instanceof General) c = 'k';
        else if (p instanceof Cannon) c = 'c';
        else if (p instanceof Soldier) c = 'p';
        return p.isRed ? Character.toUpperCase(c) : c;
    }
}