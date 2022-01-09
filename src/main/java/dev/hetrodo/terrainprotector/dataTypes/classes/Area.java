package dev.hetrodo.terrainprotector.dataTypes.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Area implements Serializable {
    public final String owner;
    public final Vector3 a;
    public final Vector3 b;
    public final List<String> members;
    public final int size;
    public final Vector3 center;
    public final List<OldBlock> oldBlockList;

    public Area(Vector3 a, Vector3 b, String owner, int size, Vector3 center) {
        this.a = new Vector3(a.x, 0, a.z);
        this.b = new Vector3(b.x, 0, b.z);
        this.members = new ArrayList<>();
        this.owner = owner;
        this.size = size;
        this.center = center;
        this.oldBlockList = new ArrayList<>();
    }

    public boolean isInside(Vector3 c) {
        boolean check0 = Math.abs(a.x - c.x) + Math.abs(b.x - c.x) == Math.abs(a.x - b.x);
        boolean check1 = Math.abs(a.z - c.z) + Math.abs(b.z - c.z) == Math.abs(a.z - b.z);

        return check0 && check1;
    }

    public double Distance(Vector3 position) {
        return Math.sqrt(Math.pow(this.center.x - position.x, 2) + Math.pow(this.center.z - position.z, 2));
    }
}
