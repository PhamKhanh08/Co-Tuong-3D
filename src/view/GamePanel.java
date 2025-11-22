package view;

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

/**
 * GamePanel - "Bộ da" của Game Cờ Tướng.
 * Nhiệm vụ:
 * 1. Vẽ hình ảnh (Bàn cờ, Quân cờ, Hiệu ứng).
 * 2. Bắt sự kiện chuột (Người chơi click vào đâu).
 * 3. Gọi Logic (Board) để xử lý luật chơi.
 */
public class GamePanel extends JPanel {

    // ==================================================================================
    // 1. KHU VỰC CẤU HÌNH (CALIBRATION)
    // Chỉnh các số ở đây để khớp hình ảnh quân cờ vào đúng giao điểm bàn cờ.
    // ==================================================================================

    // Tọa độ gốc (0,0) - Tức là tâm của con Xe Đen góc trên cùng bên trái
    public static final int START_X = 38; // Dời toàn bộ quân sang phải
    public static final int START_Y = 39; // Dời toàn bộ quân xuống dưới

    // Khoảng cách giữa các giao điểm (Kích thước lưới)
    public static final int GAP_X = 65;   // Khoảng cách cột dọc
    public static final int GAP_Y = 69;   // Khoảng cách hàng ngang

    // Kích thước hiển thị của quân cờ (Nên nhỏ hơn GAP một chút cho thoáng)
    public static final int PIECE_SIZE = 55;

    // ==================================================================================
    // 2. KHAI BÁO BIẾN (STATE)
    // ==================================================================================

    private Board board;                        // Tham chiếu đến bàn cờ Logic
    private BufferedImage boardImage;           // Ảnh nền bàn cờ
    private Map<String, Image> pieceImages;     // Kho chứa ảnh từng quân cờ (Cache)

    // Biến quan trọng: Lưu quân cờ người chơi đang click chọn
    // Nếu null nghĩa là chưa chọn quân nào.
    private Piece selectedPiece = null;

