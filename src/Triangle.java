public record Triangle(Vector2 a, Vector2 b, Vector2 c) {
    public Triangle(double ax, double ay, double bx, double by, double cx, double cy){
        this(new Vector2(ax, ay), new Vector2(bx, by), new Vector2(cx, cy));
    }

    public Triangle sub(Vector2 val){
        return new Triangle(a.sub(val), b.sub(val), c.sub(val));
    }

    public Triangle sub(Vector2Int val){
        return sub(val.toVector());
    }
}
