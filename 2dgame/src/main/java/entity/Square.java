package entity;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Square {
    private Rectangle bounds;
    private int x;
    private int y;

    public Square(int x, int y, int width, int height) {
        bounds = new Rectangle(x, y, width, height);
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setPosition(int x, int y) {
        bounds.setLocation(x, y);
        this.x = x;
        this.y = y;
    }

    public boolean intersects(Square other) {
        return bounds.intersects(other.getBounds());
    }
    public void drawRectangle(Graphics2D g2,int width, int height) {
        // ตั้งค่าสี
        g2.setColor(Color.BLACK);
    
        // วาดเฉพาะกรอบของสี่เหลี่ยม
        // g2.drawRect(this.x, this.y, width, height);
    }
}
