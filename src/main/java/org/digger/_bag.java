package org.digger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class _bag {
    private int x;
    private int y;
    private int h;
    private int v;
    private int xr;
    private int yr;
    private int dir;
    private int wt;
    private int gt;
    private int fallh;
    private boolean wobbling;
    private boolean unfallen;
    private boolean exist;

    void copyFrom(_bag t) {
        x = t.x;
        y = t.y;
        h = t.h;
        v = t.v;
        xr = t.xr;
        yr = t.yr;
        dir = t.dir;
        wt = t.wt;
        gt = t.gt;
        fallh = t.fallh;
        wobbling = t.wobbling;
        unfallen = t.unfallen;
        exist = t.exist;
    }
}