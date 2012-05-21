package org.digger;

class Bags {

    private Digger dig;

    private _bag[] bagdat1 = {new _bag(), new _bag(), new _bag(), new _bag(), new _bag(), new _bag(), new _bag(), new _bag()};
    private _bag[] bagdat2 = {new _bag(), new _bag(), new _bag(), new _bag(), new _bag(), new _bag(), new _bag(), new _bag()};
    private _bag[] bagdat = {new _bag(), new _bag(), new _bag(), new _bag(), new _bag(), new _bag(), new _bag(), new _bag()};

    private int pushcount = 0;
    private int goldtime = 0;

    private int wblanim[] = {2, 0, 1, 0};    // [4]

    Bags(Digger d) {
        dig = d;
    }

    protected int bagbits() {
        int bag, b, bags = 0;
        for (bag = 1, b = 2; bag < 8; bag++, b <<= 1)
            if (bagdat[bag].isExist())
                bags |= b;
        return bags;
    }

    private void baghitground(int bag) {
        int bn, b, clbits;
        if (bagdat[bag].getDir() == 6 && bagdat[bag].getFallh() > 1)
            bagdat[bag].setGt(1);
        else
            bagdat[bag].setFallh(0);
        bagdat[bag].setDir(-1);
        bagdat[bag].setWt(15);
        bagdat[bag].setWobbling(false);
        clbits = dig.getDrawing().drawgold(bag, 0, bagdat[bag].getX(), bagdat[bag].getY());
        dig.getMain().incpenalty();
        for (bn = 1, b = 2; bn < 8; bn++, b <<= 1)
            if ((b & clbits) != 0)
                removebag(bn);
    }

    protected int bagy(int bag) {
        return bagdat[bag].getY();
    }

    protected void cleanupbags() {
        int bpa;
        dig.getSound().soundfalloff();
        for (bpa = 1; bpa < 8; bpa++) {
            if (bagdat[bpa].isExist() && ((bagdat[bpa].getH() == 7 && bagdat[bpa].getV() == 9) ||
                    bagdat[bpa].getXr() != 0 || bagdat[bpa].getYr() != 0 || bagdat[bpa].getGt() != 0 ||
                    bagdat[bpa].getFallh() != 0 || bagdat[bpa].isWobbling())) {
                bagdat[bpa].setExist(false);
                dig.getSprite().erasespr(bpa);
            }
            if (dig.getMain().getcplayer() == 0)
                bagdat1[bpa].copyFrom(bagdat[bpa]);
            else
                bagdat2[bpa].copyFrom(bagdat[bpa]);
        }
    }

    protected void dobags() {
        int bag;
        boolean soundfalloffflag = true, soundwobbleoffflag = true;
        for (bag = 1; bag < 8; bag++)
            if (bagdat[bag].exist) {
                if (bagdat[bag].gt != 0) {
                    if (bagdat[bag].gt == 1) {
                        dig.getSound().soundbreak();
                        dig.getDrawing().drawgold(bag, 4, bagdat[bag].x, bagdat[bag].y);
                        dig.getMain().incpenalty();
                    }
                    if (bagdat[bag].gt == 3) {
                        dig.getDrawing().drawgold(bag, 5, bagdat[bag].x, bagdat[bag].y);
                        dig.getMain().incpenalty();
                    }
                    if (bagdat[bag].gt == 5) {
                        dig.getDrawing().drawgold(bag, 6, bagdat[bag].x, bagdat[bag].y);
                        dig.getMain().incpenalty();
                    }
                    bagdat[bag].gt++;
                    if (bagdat[bag].gt == goldtime)
                        removebag(bag);
                    else if (bagdat[bag].v < 9 && bagdat[bag].gt < goldtime - 10)
                        if ((dig.getMonster().getfield(bagdat[bag].h, bagdat[bag].v + 1) & 0x2000) == 0)
                            bagdat[bag].gt = goldtime - 10;
                } else
                    updatebag(bag);
            }
        for (bag = 1; bag < 8; bag++) {
            if (bagdat[bag].dir == 6 && bagdat[bag].exist)
                soundfalloffflag = false;
            if (bagdat[bag].dir != 6 && bagdat[bag].wobbling && bagdat[bag].exist)
                soundwobbleoffflag = false;
        }
        if (soundfalloffflag)
            dig.getSound().soundfalloff();
        if (soundwobbleoffflag)
            dig.getSound().soundwobbleoff();
    }

