import java.util.Objects;
import java.util.function.Predicate;

public abstract class AbstractTileType<TSelf extends AbstractTileType<TSelf>> {
    public final TileRenderer<TSelf> renderer;

    public static <TileTypeT extends AbstractTileType<?>> int getNeighborsBitfield(
            Chunk.Tilemap<TileTypeT> tilemap,
            int localPosX,
            int localPosY,
            Predicate<TileTypeT> predicate,
            boolean defaultIfEdgeCase
    ){
        int def = defaultIfEdgeCase? 1 : 0;
        return  ((localPosY > 0                 )? (predicate.test(tilemap.getTile(localPosX, localPosY-1))? 1 : 0) : def) |
                ((localPosX < Chunk.CHUNK_SIZE-1)? (predicate.test(tilemap.getTile(localPosX+1, localPosY))? 1 : 0) : def) << 1 |
                ((localPosY < Chunk.CHUNK_SIZE-1)? (predicate.test(tilemap.getTile(localPosX, localPosY+1))? 1 : 0) : def) << 2 |
                ((localPosX > 0                 )? (predicate.test(tilemap.getTile(localPosX-1, localPosY))? 1 : 0) : def) << 3;
    }

    public static <TileTypeT extends AbstractTileType<?>> int getNeighborsBitfield(
            Chunk.Tilemap<TileTypeT> tilemap,
            Vector2Int localPos,
            Predicate<TileTypeT> predicate,
            boolean defaultIfEdgeCase
    ) {
        return getNeighborsBitfield(tilemap, localPos.x, localPos.y, predicate, defaultIfEdgeCase);
    }

    public static <TileTypeT extends AbstractTileType<?>> int getNeighborsBitfield(
            Chunk.Tilemap<TileTypeT> tilemap,
            Vector2Int localPos,
            boolean defaultIfEdgeCase
    ) {
        return getNeighborsBitfield(tilemap, localPos, Objects::nonNull, defaultIfEdgeCase);
    }

    public void render(Renderer r, Chunk.Tilemap<TSelf> tilemap, int localPosX, int localPosY){
        renderer.render(r, tilemap, localPosX, localPosY);
    }

    public AbstractTileType(TileRenderer<TSelf> renderer){
        this.renderer = renderer;
    }
}
