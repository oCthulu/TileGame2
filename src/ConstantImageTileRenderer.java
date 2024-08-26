import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

public class ConstantImageTileRenderer<TileTypeT extends AbstractTileType<?>> extends TileRenderer<TileTypeT> {
    private final Image baseImage;

    //to ensure there are no seams, 4 scaled images are cached, each off by 1 pixel
    private Image scaled;

    public ConstantImageTileRenderer(Image image){
        this.baseImage = image;
        Main.onRescale.subscribe(this::onRescaled);
    }

    public ConstantImageTileRenderer(String path){
        try {
            InputStream resource = getClass().getResourceAsStream(path);
            if(resource == null) throw new InvalidParameterException("Could not find image at \"" + path + "\"");
            baseImage = ImageIO.read(resource);
            Main.onRescale.subscribe(this::onRescaled);
        }
        catch (IOException e){
            throw new RuntimeException("Reading the file threw an IOException");
        }
    }

    @Override
    public void render(Renderer r, Chunk.Tilemap<TileTypeT> tilemap, int localPosX, int localPosY) {
//        r.drawImageWorldSpace(
//                baseImage,
//                tilemap.getChunk().toGlobalPosX(localPosX),
//                tilemap.getChunk().toGlobalPosY(localPosY),
//                1,1
//        );

        int globalX = tilemap.getChunk().toGlobalPosX(localPosX);
        int globalY = tilemap.getChunk().toGlobalPosY(localPosY);

        int x = (int)Math.floor(Renderer.worldToScreenPosX(globalX));
        int y = (int)Math.floor(Renderer.worldToScreenPosY(globalY));

        Graphics g = r.graphics();

        g.drawImage(scaled, x, y, null);
    }

    private void onRescaled(){
        int size = (int)Renderer.worldToScreenVectorComponent(1);
        //cache scaled image
        scaled = baseImage.getScaledInstance(size+1, size+1, Image.SCALE_FAST);
    }
}
