package model;
import java.util.Random;

public class Board {
    public Piece[][] grid;

    // --- QUẢN LÝ TRẠNG THÁI GAME ---
    public static final int STATE_PLAYING = 0;
    public static final int STATE_RED_WIN = 1;
    public static final int STATE_BLACK_WIN = 2;
    public static final int STATE_DRAW = 3; // Hòa (nếu cần sau này)

    public int state = STATE_PLAYING;
    public boolean isRedTurn = true;

    // Biến lưu vết
    public int lastSrcX = -1, lastSrcY = -1, lastDstX = -1, lastDstY = -1;

    public Board() {
        grid = new Piece[10][9];
        initStandardBoard();
    }

    public void initStandardBoard() {
        // ... (Giữ nguyên phần khởi tạo quân cờ cũ của bạn) ...
        // Để ngắn gọn, tôi không paste lại đoạn addPiece dài dòng.
        // Bạn cứ giữ nguyên code trong hàm này nhé.
        addPiece(new Chariot(0, 9, true)); addPiece(new Horse(1, 9, true));
        addPiece(new Elephant(2, 9, true)); addPiece(new Advisor(3, 9, true));
        addPiece(new General(4, 9, true)); addPiece(new Advisor(5, 9, true));
        addPiece(new Elephant(6, 9, true)); addPiece(new Horse(7, 9, true));
        addPiece(new Chariot(8, 9, true));
        addPiece(new Cannon(1, 7, true)); addPiece(new Cannon(7, 7, true));
        for (int i = 0; i <= 8; i += 2) addPiece(new Soldier(i, 6, true));

        addPiece(new Chariot(0, 0, false)); addPiece(new Horse(1, 0, false));
        addPiece(new Elephant(2, 0, false)); addPiece(new Advisor(3, 0, false));
        addPiece(new General(4, 0, false)); addPiece(new Advisor(5, 0, false));
        addPiece(new Elephant(6, 0, false)); addPiece(new Horse(7, 0, false));
        addPiece(new Chariot(8, 0, false));
        addPiece(new Cannon(1, 2, false)); addPiece(new Cannon(7, 2, false));
        for (int i = 0; i <= 8; i += 2) addPiece(new Soldier(i, 3, false));
    }

    private void addPiece(Piece p) {
        grid[p.y][p.x] = p;
    }

    public boolean executeMove(int startX, int startY, int endX, int endY) {
        if (state != STATE_PLAYING) return false;

        Piece piece = grid[startY][startX];
        if (piece == null) return false;
        if (piece.isRed != isRedTurn) return false;

        if (!piece.isValidMove(endX, endY, this)) return false;

        // Chặn nước đi tự sát (Lộ mặt tướng hoặc vẫn bị chiếu)
        if (isKingInDangerAfterMove(piece, endX, endY)) {
            System.out.println("Lỗi: Nước đi không hợp lệ (Tướng bị chiếu)!");
            return false;
        }

        Piece target = grid[endY][endX];
        if (target != null && target.isRed == piece.isRed) return false;

        // Check ăn trực tiếp Tướng (Trường hợp hiếm nhưng cứ để)
        if (target != null && target instanceof General) {
            endGame(piece.isRed);
            return true;
        }

        // === DI CHUYỂN ===
        grid[startY][startX] = null;
        grid[endY][endX] = piece;
        piece.move(endX, endY);

        // Âm thanh
        if (target != null) view.SoundManager.play("capture.wav");
        else view.SoundManager.play("move.wav");

        // Lưu vết
        lastSrcX = startX; lastSrcY = startY;
        lastDstX = endX; lastDstY = endY;

        // Đổi lượt
        isRedTurn = !isRedTurn;

        // ---  KIỂM TRA HẾT CỜ (CHECKMATE/STALEMATE) NGAY SAU KHI ĐỔI LƯỢT  ---
        // Đến lượt phe kia, kiểm tra xem phe kia có còn nước đi nào không?
        if (!hasLegalMoves(isRedTurn)) {
            // Nếu đến lượt Đỏ mà Đỏ không đi được -> Đỏ Thua (Đen Thắng)
            // Nếu đến lượt Đen mà Đen không đi được -> Đen Thua (Đỏ Thắng)
            endGame(!isRedTurn);
        }
        // --------------------------------------------------------------------------

        return true;
    }

