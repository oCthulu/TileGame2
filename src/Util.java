public class Util {
    //private constructor makes class (practically) uninstantiable (like the Math class)
    private Util(){}

    public static boolean isPointBelowLine(Vector2 p, Vector2 a, Vector2 b){
        Vector2 purp = b.sub(a);
        //noinspection SuspiciousNameCombination
        purp = new Vector2(-purp.y, purp.x);

        return p.sub(a).dot(purp) > 0;
    }

    public static boolean linesIntersect(Vector2 a1, Vector2 a2, Vector2 a3, Vector2 a4){
        return (isPointBelowLine(a3, a1, a2) ^ isPointBelowLine(a4, a1, a2)) &&
                (isPointBelowLine(a1, a3, a4) ^ isPointBelowLine(a2, a3, a4));
    }

    public static boolean isPointInTriangle(Vector2 p, Vector2 v1, Vector2 v2, Vector2 v3){
        return isPointBelowLine(p, v2, v1) &&
                isPointBelowLine(p, v3, v2) &&
                isPointBelowLine(p, v1, v3);
    }

    public static boolean trianglesIntersect(Vector2 a1, Vector2 a2, Vector2 a3, Vector2 b1, Vector2 b2, Vector2 b3){
        return isPointInTriangle(a1, b1, b2, b3) ||
                isPointInTriangle(a2, b1, b2, b3) ||
                isPointInTriangle(a3, b1, b2, b3) ||
                isPointInTriangle(b1, a1, a2, a3) ||
                isPointInTriangle(b2, a1, a2, a3) ||
                isPointInTriangle(b3, a1, a2, a3) ||
                linesIntersect(a1, a2, b1, b2) ||
                linesIntersect(a2, a3, b1, b2) ||
                linesIntersect(a3, a1, b1, b2) ||
                linesIntersect(a1, a2, b2, b3) ||
                linesIntersect(a2, a3, b2, b3) ||
                linesIntersect(a3, a1, b2, b3) ||
                linesIntersect(a1, a2, b3, b1) ||
                linesIntersect(a2, a3, b3, b1) ||
                linesIntersect(a3, a1, b3, b1);
    }

    public static boolean trianglesIntersect(Triangle a, Triangle b){
        return  trianglesIntersect(a.a(), a.b(), a.c(), b.a(), b.b(), b.c());
    }

    public static boolean trianglesIntersect(Triangle a, Triangle[] tris){
        for (Triangle tri : tris){
            if(trianglesIntersect(a, tri)) return true;
        }
        return false;
    }

    public static double invLerp(double a, double b, double t){
        return Math.clamp((t-a)/(b-a), 0, 1);
    }
}