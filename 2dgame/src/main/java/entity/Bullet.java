package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.GamePanel;
public class Bullet extends Entity {
    GamePanel gp;
    int bulletX;
    int bulletY;
    int bulletSpeed = 5;
    String direction;
    BufferedImage bulletimg = null;
    public Bullet(GamePanel gp,int x,int y, String direction){
        this.gp = gp;
        this.bulletX = x;
        this.bulletY = y;
        this.direction = direction;
    }
    public void update(){
        try {
            switch(direction){
                case "right":
                    bulletX+=bulletSpeed;
                    break;
                case "left":
                    bulletX-=bulletSpeed;
                    break;
                case "up":
                    bulletY-=bulletSpeed;
                    break;
                case "down":
                    bulletY+=bulletSpeed;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getbulletX(){
        return bulletX;
    }
    public int getbulletY(){
        return bulletY;
    }
    
    public void draw(Graphics2D g2){
        try {
            bulletimg = ImageIO.read(getClass().getResourceAsStream("/res/bullet/bullet.png"));
            g2.drawImage(bulletimg, bulletX, bulletY, 32, 32, null);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
            
    }
    public boolean isOffScreen(){
        return bulletX < 0 || bulletX > gp.screenWidth || bulletY < 0 || bulletY > gp.screenHeight;
    }

    @Override
    public void takeDamage() {
    }
}
