package dev.hetrodo.terrainprotector.dataTypes.classes;

import java.io.Serializable;

public class Vector3 implements Serializable {
    public final int x;
    public final int y;
    public final int z;

    public Vector3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(int x, int y, int z) {
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    public Vector3 subtract(int x, int y, int z) {
        return this.add(-x, -y, -z);
    }
}
