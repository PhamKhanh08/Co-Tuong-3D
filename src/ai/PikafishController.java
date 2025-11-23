package ai;

import javax.swing.JOptionPane;
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
    }

    public boolean startEngine() {
        try {
            // 1. LẤY ĐƯỜNG DẪN THỰC TẾ CỦA FILE JAR (Hoặc thư mục Project khi chạy IDE)
            String appPath = ".";
            try {
                appPath = new File(PikafishController.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 2. Tìm thư mục engine dựa trên đường dẫn appPath
            File engineDir = new File(appPath, "engine");

            if (!engineDir.exists()) {
                // HIỆN THÔNG BÁO LỖI NẾU KHÔNG TÌM THẤY (Để bạn biết nó đang tìm ở đâu)
                JOptionPane.showMessageDialog(null,
                        "LỖI AI: Không tìm thấy thư mục 'engine'!\n" +
                                "Game đang tìm tại: " + engineDir.getAbsolutePath() + "\n" +
                                "Hãy copy thư mục engine để vào cạnh file .jar");
                return false;
            }

            // 3. Tìm file exe
            File engineFile = new File(engineDir, "pikafish.exe");
            if (!engineFile.exists()) {
                JOptionPane.showMessageDialog(null,
                        "LỖI AI: Không tìm thấy file 'pikafish.exe'!\n" +
                                "Game đang tìm tại: " + engineFile.getAbsolutePath());
                return false;
            }

            // 4. Khởi chạy
            ProcessBuilder pb = new ProcessBuilder(engineFile.getAbsolutePath());
            pb.directory(engineDir);

            process = pb.start();
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

            startListening();
            sendCommand("uci");

            File nnueFile = new File(engineDir, "pikafish.nnue");
            if (nnueFile.exists()) {
                sendCommand("setoption name EvalFile value pikafish.nnue");
            } else {
                // Báo lỗi nếu thiếu nnue
                JOptionPane.showMessageDialog(null, "CẢNH BÁO: Không tìm thấy file 'pikafish.nnue' trong thư mục engine.");
            }

            sendCommand("isready");

            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khởi động AI: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void startListening() {
        listenerThread = new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("bestmove")) {
                        String move = line.split(" ")[1];
                        System.out.println("AI Move: " + move);
                        bestMoveQueue.offer(move);
                    }
                }
            } catch (IOException e) { }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void sendCommand(String command) {
        try {
            if (writer != null) {
                writer.write(command + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBestMove(String fen) {
        try {
            bestMoveQueue.clear();
            sendCommand("position fen " + fen);
            sendCommand("go movetime " + searchTime);
            String bestMove = bestMoveQueue.poll(searchTime + 2000, TimeUnit.MILLISECONDS);
            return bestMove;
        } catch (InterruptedException e) {
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
        } catch (IOException e) { }
    }
}