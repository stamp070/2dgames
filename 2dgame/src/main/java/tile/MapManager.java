package tile;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import main.GamePanel;
public class MapManager {
    GamePanel gp;
    
    int[][][] MapLoaded;
    public Rectangle[][] TilesAround = {{null, null, null},{null, null, null},{null, null, null}};
    TileManager MapAsset;
    
    int Layers = 2;
    int[] MapSize = {100, 100};
    int[] TileSize = {32, 32, 0, 0, 0, 0};
    public ArrayList<Grave> graves = new ArrayList<>();
    
    String AssetPath = "/res/tileset/mapjaaaaa.png";
    String[] Maps = {
        "/map1/",
        "/map2/",
        "/map3/"
    };
    
    public MapManager(GamePanel gp, int MapNum) {
        this.gp = gp;
        LoadResource();
        ChangeMap(MapNum);
        for (int row = 0; row < 21; row++) {
            for (int col = 0; col < 30; col++) {
                int TileWorldY = (row * 32);
                int TileWorldX = (col * 32);
                int tileNum = MapLoaded[row][col][MapNum];
                if(tileNum == 101){
                    graves.add(new Grave(TileWorldX, TileWorldY, 32, 32));
                }
            }
        }
    }
    
    public void ChangeMap(int MapNum) {
        MapLoaded = new int[100][100][Layers];
        System.out.println("MapNum: " + MapNum);
        graves.clear();
        // add graves when change map
        try {
            InputStream is = getClass().getResourceAsStream("/res/maps"+Maps[MapNum]+"0_Ground.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            int row = 0;
            int layer = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int col = 0;
                for (int i=0; i < values.length; i++) {
                    MapLoaded[row][col++][layer] = Integer.parseInt(values[i]);
                }
                row++;
            }
            br.close();
            layer++;

            is = getClass().getResourceAsStream("/res/maps"+Maps[MapNum]+"1_Graveyard.csv");
            BufferedReader br2 = new BufferedReader(new InputStreamReader(is));

            row = 0;
            while ((line = br2.readLine()) != null) {
                String[] values = line.split(",");
                int col = 0;
                for (int i=0; i < values.length; i++) {
                    MapLoaded[row][col++][layer] = Integer.parseInt(values[i]);
                }
                row++;
            }
            br2.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        for (int row = 0; row < 21; row++) {
            for (int col = 0; col < 30; col++) {
                System.out.println();
                int TileWorldY = (row * 32);
                int TileWorldX = (col * 32);
                int tileNum = MapLoaded[row][col][1];
                if(tileNum == 101){
                    graves.add(new Grave(TileWorldX, TileWorldY, 32, 32));
                    System.out.println("row: " + row + "col: " + col);
                }
            }
        }
    }
    
    public void LoadResource() {
        MapAsset = new TileManager(AssetPath, TileSize, false, true);
        System.err.println(MapAsset);
    }

    public void draw(Graphics2D g2) {
        
        int tileSize = 32;
        int startRow = 0;
        int startCol = 0;
                
        for (int row = startRow; row < 21; row++) {
            for (int col = startCol; col < 30; col++) {
                int TileWorldY = (row * tileSize);
                int TileWorldX = (col * tileSize);
                
                for (int layer = 0; layer < 2; layer++) {
                    int tileNum = MapLoaded[row][col][layer];
                    if (tileNum == -1) continue;
                    
                    g2.drawImage(
                        MapAsset.GetTile(0, tileNum),
                        TileWorldX,
                        TileWorldY,
                        tileSize,
                        tileSize,
                        null
                    );
                }
            }
        }
        
    }
}
