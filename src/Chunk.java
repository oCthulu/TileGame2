import java.util.HashMap;

public class Chunk extends GameObject {
    public class Tilemap<TileTypeT extends AbstractTileType<?>>{
        private final TileTypeT[][] tiles;

        private Tilemap(TileTypeT[][] tiles){
            this.tiles = tiles;
        }

        public Chunk getChunk(){
            return Chunk.this;
        }

        public TileTypeT getTile(int x, int y){
            return tiles[x][y];
        }

        public TileTypeT getTile(Vector2Int pos){
            return getTile(pos.x, pos.y);
        }
        public void setTile(int x, int y, TileTypeT value){
            tiles[x][y] = value;
        }
        public void setTile(Vector2Int pos, TileTypeT value){
            setTile(pos.x, pos.y, value);
        }
    }
    public static final int CHUNK_SIZE = 64;
    private static final HashMap<Vector2Int, Chunk> chunkMap = new HashMap<>();

    public final Vector2Int chunkPos;
    public final Vector2Int globalChunkPos;

    public final Tilemap<TileType> mainTilemap;
    public final Tilemap<BackgroundTileType> backgroundTilemap;

    public Chunk(Vector2Int chunkPos){
        this.chunkPos = chunkPos;
        chunkMap.put(chunkPos, this);
        globalChunkPos = chunkPos.scale(CHUNK_SIZE);

        mainTilemap = new Tilemap<>(new TileType[CHUNK_SIZE][CHUNK_SIZE]);
        backgroundTilemap = new Tilemap<>(new BackgroundTileType[CHUNK_SIZE][CHUNK_SIZE]);
    }

    public static Vector2Int toChunkPos(Vector2 global){
        return global.scale(1D/CHUNK_SIZE).floorToInt();
    }

    public static Vector2Int toChunkPos(Vector2Int global){
        return toChunkPos(global.toVector());
    }

    public static Chunk tryGetChunk(Vector2Int chunkPos){
        return chunkMap.get(chunkPos);
    }

    public static Chunk getChunk(Vector2Int chunkPos){
        Chunk chunk = tryGetChunk(chunkPos);
        if(chunk == null) return World.loadChunk(chunkPos);
        return chunk;
    }

    public static boolean hasChunk(Vector2Int chunkPos){
        return chunkMap.containsKey(chunkPos);
    }

    public int toGlobalPosX(int x){
        return globalChunkPos.x + x;
    }

    public int toGlobalPosY(int y){
        return globalChunkPos.y + y;
    }

    public Vector2Int toGlobalPos(Vector2Int local){
        return new Vector2Int(toGlobalPosX(local.x), toGlobalPosY(local.y));
    }

    public int toLocalPosX(int x){
        return x - globalChunkPos.x;
    }

    public int toLocalPosY(int y){
        return y - globalChunkPos.y;
    }
    public Vector2Int toLocalPos(Vector2Int global) {
        return new Vector2Int(toLocalPosX(global.x), toLocalPosY(global.y));
    }

    @Override
    public void render(Renderer r) {
        if(
                toGlobalPosX(CHUNK_SIZE) < Renderer.viewportLeftEdge() ||
                toGlobalPosX(0) > Renderer.viewportRightEdge()
                //toGlobalPosY(CHUNK_SIZE) < Renderer.viewportBottomEdge()// ||
                //toGlobalPosY(0) > Renderer.viewportTopEdge()
        ) return;

        //rendering it in two separate loops for foreground and background is more efficient (I think)
        //render background blocks
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                int globalX = toGlobalPosX(x);
                int globalY = toGlobalPosY(y);

                if(
                        Renderer.worldToScreenPosX(globalX + 1) < 0 ||
                                Renderer.worldToScreenPosY(globalY+1) < 0 ||
                                Renderer.worldToScreenPosX(globalX) > Main.getWidth() ||
                                Renderer.worldToScreenPosY(globalY) > Main.getHeight()
                ) continue;

                BackgroundTileType bgTile = backgroundTilemap.getTile(x, y);
                TileType tile = mainTilemap.getTile(x, y);

                //TODO: account for transparency in foreground tile types
                if(tile == null && bgTile != null) bgTile.render(r, backgroundTilemap, x, y);
                //if(tile != null) tile.render(r, mainTilemap, x, y);
            }
        }

        //render foreground blocks
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                int globalX = toGlobalPosX(x);
                int globalY = toGlobalPosY(y);

                if(
                        Renderer.worldToScreenPosX(globalX + 1) < 0 ||
                        Renderer.worldToScreenPosY(globalY+1) < 0 ||
                        Renderer.worldToScreenPosX(globalX) > Main.getWidth() ||
                        Renderer.worldToScreenPosY(globalY) > Main.getHeight()
                ) continue;

                TileType tile = mainTilemap.getTile(x, y);

                if(tile != null) tile.render(r, mainTilemap, x, y);
            }
        }
    }

    @Override
    public void tick() {

    }
}
