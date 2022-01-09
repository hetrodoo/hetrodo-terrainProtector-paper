package dev.hetrodo.terrainprotector.dataTypes.classes;

import java.io.Serializable;

public class OldBlock implements Serializable {
    public final String material;
    public final Vector3 position;

    public OldBlock(String material, Vector3 position) {
        this.material = material;
        this.position = position;
    }
}
