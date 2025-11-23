package ai;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PikafishController {
    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Thread listenerThread;

    private final BlockingQueue<String> bestMoveQueue = new LinkedBlockingQueue<>();
    private int searchTime = 1000;

    public void setSearchTime(int timeMs) {
        this.searchTime = timeMs;
        System.out.println(">>> Đã cập nhật thời gian suy nghĩ: " + timeMs + "ms");
    }

    public boolean startEngine() {
        try {
            // 1. Xác định thư mục chứa engine
            File engineDir = new File("engine");
            if (!engineDir.exists() || !engineDir.isDirectory()) {
                System.err.println("LỖI: Không tìm thấy thư mục 'engine' ngang hàng với 'src'!");
                return false;
            }

            // 2. Tìm file exe
            File engineFile = new File(engineDir, "pikafish.exe");
            if (!engineFile.exists()) {
                System.err.println("LỖI: Không tìm thấy file pikafish.exe!");
                return false;
            }

            // 3. KHỞI CHẠY VỚI WORKING DIRECTORY (QUAN TRỌNG)
            // Thiết lập thư mục làm việc là "engine" để tránh lỗi đường dẫn tiếng Việt
            ProcessBuilder pb = new ProcessBuilder(engineFile.getAbsolutePath());
            pb.directory(engineDir); // <-- Dòng này sửa lỗi "P Khánh"

            process = pb.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            startListening();

            System.out.println(">>> Đang khởi động engine...");
            sendCommand("uci");

            // 4. Cấu hình NNUE (Chỉ cần gửi tên file, không cần đường dẫn dài)
            File nnueFile = new File(engineDir, "pikafish.nnue");
            if (nnueFile.exists()) {
                // Gửi mỗi tên file thôi, AI sẽ tự tìm trong thư mục hiện tại của nó
                sendCommand("setoption name EvalFile value pikafish.nnue");
            } else {
                System.err.println("CẢNH BÁO: Không tìm thấy file pikafish.nnue!");
            }

            sendCommand("isready");

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startListening() {
        listenerThread = new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    // System.out.println("AI LOG: " + line); // Bật dòng này nếu muốn debug sâu
                    if (line.startsWith("bestmove")) {
                        String move = line.split(" ")[1];
                        System.out.println("AI tìm ra nước đi: " + move);
                        bestMoveQueue.offer(move);
                    }
                }
            } catch (IOException e) {
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void sendCommand(String command) {
        try {
            if (writer != null) {
                writer.write(command + "\n");
                writer.flush();
                System.out.println("Java gửi: " + command);
            }
        } catch (IOException e) {
            System.err.println("Lỗi gửi lệnh: " + e.getMessage());
        }
    }

    public String getBestMove(String fen) {
        try {
            bestMoveQueue.clear();
            // System.out.println("--- BẮT ĐẦU TÌM NƯỚC ĐI ---");

            sendCommand("position fen " + fen);
            sendCommand("go movetime " + searchTime);

            String bestMove = bestMoveQueue.poll(searchTime + 2000, TimeUnit.MILLISECONDS);

            if (bestMove == null) {
                System.err.println("AI không phản hồi! (Timeout)");
            }

            return bestMove;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void stopEngine() {
        try {
            if (writer != null) {
                sendCommand("quit");
                writer.close();
            }
            if (reader != null) reader.close();
            if (process != null) process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}