    // Hàm xử lý kết thúc game
    private void endGame(boolean redWins) {
        state = redWins ? STATE_RED_WIN : STATE_BLACK_WIN;
        System.out.println("GAME OVER! " + (redWins ? "ĐỎ" : "ĐEN") + " THẮNG!");
        view.SoundManager.play("win.wav");
    }

    // --- HÀM MỚI: KIỂM TRA XEM CÓ CÒN NƯỚC ĐI HỢP LỆ KHÔNG ---
    public boolean hasLegalMoves(boolean checkRed) {
        // Duyệt tất cả quân cờ của phe 'checkRed'
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = grid[y][x];
                if (p != null && p.isRed == checkRed) {
                    // Với mỗi quân, thử đi vào TẤT CẢ các ô trên bàn cờ
                    for (int ty = 0; ty < 10; ty++) {
                        for (int tx = 0; tx < 9; tx++) {
                            // 1. Check luật đi cơ bản
                            if (p.isValidMove(tx, ty, this)) {
                                // 2. Check xem ô đích có bị chặn bởi quân mình không
                                Piece target = grid[ty][tx];
                                if (target != null && target.isRed == p.isRed) continue;

                                // 3. Check xem đi xong có bị chết Tướng không
                                if (!isKingInDangerAfterMove(p, tx, ty)) {
                                    return true; // Vẫn còn ít nhất 1 nước đi -> Sống
                                }
                            }
                        }
                    }
                }
            }
        }
        return false; // Đã thử hết mọi nước mà không đi được -> CHẾT
    }

    public void resetBoard() {
        grid = new Piece[10][9];
        initStandardBoard();

        // RANDOM LƯỢT: 50% Đỏ, 50% Đen
        isRedTurn = new Random().nextBoolean();

        state = STATE_PLAYING;
        lastSrcX = -1; lastSrcY = -1; lastDstX = -1; lastDstY = -1;
    }

    private boolean isKingInDangerAfterMove(Piece piece, int endX, int endY) {
        int startX = piece.x; int startY = piece.y;
        Piece originalTarget = grid[endY][endX];
        grid[startY][startX] = null;
        grid[endY][endX] = piece;
        piece.x = endX; piece.y = endY;
        boolean inDanger = isKingChecked(piece.isRed);
        piece.x = startX; piece.y = startY;
        grid[startY][startX] = piece;
        grid[endY][endX] = originalTarget;
        return inDanger;
    }

    private boolean isKingChecked(boolean isRed) {
        int kingX = -1, kingY = -1;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = grid[y][x];
                if (p != null && p.isRed == isRed && p instanceof General) {
                    kingX = x; kingY = y; break;
                }
            }
        }
        if (kingX == -1) return true;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = grid[y][x];
                if (p != null && p.isRed != isRed) {
                    if (p.isValidMove(kingX, kingY, this)) return true;
                }
            }
        }
        int enemyKingX = -1, enemyKingY = -1;
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = grid[y][x];
                if (p != null && p.isRed != isRed && p instanceof General) {
                    enemyKingX = x; enemyKingY = y; break;
                }
            }
        }
        if (kingX == enemyKingX) {
            if (countObstacles(kingX, kingY, enemyKingX, enemyKingY) == 0) return true;
        }
        return false;
    }

    public int countObstacles(int x1, int y1, int x2, int y2) {
        int count = 0;
        if (y1 == y2) {
            int minX = Math.min(x1, x2); int maxX = Math.max(x1, x2);
            for (int k = minX + 1; k < maxX; k++) { if (grid[y1][k] != null) count++; }
        } else if (x1 == x2) {
            int minY = Math.min(y1, y2); int maxY = Math.max(y1, y2);
            for (int k = minY + 1; k < maxY; k++) { if (grid[k][x1] != null) count++; }
        }
        return count;
    }

    public String getFen() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 10; y++) {
            int emptyCount = 0;
            for (int x = 0; x < 9; x++) {
                Piece p = grid[y][x];
                if (p == null) { emptyCount++; }
                else {
                    if (emptyCount > 0) { sb.append(emptyCount); emptyCount = 0; }
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