import java.util.function.IntFunction;
import java.util.function.Predicate;

public class TileType extends AbstractTileType<TileType>{
    private static final Vector2 O_TL = new Vector2(0  ,0  );
    private static final Vector2 O_TC = new Vector2(0.5,0  );
    private static final Vector2 O_TR = new Vector2(1  ,0  );
    private static final Vector2 O_CL = new Vector2(0  ,0.5);
    private static final Vector2 O_CC = new Vector2(0.5,0.5);
    private static final Vector2 O_CR = new Vector2(1  ,0.5);
    private static final Vector2 O_BL = new Vector2(0  ,1  );
    private static final Vector2 O_BC = new Vector2(0.5,1  );
    private static final Vector2 O_BR = new Vector2(1  ,1  );

    //lookup for improved collision
    //and yes, I had to do this BY HAND (very painful)
    private static final Triangle[][][] collisionLookup = {
            {
                    //0b0000
                    {new Triangle(O_CC, O_TL, O_TR)},
                    {new Triangle(O_CC, O_TR, O_BR)},
                    {new Triangle(O_CC, O_BR, O_BL)},
                    {new Triangle(O_CC, O_BL, O_TL)}
            },
            {
                    //0b0001
                    {},
                    {new Triangle(O_CC, O_TR, O_BR), new Triangle(O_CC, O_TC, O_TR)},
                    {new Triangle(O_CC, O_BR, O_BL)},
                    {new Triangle(O_CC, O_BL, O_TL), new Triangle(O_CC, O_TL, O_TC)}
            },
            {
                    //0b0010
                    {new Triangle(O_CC, O_TL, O_TR), new Triangle(O_CC, O_TR, O_CR)},
                    {},
                    {new Triangle(O_CC, O_BR, O_BL), new Triangle(O_CC, O_CR, O_BR)},
                    {new Triangle(O_CC, O_BL, O_TL)}
            },
            {
                    //0b0011
                    {},
                    {},
                    {new Triangle(O_BL, O_TR, O_BR)},
                    {new Triangle(O_BL, O_TL, O_TR)}
            },
            {
                    //0b0100
                    {new Triangle(O_CC, O_TL, O_TR)},
                    {new Triangle(O_CC, O_TR, O_BR), new Triangle(O_CC, O_BR, O_BC)},
                    {},
                    {new Triangle(O_CC, O_BL, O_TL), new Triangle(O_CC, O_BC, O_BL)}
            },
            {
                    //0b0101
                    {},
                    {new Triangle(O_CL, O_TL, O_TR), new Triangle(O_CL, O_TR, O_CR)},
                    {},
                    {new Triangle(O_CL, O_CR, O_BR), new Triangle(O_CL, O_BR, O_BL)},
            },
            {
                    //0b0110
                    {new Triangle(O_TL, O_TR, O_BR)},
                    {},
                    {},
                    {new Triangle(O_TL, O_BR, O_BL)}
            },
            {
                    //0b0111
                    {},
                    {},
                    {},
                    {new Triangle(O_TL, O_TR, O_BL), new Triangle(O_TR, O_BR, O_BL)}
            },
            {
                    //0b1000
                    {new Triangle(O_CC, O_TL, O_TR), new Triangle(O_CC, O_CL, O_TL)},
                    {new Triangle(O_CC, O_TR, O_BR)},
                    {new Triangle(O_CC, O_BR, O_BL), new Triangle(O_CC, O_BL, O_CL)},
                    {}
            },
            {
                    //0b1001
                    {},
                    {new Triangle(O_TL, O_TR, O_BR)},
                    {new Triangle(O_TL, O_BR, O_BL)},
                    {}
            },
            {
                    //0b1010
                    {},
                    {new Triangle(O_BC, O_TR, O_BR), new Triangle(O_BC, O_TC, O_TR)},
                    {},
                    {new Triangle(O_BC, O_BR, O_TR), new Triangle(O_BC, O_TR, O_TC)}
            },
            {
                    //0b1011
                    {},
                    {},
                    {new Triangle(O_TL, O_TR, O_BL), new Triangle(O_TR, O_BR, O_BL)},
                    {}
            },
            {
                    //0b1100
                    {new Triangle(O_TR, O_BL, O_TL)},
                    {new Triangle(O_TR, O_BR, O_BL)},
                    {},
                    {}
            },
            {
                    //0b1101
                    {},
                    {new Triangle(O_TL, O_TR, O_BL), new Triangle(O_TR, O_BR, O_BL)},
                    {},
                    {}
            },
            {
                    //0b1110
                    {new Triangle(O_TL, O_TR, O_BL), new Triangle(O_TR, O_BR, O_BL)},
                    {},
                    {},
                    {}
            },
            {
                    //0b1111
                    {new Triangle(O_TL, O_TR, O_BL), new Triangle(O_TR, O_BR, O_BL)},
                    {},
                    {},
                    {}
            }
    };

