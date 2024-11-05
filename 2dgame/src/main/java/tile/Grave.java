package tile;

import entity.Player;
import entity.Square;

public class Grave {
    private int x;
    private int y;
    private int width;
    private int height;
    Square grave_rect;

    public Grave(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        grave_rect= new Square(x, y, width, height);
    }

    public Square getHitbox() {
        return grave_rect;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // เพิ่มฟังก์ชันสำหรับการชนกับ Player หรือวัตถุอื่น ๆ
    public boolean intersects(Player player) {
        return player.getHitbox().intersects(grave_rect);
    }
}