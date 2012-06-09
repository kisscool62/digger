package org.digger;

import lombok.Getter;
import lombok.Setter;

@Getter
class _monster {
    private int x;

    @Setter
    private int y;

    @Setter
    private int h;

    @Setter
    private int v;

    @Setter
    private int xr;

    @Setter
    private int yr;

    @Setter
    private int dir;

    @Setter
    private int hdir;

    @Setter
    private int t;

    @Setter
    private int hnt; //hobbin counter

    @Setter
    private int death;

    @Setter
    private int bag;

    @Setter
    private int dtime;
    private int stime;

    @Setter
    private boolean flag;

    @Setter
    private boolean nob;

    @Setter
    private boolean alive;


    public void create() {
        flag = true;
        alive = true;
        t = 0;
        nob = true;
        hnt = 0;
        h = 14;
        v = 0;
        x = 292;
        y = 18;
        xr = 0;
        yr = 0;
        dir = 4;
        hdir = 4;
        stime = 5;
    }

    public void increaseHobbinCounter() {
        hnt++;
    }

    protected void decreaseT() {
        t--;
    }

    protected void increaseT() {
        t++;
    }

    protected void goUp() {
        y -= 3;
    }

    protected void goDown() {
        y += 3;
    }

    protected void goLeft() {
        x -= 4;
    }

    protected void goRight() {
        x += 4;
    }

    public void dontMoveFrom(int monox, int monoy) {
        x = monox;
        y = monoy;
    }

    public void decreaseStime() {
        stime--;
    }

    public void decreaseDtime() {
        dtime--;
    }
}