    // ==================================================================================
    // 3. KHỞI TẠO (CONSTRUCTOR)
    // ==================================================================================
    public GamePanel(Board board) {
        this.board = board;

        // A. Tính toán kích thước cửa sổ game
        // Công thức: Lề trái * 2 + 8 khoảng cách cột
        int width = START_X * 2 + 8 * GAP_X;
        int height = START_Y * 2 + 9 * GAP_Y;
        this.setPreferredSize(new Dimension(width, height));

        // B. Tải hình ảnh từ thư mục res
        loadResources();

        // C. Lắng nghe chuột (Mouse Listener) - Trái tim của tương tác
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY()); // Gọi hàm xử lý riêng cho gọn
            }
        });
    }

    /**
     * Hàm xử lý khi người chơi bấm chuột
     * @param mouseX Tọa độ chuột X trên màn hình
     * @param mouseY Tọa độ chuột Y trên màn hình
     */
    private void handleMouseClick(int mouseX, int mouseY) {
        // 1. Biến đổi tọa độ Chuột (Pixel) -> Tọa độ Lưới (Cột, Dòng)
        // Trừ đi điểm gốc (START) rồi chia cho khoảng cách (GAP)
        // Dùng Math.round để làm tròn, giúp người chơi click lệch tâm một chút vẫn ăn
        int dx = mouseX - START_X;
        int dy = mouseY - START_Y;
        int col = (int) Math.round((double) dx / GAP_X);
        int row = (int) Math.round((double) dy / GAP_Y);

        // 2. Kiểm tra xem click có ra ngoài bàn cờ không?
        if (col < 0 || col > 8 || row < 0 || row > 9) {
            selectedPiece = null; // Click ra ngoài thì bỏ chọn
            repaint();            // Vẽ lại màn hình
            return;
        }

        // 3. Lấy quân cờ tại vị trí vừa click (nếu có)
        Piece clickedPiece = board.grid[row][col];

        // --- LOGIC CHỌN VÀ DI CHUYỂN ---

        // TH1: Chưa có quân nào được chọn trước đó
        if (selectedPiece == null) {
            // Chỉ được chọn quân nếu: Có quân VÀ Quân đó đúng phe (đến lượt)
            if (clickedPiece != null && clickedPiece.isRed == board.isRedTurn) {
                selectedPiece = clickedPiece;
                System.out.println("Đã chọn: " + clickedPiece.getSymbol());
            }
        }
        // TH2: Đã có quân được chọn rồi
        else {
            // Nếu click lại vào chính quân đó -> BỎ CHỌN (Toggle)
            if (clickedPiece == selectedPiece) {
                selectedPiece = null;
            }
            // Nếu click vào quân khác CÙNG PHE -> ĐỔI QUA CHỌN CON MỚI
            else if (clickedPiece != null && clickedPiece.isRed == selectedPiece.isRed) {
                selectedPiece = clickedPiece;
            }
            // Nếu click vào ô trống hoặc quân địch -> THỰC HIỆN DI CHUYỂN
            else {
                boolean success = board.executeMove(selectedPiece.x, selectedPiece.y, col, row);
                if (success) {
                    selectedPiece = null; // Đi thành công thì bỏ chọn
                    // (Lượt đã được đổi bên trong hàm executeMove của Board rồi)
                }
                // Nếu success == false (đi sai luật), ta giữ nguyên selectedPiece
                // để người chơi có thể chọn ô khác mà không cần click lại quân.
            }
        }

        // 4. Vẽ lại màn hình ngay lập tức để cập nhật thay đổi
        repaint();
    }

    // ==================================================================================
    // 4. KHU VỰC TẢI TÀI NGUYÊN (IMAGES)
    // ==================================================================================
    private void loadResources() {
        try {
            // Tải ảnh bàn cờ
            // Lưu ý: Dấu "/" nghĩa là tìm trong thư mục Resources Root (res)
            boardImage = ImageIO.read(getClass().getResource("/board.png"));

            // Tải ảnh quân cờ
            pieceImages = new HashMap<>();
            String[] keys = {"R_X", "R_M", "R_P", "R_T", "R_V", "R_S", "R_K",
                    "B_X", "B_M", "B_P", "B_T", "B_V", "B_S", "B_K"};

            for (String key : keys) {
                BufferedImage img = ImageIO.read(getClass().getResource("/" + key + ".png"));
                // Co giãn ảnh cho vừa với kích thước PIECE_SIZE ta quy định
                Image scaledImg = img.getScaledInstance(PIECE_SIZE, PIECE_SIZE, Image.SCALE_SMOOTH);
                pieceImages.put(key, scaledImg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi tải ảnh! Hãy kiểm tra thư mục 'res' và tên file.");
        }
    }

    // ==================================================================================
    // 5. KHU VỰC VẼ (RENDER) - CÂY CỌ VẼ CỦA GAME
    // Hàm này được gọi tự động mỗi khi có lệnh repaint()
    // ==================================================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Bật khử răng cưa (Antialiasing) để hình ảnh và chữ mượt mà, không bị vỡ hạt
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // BƯỚC 1: Vẽ Bàn Cờ (Nền)
        if (boardImage != null) {
            g2d.drawImage(boardImage, 0, 0, getWidth(), getHeight(), null);
        }

        // BƯỚC 2: Vẽ Thông báo Lượt đi (Chữ ở giữa sông)
        drawTurnInfo(g2d);

        // BƯỚC 3: Vẽ Gợi ý nước đi (Các chấm xanh)
        // Vẽ cái này TRƯỚC quân cờ để chấm xanh nằm chìm bên dưới
        if (selectedPiece != null) {
            drawValidMoves(g2d);
        }

        // BƯỚC 4: Vẽ Các Quân Cờ
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                Piece p = board.grid[y][x];
                if (p != null) {
                    drawSinglePiece(g2d, p, x, y);
                }
            }
        }
    }

    // --- Hàm phụ: Vẽ chữ báo lượt ---
    private void drawTurnInfo(Graphics2D g2) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 30)); // Font chữ đậm, to

        String text;
        if (board.isRedTurn) {
            g2.setColor(Color.RED);
            text = "Lượt: ĐỎ";
        } else {
            g2.setColor(Color.BLACK);
            text = "Lượt: ĐEN";
        }

        // Tính vị trí để vẽ vào giữa sông (Khoảng hàng 4 và 5)
        int riverY = START_Y + 4 * GAP_Y + (GAP_Y / 2) + 10;

        // Căn giữa theo chiều ngang (Lấy độ rộng màn hình trừ độ rộng chữ rồi chia 2)
        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(text)) / 2;

        g2.drawString(text, textX, riverY);
    }

    // --- Hàm phụ: Vẽ chấm xanh gợi ý ---
    private void drawValidMoves(Graphics2D g2) {
        // Quét toàn bộ 90 giao điểm trên bàn cờ
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {

                // 1. Kiểm tra xem quân đang chọn có đi được đến ô (col, row) không
                if (selectedPiece.isValidMove(col, row, board)) {

                    // Lấy thông tin ô đích đến
                    Piece target = board.grid[row][col];

                    // Tính tọa độ tâm để vẽ
                    int cx = START_X + col * GAP_X;
                    int cy = START_Y + row * GAP_Y;

                    // TRƯỜNG HỢP 1: Ô TRỐNG (Di chuyển thường)
                    if (target == null) {
                        g2.setColor(new Color(0, 200, 0, 150)); // Xanh lá
                        g2.fillOval(cx - 7, cy - 7, 14, 14); // Vẽ chấm nhỏ
                    }
                    // TRƯỜNG HỢP 2: CÓ QUÂN (Kiểm tra xem địch hay ta)
                    else {
                        boolean isAlly = (target.isRed == selectedPiece.isRed);

                        // Nếu là ĐỊCH -> Vẽ vòng tròn đỏ cảnh báo (Ăn quân)
                        if (!isAlly) {
                            g2.setColor(new Color(255, 0, 0, 200)); // Màu Đỏ đậm
                            g2.setStroke(new BasicStroke(4)); // Viền dày 4px

                            // Vẽ vòng tròn bao quanh quân địch
                            // Kích thước to hơn quân cờ một chút để không che mất quân
                            int ringSize = PIECE_SIZE + 6;
                            g2.drawOval(cx - ringSize/2, cy - ringSize/2, ringSize, ringSize);
                        }
                        // Nếu là quân TA -> Không vẽ gì cả (Bị chặn)
                    }
                }
            }
        }
    }

    // --- Hàm phụ: Vẽ 1 quân cờ ---
    private void drawSinglePiece(Graphics2D g2, Piece p, int x, int y) {
        Image img = pieceImages.get(p.getSymbol());

        // Tính toán tọa độ vẽ ảnh (Căn giữa giao điểm)
        int centerX = START_X + (x * GAP_X);
        int centerY = START_Y + (y * GAP_Y);
        int pixelX = centerX - (PIECE_SIZE / 2);
        int pixelY = centerY - (PIECE_SIZE / 2);

        // Hiệu ứng: Nếu quân này đang được chọn -> Vẽ viền sáng lên
        if (p == selectedPiece) {
            g2.setColor(Color.GREEN);
            g2.setStroke(new BasicStroke(3)); // Nét viền dày 3px
            g2.drawOval(pixelX - 2, pixelY - 2, PIECE_SIZE + 4, PIECE_SIZE + 4);
        }

        // Vẽ ảnh quân cờ
        if (img != null) {
            g2.drawImage(img, pixelX, pixelY, null);
        }
    }
}