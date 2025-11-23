package view;

import ai.PikafishController;
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

    public static final int START_X = 38;
    public static final int START_Y = 39;
    public static final int GAP_X = 65;
    public static final int GAP_Y = 69;
    public static final int PIECE_SIZE = 55;

    private Board board;
    private BufferedImage boardImage;
    private Map<String, Image> pieceImages;
    private Piece selectedPiece = null;

    private PikafishController ai;
    private boolean isAiThinking = false;

    public GamePanel(Board board) {
        this.board = board;

        ai = new PikafishController();
        if (ai.startEngine()) {
            System.out.println("✅ AI đã sẵn sàng!");
        }

        int width = START_X * 2 + 8 * GAP_X;
        int height = START_Y * 2 + 9 * GAP_Y;
        this.setPreferredSize(new Dimension(width, height));

        loadResources();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // --- TRƯỜNG HỢP ĐẶC BIỆT: GAME OVER ---
                // Nếu game đã kết thúc, click chuột để chơi lại ngay
                if (board.state != Board.STATE_PLAYING) {
                    restartGame();
                    return;
                }
                // --------------------------------------

                if (isAiThinking || !board.isRedTurn) return;
                handleMouseClick(e.getX(), e.getY());
            }
        });

        if (!board.isRedTurn) {
            aiMove();
        }

    }

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
                boolean success = board.executeMove(selectedPiece.x, selectedPiece.y, col, row);
                if (success) {
                    selectedPiece = null;
                    repaint();

                    // Nếu game kết thúc thì dừng AI và vẽ lại màn hình (để hiện thông báo)
                    if (board.state != Board.STATE_PLAYING) {
                        ai.stopEngine();
                        repaint();
                        return;
                    }

                    if (!board.isRedTurn) {
                        aiMove();
                    }
                }
            }
        }
        repaint();
    }

    private void aiMove() {
        isAiThinking = true;
        repaint();

        new Thread(() -> {
            try {
                Thread.sleep(500);
                String fen = board.getFen();
                String bestMove = ai.getBestMove(fen);

                if (bestMove != null) {
                    int startCol = bestMove.charAt(0) - 'a';
                    int startRow = 9 - (bestMove.charAt(1) - '0');
                    int endCol   = bestMove.charAt(2) - 'a';
                    int endRow   = 9 - (bestMove.charAt(3) - '0');

                    SwingUtilities.invokeLater(() -> {
                        board.executeMove(startCol, startRow, endCol, endRow);

                        if (board.state != Board.STATE_PLAYING) {
                            ai.stopEngine();
                        }

                        repaint();
                        isAiThinking = false;
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                isAiThinking = false;
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (boardImage != null) g2d.drawImage(boardImage, 0, 0, getWidth(), getHeight(), null);

        drawLastMove(g2d);
        drawTurnInfo(g2d);
        if (selectedPiece != null) drawValidMoves(g2d);

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = board.grid[y][x];
                if (p != null) drawSinglePiece(g2d, p, x, y);
            }
        }

        // --- VẼ MÀN HÌNH KẾT THÚC (NẾU CÓ) ---
        if (board.state != Board.STATE_PLAYING) {
            drawGameOverScreen(g2d);
        }
    }

    // --- HÀM MỚI: VẼ MÀN HÌNH CHIẾN THẮNG/THẤT BẠI ---
    private void drawGameOverScreen(Graphics2D g2) {
        // 1. Vẽ màn hình đen mờ che phủ (Overlay)
        g2.setColor(new Color(0, 0, 0, 150)); // Màu đen, trong suốt 150
        g2.fillRect(0, 0, getWidth(), getHeight());

        // 2. Chuẩn bị chữ
        String msg = "";
        Color color;
        if (board.state == Board.STATE_RED_WIN) {
            msg = "CHIẾN THẮNG!";
            color = new Color(255, 215, 0); // Màu vàng kim
        } else {
            msg = "THẤT BẠI...";
            color = new Color(200, 200, 200); // Màu xám bạc
        }

        // 3. Vẽ chữ to ở giữa
        g2.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = getHeight() / 2;

        // Vẽ bóng đổ cho chữ (Shadow)
        g2.setColor(Color.BLACK);
        g2.drawString(msg, x + 4, y + 4);

        // Vẽ chữ chính
        g2.setColor(color);
        g2.drawString(msg, x, y);

        // 4. Vẽ hướng dẫn chơi lại
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(Color.WHITE);
        String subMsg = "Click chuột bất kỳ để chơi lại";
        int subX = (getWidth() - g2.getFontMetrics().stringWidth(subMsg)) / 2;
        g2.drawString(subMsg, subX, y + 50);
    }

    private void drawTurnInfo(Graphics2D g2) {
        // Nếu hết game thì không vẽ chữ lượt nữa cho đỡ rối
        if (board.state != Board.STATE_PLAYING) return;

        g2.setFont(new Font("SansSerif", Font.BOLD, 30));
        if (isAiThinking) {
            g2.setColor(Color.BLUE);
            String text = "AI ĐANG NGHĨ...";
            int riverY = START_Y + 4 * GAP_Y + (GAP_Y / 2) + 10;
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(text)) / 2;
            g2.drawString(text, textX, riverY);
        } else {
            String text = board.isRedTurn ? "Lượt: BẠN (Đỏ)" : "Lượt: MÁY (Đen)";
            g2.setColor(board.isRedTurn ? Color.RED : Color.BLACK);
            int riverY = START_Y + 4 * GAP_Y + (GAP_Y / 2) + 10;
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(text)) / 2;
            g2.drawString(text, textX, riverY);
        }
    }

    private void drawLastMove(Graphics2D g2) {
        if (board.lastSrcX != -1) {
            // Vẽ dấu vết (Chấm xanh cũ + Vòng xanh mới) như code trước
            int srcX = START_X + board.lastSrcX * GAP_X;
            int srcY = START_Y + board.lastSrcY * GAP_Y;
            g2.setColor(new Color(0, 200, 0, 150));
            g2.fillOval(srcX - 8, srcY - 8, 16, 16);

            int dstX = START_X + board.lastDstX * GAP_X;
            int dstY = START_Y + board.lastDstY * GAP_Y;
            g2.setColor(Color.GREEN);
            g2.setStroke(new BasicStroke(3));
            int ringSize = PIECE_SIZE + 7;
            g2.drawOval(dstX - ringSize/2, dstY - ringSize/2, ringSize, ringSize);
        }
    }

    // ... (Giữ nguyên loadResources, drawValidMoves, drawSinglePiece, setAIDifficulty) ...
    private void loadResources() {
        try {
            boardImage = ImageIO.read(getClass().getResource("/board.png"));
            pieceImages = new HashMap<>();
            String[] keys = {"R_X", "R_M", "R_P", "R_T", "R_V", "R_S", "R_K", "B_X", "B_M", "B_P", "B_T", "B_V", "B_S", "B_K"};
            for (String key : keys) {
                BufferedImage img = ImageIO.read(getClass().getResource("/" + key + ".png"));
                Image scaledImg = img.getScaledInstance(PIECE_SIZE, PIECE_SIZE, Image.SCALE_SMOOTH);
                pieceImages.put(key, scaledImg);
            }
        } catch (Exception e) { e.printStackTrace(); }
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
        ai.stopEngine();
        if (ai.startEngine()) System.out.println("✅ AI Restarted!");
        isAiThinking = false;
        board.resetBoard(); // Lúc này board sẽ random lượt
        selectedPiece = null;
        repaint();
        ai.sendCommand("ucinewgame");

        // --- CHECK LƯỢT SAU KHI RESET ---
        // Nếu random trúng Đen thì cho AI đi luôn
        if (!board.isRedTurn) {
            aiMove();
        }

        JOptionPane.showMessageDialog(this, "Ván mới! Lượt đầu: " + (board.isRedTurn ? "BẠN (Đỏ)" : "MÁY (Đen)"));
    }

    public void setAIDifficulty(int timeMs) {
        ai.setSearchTime(timeMs);
        JOptionPane.showMessageDialog(this, "Đã chỉnh độ khó AI: " + (timeMs/1000) + " giây/nước");
    }
}