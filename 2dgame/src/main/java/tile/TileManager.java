package tile;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


public class TileManager {
    private static final int FLIPPED_HORIZONTALLY_FLAG = 0x80000000; // Bit 31
    private static final int FLIPPED_VERTICALLY_FLAG = 0x40000000;   // Bit 30
    private static final int FLIPPED_DIAGONALLY_FLAG = 0x20000000;   // Bit 29
    
    // Variable
    String FileName;
    public int maxRow;
    public int maxColumn;
    public int newMaxColumn;
    boolean oneRow;
    int TileSizeX = 0;
    int TileSizeY = 0;
    int TileOffset_Top = 0;
    int TileOffset_Left = 0;
    int TileOffset_Right = 0;
    int TileOffset_Bottom = 0;
    
    public BufferedImage[][] TileSet;
    public BufferedImage[][] TileSetFlip;
    
    public TileManager(String pathFile, int[] size) {
        TileManagerFunc(pathFile, size, true, true);
    }
    
    public TileManager(String pathFile, int[] size, boolean flip) {
        TileManagerFunc(pathFile, size, flip, true);
    }
    
    public TileManager(String pathFile, int[] size, boolean flip, boolean oneRow) {
        TileManagerFunc(pathFile, size, flip, oneRow);
    }
    
    public void TileManagerFunc(String pathFile, int[] size, boolean flip, boolean oneRow) {
        this.oneRow = oneRow;
        FileName = pathFile;
        TileSizeX = size[0];
        TileSizeY = size[1];
        TileOffset_Left = size[2];
        TileOffset_Top = size[3];
        TileOffset_Right = size[4];
        TileOffset_Bottom = size[5];
        System.out.println(FileName);

        BufferedImage imgTemp = null;
        try{
           imgTemp = ImageIO.read(getClass().getResourceAsStream(FileName)); 
        }catch(IOException e){
            System.out.println("Error: " + e.getMessage());
        }
        this.maxRow = imgTemp.getHeight() / TileSizeY;
        this.maxColumn = imgTemp.getWidth() / TileSizeX;

        if (!oneRow) {
            this.TileSet = new BufferedImage[maxRow][maxColumn];
            if (flip) {
                this.TileSetFlip = new BufferedImage[maxRow][maxColumn];
            }
        } else {
            this.newMaxColumn = maxRow*maxColumn;
            this.TileSet = new BufferedImage[1][newMaxColumn];
            if (flip) {
                this.TileSetFlip = new BufferedImage[1][newMaxColumn];
            }
        }
        for (int row = 0; row < maxRow; row++) {
            for (int column = 0; column < maxColumn; column++) {
                BufferedImage imgCrop = imgTemp.getSubimage(
                    (column * TileSizeX) + TileOffset_Left,
                    (row * TileSizeY) + TileOffset_Top,
                    TileSizeX - TileOffset_Right,
                    TileSizeY - TileOffset_Bottom
                );
                if (!oneRow) {
                    this.TileSet[row][column] = imgCrop;
                    if (flip) {
                        this.TileSetFlip[row][column] = FlipImgHorizontally(imgCrop);
                    }
                } else {
                    int nowCol = (row*maxColumn)+column;
                    this.TileSet[0][nowCol] = imgCrop;
                    if (flip) {
                        this.TileSetFlip[0][nowCol] = FlipImgHorizontally(imgCrop);
                    }
                }
            }
        }
    }
    
    
    public BufferedImage GetTile(int row, int column) {
        if (oneRow && (column < 0 || column > newMaxColumn)) {
            int tileId = column & 0x1FFFFFFF;

            boolean flippedHorizontally = (column & FLIPPED_HORIZONTALLY_FLAG) != 0;
            boolean flippedVertically = (column & FLIPPED_VERTICALLY_FLAG) != 0;
            boolean flippedDiagonally = (column & FLIPPED_DIAGONALLY_FLAG) != 0;
            
            BufferedImage img = this.TileSet[row][tileId];
            if (flippedHorizontally) {
                img = FlipImgHorizontally(img);
            }
            if (flippedVertically) {
                img = FlipImgVertical(img);
            }
            
            return img;
        } else {
            return this.TileSet[row][column];
        }
    }
    
    public static BufferedImage FlipImgVertical(BufferedImage img) {
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -img.getHeight());

        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage flippedImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D g2d = flippedImg.createGraphics();
        g2d.drawImage(op.filter(img, null), 0, 0, null);
        g2d.dispose();

        return flippedImg;
    }
    
    public static BufferedImage FlipImgHorizontally(BufferedImage img) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-img.getWidth(), 0);

        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        BufferedImage flippedImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D g2d = flippedImg.createGraphics();
        g2d.drawImage(op.filter(img, null), 0, 0, null);
        g2d.dispose();

        return flippedImg;
    }
}
