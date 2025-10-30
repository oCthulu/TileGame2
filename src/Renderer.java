import java.awt.*;

public record Renderer(Graphics2D graphics) {
    //------------------------------------World to screen functions------------------------------------
    public static Vector2 worldToScreenVector(Vector2 world) {
        return new Vector2(worldToScreenVectorComponent(world.x), worldToScreenVectorComponent(world.y));
        //return world.scale(Player.instance.zoom * Main.getHeight());
    }
    public static double worldToScreenVectorComponent(double val){
        return val * Player.instance.getZoom() * Main.getHeight();
    }

    public static Vector2 worldToScreenPos(Vector2 world) {
        return new Vector2(worldToScreenPosX(world.x), worldToScreenPosY((world.y)));
        //return worldToScreenVector(world.sub(Player.instance.pos)).add(screenCenter());
    }

    public static double worldToScreenPosX(double x){
        return  worldToScreenVectorComponent(x - Player.instance.pos.x) + (Main.getWidth()/2);
    }

    public static double worldToScreenPosY(double y){
        return  worldToScreenVectorComponent(y - Player.instance.pos.y) + (Main.getHeight()/2);
    }

    //------------------------------------Screen to world functions------------------------------------
    public static Vector2 screenToWorldVector(Vector2 world) {
        return new Vector2(screenToWorldVectorComponent(world.x), screenToWorldVectorComponent(world.y));
        //return world.scale(Player.instance.zoom * Main.getHeight());
    }
    public static double screenToWorldVectorComponent(double val){
        return val / (Player.instance.getZoom() * Main.getHeight());
    }

    public static Vector2 screenToWorldPos(Vector2 screen) {
        return new Vector2(screenToWorldPosX(screen.x), screenToWorldPosY(screen.y));
        //return worldToScreenVector(world.sub(Player.instance.pos)).add(screenCenter());
    }

    public static double screenToWorldPosX(double x){
        return screenToWorldVectorComponent(x - (Main.getWidth()/2)) + Player.instance.pos.x;
    }

    public static double screenToWorldPosY(double y){
        return screenToWorldVectorComponent(y - (Main.getHeight()/2)) + Player.instance.pos.y;
    }

    //------------------------------------Rendering functions------------------------------------

    public Vector2 screenCenter() {
        return new Vector2(Main.getWidth(), Main.getHeight()).scale(0.5f);
    }

    public void setColor(Color c) {
        graphics.setColor(c);
    }

    public void drawRect(double posX, double posY, double sizeX, double sizeY) {
        int posXInt = (int) posX;
        int posYInt = (int) posY;

        double posXFrac = posX - posXInt;
        double posYFrac = posY - posYInt;

        graphics.fillRect(posXInt, posYInt, (int) (sizeX + posXFrac), (int) (sizeY + posYFrac));
    }

    public void drawRectWorldSpace(double posX, double posY, double sizeX, double sizeY) {
        drawRect(
                worldToScreenPosX(posX),
                worldToScreenPosY(posY),
                worldToScreenVectorComponent(sizeX),
                worldToScreenVectorComponent(sizeY)
        );
    }

    public void drawRect(Vector2 pos, Vector2 size) {
        drawRect(pos.x, pos.y, size.x, size.y);
    }

    public void drawRectWorldSpace(Vector2 pos, Vector2 size) {
        drawRect(worldToScreenPos(pos), worldToScreenVector(size));
    }

    public void drawImage(Image image, double posX, double posY, double sizeX, double sizeY) {
        int posXInt = (int) posX;
        int posYInt = (int) posY;

        double posXFrac = posX - posXInt;
        double posYFrac = posY - posYInt;

        graphics.drawImage(image, posXInt, posYInt, (int) (sizeX + posXFrac), (int) (sizeY + posYFrac), null);
    }

    public void drawImageWorldSpace(Image image, double posX, double posY, double sizeX, double sizeY) {
        drawImage(
                image,
                worldToScreenPosX(posX),
                worldToScreenPosY(posY),
                worldToScreenVectorComponent(sizeX),
                worldToScreenVectorComponent(sizeY)
        );
    }

    public void drawImage(Image image, Vector2 pos, Vector2 size) {
        drawImage(image, pos.x, pos.y, size.x, size.y);
    }

    public void drawImageWorldSpace(Image image, Vector2 pos, Vector2 size) {
        drawImage(image, worldToScreenPos(pos), worldToScreenVector(size));
    }

    public static double viewportLeftEdge(){
        return screenToWorldPosX(0);
    }
    public static double viewportRightEdge(){
        return screenToWorldPosX(Main.getWidth());
    }
    public static double viewportTopEdge(){
        return screenToWorldPosY(0);
    }
    public static double viewportBottomEdge(){
        return screenToWorldPosY(Main.getHeight());
    }

    public static Vector2 viewportTopLeftCorner(){
        return new Vector2(viewportLeftEdge(), viewportTopEdge());
    }

    public static Vector2 viewportBottomRightCorner(){
        return new Vector2(viewportRightEdge(), viewportBottomEdge());
    }
}
