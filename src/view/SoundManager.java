package view;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundManager {

    // Hàm phát âm thanh một lần (Play once)
    public static void play(String soundFileName) {
        new Thread(() -> {
            try {
                // 1. Lấy file từ thư mục res
                // Lưu ý: Phải có dấu / ở đầu tên file
                URL url = SoundManager.class.getResource("/" + soundFileName);

                if (url == null) {
                    System.err.println("Lỗi: Không tìm thấy file âm thanh: " + soundFileName);
                    return;
                }

                // 2. Mở luồng âm thanh
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);

                // 3. Lấy Clip để phát
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);

                // 4. Bắt đầu phát
                clip.start();

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start(); // Chạy luồng riêng để không làm đơ game
    }
}