    protected void drawbags() {
        int bag;
        for (bag = 1; bag < 8; bag++) {
            if (dig.getMain().getcplayer() == 0)
                bagdat[bag].copyFrom(bagdat1[bag]);
            else
                bagdat[bag].copyFrom(bagdat2[bag]);
            if (bagdat[bag].exist)
                dig.getSprite().movedrawspr(bag, bagdat[bag].x, bagdat[bag].y);
        }
    }

    protected int getbagdir(int bag) {
        if (bagdat[bag].exist)
            return bagdat[bag].dir;
        return -1;
    }

    private void getgold(int bag) {
        int clbits;
        clbits = dig.getDrawing().drawgold(bag, 6, bagdat[bag].x, bagdat[bag].y);
        dig.getMain().incpenalty();
        if ((clbits & 1) != 0) {
            dig.getScores().scoregold();
            dig.getSound().soundgold();
            dig.setDigtime(0);
        } else
            dig.getMonster().mongold();
        removebag(bag);
    }

    protected int getnmovingbags() {
        int bag, n = 0;
        for (bag = 1; bag < 8; bag++)
            if (bagdat[bag].exist && bagdat[bag].gt < 10 &&
                    (bagdat[bag].gt != 0 || bagdat[bag].wobbling))
                n++;
        return n;
    }

    protected void initbags() {
        int bag, x, y;
        pushcount = 0;
        goldtime = 150 - dig.getMain().levof10() * 10;
        for (bag = 1; bag < 8; bag++)
            bagdat[bag].exist = false;
        bag = 1;
        for (x = 0; x < 15; x++)
            for (y = 0; y < 10; y++)
                if (dig.getMain().getlevch(x, y, dig.getMain().levplan()) == 'B')
                    if (bag < 8) {
                        bagdat[bag].exist = true;
                        bagdat[bag].gt = 0;
                        bagdat[bag].fallh = 0;
                        bagdat[bag].dir = -1;
                        bagdat[bag].wobbling = false;
                        bagdat[bag].wt = 15;
                        bagdat[bag].unfallen = true;
                        bagdat[bag].x = x * 20 + 12;
                        bagdat[bag].y = y * 18 + 18;
                        bagdat[bag].h = x;
                        bagdat[bag].v = y;
                        bagdat[bag].xr = 0;
                        bagdat[bag++].yr = 0;
                    }
        if (dig.getMain().getcplayer() == 0)
            for (int i = 1; i < 8; i++)
                bagdat1[i].copyFrom(bagdat[i]);
        else
            for (int i = 1; i < 8; i++)
                bagdat2[i].copyFrom(bagdat[i]);
    }

    private boolean pushbag(int bag, int dir) {
        int x, y, h, v, ox, oy, clbits;
        boolean push = true;
        ox = x = bagdat[bag].x;
        oy = y = bagdat[bag].y;
        h = bagdat[bag].h;
        v = bagdat[bag].v;
        if (bagdat[bag].gt != 0) {
            getgold(bag);
            return true;
        }
        if (bagdat[bag].dir == 6 && (dir == 4 || dir == 0)) {
            clbits = dig.getDrawing().drawgold(bag, 3, x, y);
            dig.getMain().incpenalty();
            if (((clbits & 1) != 0) && (dig.getDiggery() >= y))
                dig.killdigger(1, bag);
            if ((clbits & 0x3f00) != 0)
                dig.getMonster().squashmonsters(bag, clbits);
            return true;
        }
        if ((x == 292 && dir == 0) || (x == 12 && dir == 4) || (y == 180 && dir == 6) ||
                (y == 18 && dir == 2))
            push = false;
        if (push) {
            switch (dir) {
                case 0:
                    x += 4;
                    break;
                case 4:
                    x -= 4;
                    break;
                case 6:
                    if (bagdat[bag].unfallen) {
                        bagdat[bag].unfallen = false;
                        dig.getDrawing().drawsquareblob(x, y);
                        dig.getDrawing().drawtopblob(x, y + 21);
                    } else
                        dig.getDrawing().drawfurryblob(x, y);
                    dig.getDrawing().eatfield(x, y, dir);
                    dig.killemerald(h, v);
                    y += 6;
            }
            switch (dir) {
                case 6:
                    clbits = dig.getDrawing().drawgold(bag, 3, x, y);
                    dig.getMain().incpenalty();
                    if (((clbits & 1) != 0) && dig.getDiggery() >= y)
                        dig.killdigger(1, bag);
                    if ((clbits & 0x3f00) != 0)
                        dig.getMonster().squashmonsters(bag, clbits);
                    break;
                case 0:
                case 4:
                    bagdat[bag].wt = 15;
                    bagdat[bag].wobbling = false;
                    clbits = dig.getDrawing().drawgold(bag, 0, x, y);
                    dig.getMain().incpenalty();
                    pushcount = 1;
                    if ((clbits & 0xfe) != 0)
                        if (!pushbags(dir, clbits)) {
                            x = ox;
                            y = oy;
                            dig.getDrawing().drawgold(bag, 0, ox, oy);
                            dig.getMain().incpenalty();
                            push = false;
                        }
                    if (((clbits & 1) != 0) || ((clbits & 0x3f00) != 0)) {
                        x = ox;
                        y = oy;
                        dig.getDrawing().drawgold(bag, 0, ox, oy);
                        dig.getMain().incpenalty();
                        push = false;
                    }
            }
            if (push)
                bagdat[bag].dir = dir;
            else
                bagdat[bag].dir = dig.reversedir(dir);
            bagdat[bag].x = x;
            bagdat[bag].y = y;
            bagdat[bag].h = (x - 12) / 20;
            bagdat[bag].v = (y - 18) / 18;
            bagdat[bag].xr = (x - 12) % 20;
            bagdat[bag].yr = (y - 18) % 18;
        }
        return push;
    }

