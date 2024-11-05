package entity;

import java.awt.image.BufferedImage;

public abstract class Entity {
    public int x,y;
    public double speed;
    public int width,height;
    
    public BufferedImage up, down, left, right;
    public String direction;

    public abstract void takeDamage();
}
