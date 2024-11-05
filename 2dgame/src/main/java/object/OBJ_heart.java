package object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.GamePanel;

public class OBJ_heart{
    public BufferedImage heart_full, heart_blank;
    GamePanel gp;
    public OBJ_heart(GamePanel gp){
        this.gp = gp;
        try {
            heart_full = ImageIO.read(getClass().getResourceAsStream("/res/heart/heart_full.png"));
            heart_blank = ImageIO.read(getClass().getResourceAsStream("/res/heart/heart_blank.png"));
            heart_blank = scaleImage(heart_blank, gp.tileSize, gp.tileSize);
            heart_full = scaleImage(heart_full, gp.tileSize, gp.tileSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedImage scaleImage(BufferedImage img, int width, int height) {
        BufferedImage scaledImg  = new BufferedImage(width, height, img.getType());
        Graphics2D g2 = scaledImg.createGraphics();
        g2.drawImage(img, 0, 0,width,height, null);
        g2.dispose();

        return scaledImg;
    }
}