    protected boolean pushbags(int dir, int bits) {
        int bag, bit;
        boolean push = true;
        for (bag = 1, bit = 2; bag < 8; bag++, bit <<= 1)
            if ((bits & bit) != 0)
                if (!pushbag(bag, dir))
                    push = false;
        return push;
    }

    protected boolean pushudbags(int bits) {
        int bag, b;
        boolean push = true;
        for (bag = 1, b = 2; bag < 8; bag++, b <<= 1)
            if ((bits & b) != 0)
                if (bagdat[bag].gt != 0)
                    getgold(bag);
                else
                    push = false;
        return push;
    }

    private void removebag(int bag) {
        if (bagdat[bag].exist) {
            bagdat[bag].exist = false;
            dig.getSprite().erasespr(bag);
        }
    }

    protected void removebags(int bits) {
        int bag, b;
        for (bag = 1, b = 2; bag < 8; bag++, b <<= 1)
            if ((bagdat[bag].exist) && ((bits & b) != 0))
                removebag(bag);
    }

    private void updatebag(int bag) {
        int x, h, xr, y, v, yr, wbl;
        x = bagdat[bag].x;
        h = bagdat[bag].h;
        xr = bagdat[bag].xr;
        y = bagdat[bag].y;
        v = bagdat[bag].v;
        yr = bagdat[bag].yr;
        switch (bagdat[bag].dir) {
            case -1:
                if (y < 180 && xr == 0) {
                    if (bagdat[bag].wobbling) {
                        if (bagdat[bag].wt == 0) {
                            bagdat[bag].dir = 6;
                            dig.getSound().soundfall();
                            break;
                        }
                        bagdat[bag].wt--;
                        wbl = bagdat[bag].wt % 8;
                        if (!((wbl & 1) != 0)) {
                            dig.getDrawing().drawgold(bag, wblanim[wbl >> 1], x, y);
                            dig.getMain().incpenalty();
                            dig.getSound().soundwobble();
                        }
                    } else if ((dig.getMonster().getfield(h, v + 1) & 0xfdf) != 0xfdf)
                        if (!dig.checkdiggerunderbag(h, v + 1))
                            bagdat[bag].wobbling = true;
                } else {
                    bagdat[bag].wt = 15;
                    bagdat[bag].wobbling = false;
                }
                break;
            case 0:
            case 4:
                if (xr == 0)
                    if (y < 180 && (dig.getMonster().getfield(h, v + 1) & 0xfdf) != 0xfdf) {
                        bagdat[bag].dir = 6;
                        bagdat[bag].wt = 0;
                        dig.getSound().soundfall();
                    } else
                        baghitground(bag);
                break;
            case 6:
                if (yr == 0)
                    bagdat[bag].fallh++;
                if (y >= 180)
                    baghitground(bag);
                else if ((dig.getMonster().getfield(h, v + 1) & 0xfdf) == 0xfdf)
                    if (yr == 0)
                        baghitground(bag);
                dig.getMonster().checkmonscared(bagdat[bag].h);
        }
        if (bagdat[bag].dir != -1)
            if (bagdat[bag].dir != 6 && pushcount != 0)
                pushcount--;
            else
                pushbag(bag, bagdat[bag].dir);
    }
}