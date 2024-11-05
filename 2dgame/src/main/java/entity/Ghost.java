package entity;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import main.GamePanel;

public class Ghost extends Entity implements Runnable {
    GamePanel gp;
    Player player;
    Square hitbox;

    int health = 3; // เลือด 3 หน่วย
    public boolean isAlive = true;
    BufferedImage[] animations; // Array for animation frames
    int aniTick = 0; // Timer for animation frames
    int aniIndex = 0; // Current frame index
    int aniSpeed = 10; // Speed of the animation
    BufferedImage img; // The image sprite sheet

    private final int sethitbox = 10;

    private final int[][] spawnLocations;

    public Ghost(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player; // เชื่อมโยงกับวัตถุ Player
        spawnLocations = new int[][] {
            {100, 100},
            {gp.screenWidth - 200, 100},
            {100, gp.screenHeight - 200},
            {gp.screenWidth - 200, gp.screenHeight - 200},
            {gp.screenWidth / 4, gp.screenHeight / 4},
            {3 * gp.screenWidth / 4, gp.screenHeight / 4},
            {gp.screenWidth / 4, 3 * gp.screenHeight / 4},
            {3 * gp.screenWidth / 4, 3 * gp.screenHeight / 4}
        };
        setRandomSpawnLocation();
        setDefaultValues();
        
        try {
            img = ImageIO.read(getClass().getResourceAsStream("/res/ghost/ghost-Sheet.png")); // Load the ghost sprite sheet
            loadAnimations(); // Load animation frames
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.hitbox = new Square(x+sethitbox, y+sethitbox, width, height);
    }
    public void setRandomSpawnLocation() {
        Random rand = new Random();
        int[] spawnPoint = spawnLocations[rand.nextInt(spawnLocations.length)];
        this.x = spawnPoint[0];
        this.y = spawnPoint[1];
    }
    public void setDefaultValues(){
        width = 32;
        height = 32;

        speed = 3;
        direction="down";
    }

    public void loadAnimations() {
        if (img != null) {
            animations = new BufferedImage[4];
            for (int i = 0; i < animations.length; i++) {
                try {
                    animations[i] = img.getSubimage(i * 32, 0, 32, 32);
                    System.out.println("Loaded animation frame: " + i);
                } catch (Exception e) {
                    System.err.println("Error loading animation frame: " + i);
                }
            }
        }
    }
    public void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= animations.length) {
                aniIndex = 0; // Reset to the first frame
            }
        }
    }

    @Override
    public void run() {
        while (isAlive) {
            hitbox.setPosition(x+sethitbox, y+sethitbox);
            moveTowardsPlayer();
            updateAnimationTick(); // Update animation frame
            try {
                Thread.sleep(25); // Movement speed
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void moveTowardsPlayer() {
        // ทำให้ผีเดินเข้าหาผู้เล่น
        if (player.getplayerX()+32 > x) {
            x += speed; // เคลื่อนไปทางขวา
        } else if (player.getplayerX()+32 < x) {
            x -= speed; // เคลื่อนไปทางซ้าย
        }

        if (player.getplayerY()+16 > y) {
            y += speed; // เคลื่อนไปข้างล่าง
        } else if (player.getplayerY()+16 < y) {
            y -= speed; // เคลื่อนไปข้างบน
        }
    }

    public Square getHitbox() {
        return hitbox;
    }

    public int getGhostX(){
        return x;
    }

    public int getGhostY(){
        return y;
    }
    @Override
    public void takeDamage() {
        health--;
        if (health <= 0) {
            isAlive = false; // ถ้าเลือดหมดให้ทำให้ผีตาย
        }
        double angle = Math.atan2(player.getplayerY() - y, player.getplayerX() - x); // หาองศา
        x -= Math.cos(angle) * 20; 
        y -= Math.sin(angle) * 20; 
    }

    public boolean isOffScreen() {
        return x < 0 || x > gp.screenWidth || y < 0 || y > gp.screenHeight;
    }
    public void draw(Graphics2D g2) {
        if (isAlive) {
            try {
                g2.drawImage(animations[aniIndex], x, y, 48, 48, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        hitbox.drawRectangle(g2, 32, 32);
    }
}
