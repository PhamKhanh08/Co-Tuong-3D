package view;

import ai.PikafishController; // Import bộ điều khiển AI
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import model.Board;
import model.Piece;

public class GamePanel extends JPanel {

    // --- KHU VỰC CẤU HÌNH ĐỒ HỌA ---
    // (Giữ nguyên thông số bạn đã chỉnh)
    public static final int START_X = 38;
    public static final int START_Y = 39;
    public static final int GAP_X = 65;
    public static final int GAP_Y = 69;
    public static final int PIECE_SIZE = 55;

    private Board board;
    private BufferedImage boardImage;
    private Map<String, Image> pieceImages;
    private Piece selectedPiece = null;

    // --- BIẾN CHO AI ---
    private PikafishController ai;
    private boolean isAiThinking = false; // Cờ đánh dấu AI đang suy nghĩ

    public GamePanel(Board board) {
        this.board = board;

        // Khởi động AI
        ai = new PikafishController();
        if (ai.startEngine()) {
            System.out.println("AI đã sẵn sàng!");
        } else {
            System.err.println("Không bật được AI. Kiểm tra lại file pikafish.exe!");
        }

        // Kích thước cửa sổ
        int width = START_X * 2 + 8 * GAP_X;
        int height = START_Y * 2 + 9 * GAP_Y;
        this.setPreferredSize(new Dimension(width, height));

        loadResources();

        // XỬ LÝ CHUỘT
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Nếu AI đang suy nghĩ hoặc chưa đến lượt người chơi (Đỏ) thì chặn chuột
                if (isAiThinking || !board.isRedTurn) {
                    return;
                }
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    // Hàm xử lý logic chuột của Người chơi
    private void handleMouseClick(int mouseX, int mouseY) {
        int dx = mouseX - START_X;
        int dy = mouseY - START_Y;
        int col = (int) Math.round((double) dx / GAP_X);
        int row = (int) Math.round((double) dy / GAP_Y);

        if (col < 0 || col > 8 || row < 0 || row > 9) {
            selectedPiece = null;
            repaint();
            return;
        }

        Piece clickedPiece = board.grid[row][col];

        if (selectedPiece == null) {
            if (clickedPiece != null && clickedPiece.isRed == board.isRedTurn) {
                selectedPiece = clickedPiece;
            }
        } else {
            if (clickedPiece == selectedPiece) {
                selectedPiece = null;
            } else if (clickedPiece != null && clickedPiece.isRed == selectedPiece.isRed) {
                selectedPiece = clickedPiece;
            } else {
                // NGƯỜI CHƠI ĐI
                boolean success = board.executeMove(selectedPiece.x, selectedPiece.y, col, row);
                if (success) {
                    selectedPiece = null;
                    repaint();
                    checkGameOver(); // Kiểm tra thắng thua

                    // SAU KHI NGƯỜI ĐI XONG -> GỌI AI ĐI
                    if (!board.gameOver && !board.isRedTurn) {
                        aiMove(); // Kích hoạt AI
                    }
                }
            }
        }
        repaint();
    }

    // --- LOGIC AI (QUAN TRỌNG) ---
    private void aiMove() {
        isAiThinking = true;
        repaint();

        new Thread(() -> {
            try {
                Thread.sleep(500); // Độ trễ

                String fen = board.getFen();

                // Thêm try-catch riêng cho đoạn hỏi AI để tránh crash luồng
                String bestMove = null;
                try {
                    bestMove = ai.getBestMove(fen);
                } catch (Exception e) {
                    System.err.println("Lỗi khi hỏi AI: " + e.getMessage());
                }

                if (bestMove != null) {
                    int startCol = bestMove.charAt(0) - 'a';
                    int startRow = 9 - (bestMove.charAt(1) - '0');
                    int endCol   = bestMove.charAt(2) - 'a';
                    int endRow   = 9 - (bestMove.charAt(3) - '0');

                    SwingUtilities.invokeLater(() -> {
                        // Check lại lần nữa xem game có bị reset giữa chừng không
                        // Nếu board.gameOver đã bị reset thành false thì vẫn cho đi
                        board.executeMove(startCol, startRow, endCol, endRow);

                        if (board.gameOver) {
                            JOptionPane.showMessageDialog(this, "BẠN ĐÃ THUA!");
                            // Không stopEngine() ở đây nữa, để dành cho ván sau
                        }

                        repaint();
                        isAiThinking = false;
                    });
                } else {
                    // Nếu AI không trả lời (do bị stop giữa chừng)
                    isAiThinking = false;
                    SwingUtilities.invokeLater(this::repaint);
                }
            } catch (Exception e) {
                e.printStackTrace();
                isAiThinking = false;
            }
        }).start();
    }

    // Hàm kiểm tra kết thúc game
    private void checkGameOver() {
        if (board.gameOver) {
            String winner = board.isRedTurn ? "ĐEN (AI)" : "ĐỎ (Bạn)";
            JOptionPane.showMessageDialog(this, "GAME OVER! Phe " + winner + " thắng!");
            // Tắt AI khi hết game
            ai.stopEngine();
        }
    }

    // ... (Phần loadResources, paintComponent, draw... giữ nguyên như cũ) ...
    private void loadResources() {
        try {
            boardImage = ImageIO.read(getClass().getResource("/board.png"));
            pieceImages = new HashMap<>();
            String[] keys = {"R_X", "R_M", "R_P", "R_T", "R_V", "R_S", "R_K",
                    "B_X", "B_M", "B_P", "B_T", "B_V", "B_S", "B_K"};
            for (String key : keys) {
                BufferedImage img = ImageIO.read(getClass().getResource("/" + key + ".png"));
                Image scaledImg = img.getScaledInstance(PIECE_SIZE, PIECE_SIZE, Image.SCALE_SMOOTH);
                pieceImages.put(key, scaledImg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (boardImage != null) g2d.drawImage(boardImage, 0, 0, getWidth(), getHeight(), null);
        drawTurnInfo(g2d);
        if (selectedPiece != null) drawValidMoves(g2d);

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = board.grid[y][x];
                if (p != null) drawSinglePiece(g2d, p, x, y);
            }
        }
    }

    private void drawTurnInfo(Graphics2D g2) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 30));
        String text = board.isRedTurn ? "Lượt: BẠN (Đỏ)" : "Lượt: MÁY (Đen)...";
        g2.setColor(board.isRedTurn ? Color.RED : Color.BLACK);
        int riverY = START_Y + 4 * GAP_Y + (GAP_Y / 2) + 10;
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(text)) / 2;
        g2.drawString(text, textX, riverY);
    }

    private void drawValidMoves(Graphics2D g2) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                if (selectedPiece.isValidMove(col, row, board)) {
                    Piece target = board.grid[row][col];
                    boolean isAlly = (target != null && target.isRed == selectedPiece.isRed);
                    if (!isAlly) {
                        int cx = START_X + col * GAP_X;
                        int cy = START_Y + row * GAP_Y;
                        if (target == null) {
                            g2.setColor(new Color(0, 200, 0, 150));
                            g2.fillOval(cx - 7, cy - 7, 14, 14);
                        } else {
                            g2.setColor(new Color(255, 0, 0, 200));
                            g2.setStroke(new BasicStroke(4));
                            int ringSize = PIECE_SIZE + 6;
                            g2.drawOval(cx - ringSize/2, cy - ringSize/2, ringSize, ringSize);
                        }
                    }
                }
            }
        }
    }

    private void drawSinglePiece(Graphics2D g2, Piece p, int x, int y) {
        Image img = pieceImages.get(p.getSymbol());
        int centerX = START_X + (x * GAP_X);
        int centerY = START_Y + (y * GAP_Y);
        int pixelX = centerX - (PIECE_SIZE / 2);
        int pixelY = centerY - (PIECE_SIZE / 2);

        if (p == selectedPiece) {
            g2.setColor(Color.GREEN);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(pixelX, pixelY, PIECE_SIZE, PIECE_SIZE);
        }
        if (img != null) g2.drawImage(img, pixelX, pixelY, null);
    }

    public void restartGame() {
        // 1. DỪNG AI TUYỆT ĐỐI
        ai.stopEngine();

        // 2. KHỞI ĐỘNG LẠI AI MỚI
        if (ai.startEngine()) {
            System.out.println("✅ AI đã được khởi động lại cho ván mới!");
        } else {
            System.err.println("❌ Lỗi: Không khởi động lại được AI.");
        }

        isAiThinking = false; // Reset cờ trạng thái

        // 3. Reset dữ liệu bàn cờ
        board.resetBoard();

        // 4. Bỏ chọn quân
        selectedPiece = null;

        // 5. Vẽ lại màn hình
        repaint();

        // 6. Gửi lệnh ucinewgame cho AI
        ai.sendCommand("ucinewgame");

        JOptionPane.showMessageDialog(this, "Đã khởi tạo ván mới!");
    }

    /**
     * Hàm chỉnh độ khó cho AI (Gọi từ Menu)
     * @param timeMs Thời gian suy nghĩ (ms). Ví dụ: 1000 = 1 giây
     */
    public void setAIDifficulty(int timeMs) {
        // Lưu ý: Hàm setSearchTime cần phải có trong PikafishController.java
        ai.setSearchTime(timeMs);
        JOptionPane.showMessageDialog(this, "Đã chỉnh độ khó AI: " + (timeMs/1000) + " giây/nước");
    }
}