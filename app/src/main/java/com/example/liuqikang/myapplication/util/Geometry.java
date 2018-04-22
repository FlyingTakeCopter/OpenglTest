package com.example.liuqikang.myapplication.util;

/**
 * Created by liuqikang on 2018/4/22.
 */

public class Geometry {
    public static class Point{
        public final float x, y, z;
        public Point(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point translateY(float distance){
            return new Point(x, y + distance, z);
        }
    }

    public static class Vector{
        public final float x, y, z;

        public Vector(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public static Vector vectorBetween(Point from, Point to){
            return new Vector(
                    to.x - from.x,
                    to.y - from.y,
                    to.z - from.z);
        }
    }
}