    public final BackgroundTileType backgroundType;

    private TileType(TileRenderer<TileType> renderer, BackgroundTileType backgroundType) {
        super(renderer);
        this.backgroundType = backgroundType;
    }

    public TileType(TileRenderer<TileType> renderer) {
        this(renderer, (BackgroundTileType) null);
    }

    public TileType(TileRenderer<TileType> renderer, TileRenderer<BackgroundTileType> bgRenderer){
        super(renderer);
        this.backgroundType = new BackgroundTileType(bgRenderer, this);
    }

    public void processCollision(Collider collider, Chunk.Tilemap<TileType> tilemap, Vector2Int localPos){
        Vector2Int gPos = tilemap.getChunk().toGlobalPos(localPos);
        Vector2 bCenter = new Vector2(gPos.x + 0.5f, gPos.y + 0.5f);
        Vector2 bBottomLeft = new Vector2(gPos.x, gPos.y + 1);
        Vector2 bBottomRight = new Vector2(gPos.x+1, gPos.y + 1);
        Vector2 bTopLeft = new Vector2(gPos.x, gPos.y);
        Vector2 bTopRight = new Vector2(gPos.x+1, gPos.y);

        Vector2 cCenter = collider.getCenter();
        Vector2 semiSize = collider.getSize().scale(0.5f);

        Vector2 cBottomLeft = new Vector2(cCenter.x - semiSize.x, cCenter.y + semiSize.y);
        Vector2 cBottomRight = new Vector2(cCenter.x + semiSize.x, cCenter.y + semiSize.y);
        Vector2 cTopLeft = new Vector2(cCenter.x - semiSize.x, cCenter.y - semiSize.y);
        Vector2 cTopRight = new Vector2(cCenter.x + semiSize.x, cCenter.y - semiSize.y);

        Vector2 newCenter = new Vector2(cCenter.x, cCenter.y);

        int colIndex = getNeighborsBitfield(tilemap, localPos, false);

        //in this case subtracting the player hitbox by gPos is the same as adding the gPos to the block collision
        //right face of block
        if(Util.trianglesIntersect(new Triangle(cCenter, cBottomLeft, cTopLeft).sub(gPos), collisionLookup[colIndex][1])){
            newCenter = newCenter.withX(gPos.x + semiSize.x + 1);
            collider.onLeftCollide();
        }

        //left face of block
        if(Util.trianglesIntersect(new Triangle(cCenter, cTopRight, cBottomRight).sub(gPos), collisionLookup[colIndex][3])){
            newCenter = newCenter.withX(gPos.x - semiSize.x);
            collider.onRightCollide();
        }

        //bottom face of block
        if(Util.trianglesIntersect(new Triangle(cCenter, cTopLeft, cTopRight).sub(gPos), collisionLookup[colIndex][2])){
            newCenter = newCenter.withY(gPos.y + semiSize.y + 1);
            collider.onTopCollide();
        }

        //top face of block
        if(Util.trianglesIntersect(new Triangle(cCenter, cBottomRight, cBottomLeft).sub(gPos), collisionLookup[colIndex][0])){
            newCenter = newCenter.withY(gPos.y - semiSize.y);
            collider.onBottomCollide();
        }

        collider.setCenter(newCenter);
    }
}
