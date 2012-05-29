package org.digger;

import lombok.Getter;

class Drawing {

    private Digger dig;

    private int field1[] = {    // [150]
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private int field2[] = {    // [150]
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    @Getter
    private int field[] = {    // [150]
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private short[] diggerbuf = new short[480];
    private short[] bagbuf1 = new short[480];
    private short[] bagbuf2 = new short[480];
    private short[] bagbuf3 = new short[480];
    private short[] bagbuf4 = new short[480];
    private short[] bagbuf5 = new short[480];
    private short[] bagbuf6 = new short[480];
    private short[] bagbuf7 = new short[480];
    private short[] monbuf1 = new short[480];
    private short[] monbuf2 = new short[480];
    private short[] monbuf3 = new short[480];
    private short[] monbuf4 = new short[480];
    private short[] monbuf5 = new short[480];
    private short[] monbuf6 = new short[480];
    private short[] bonusbuf = new short[480];
    private short[] firebuf = new short[128];

    private int bitmasks[] = {0xfffe, 0xfffd, 0xfffb, 0xfff7, 0xffef, 0xffdf, 0xffbf, 0xff7f, 0xfeff, 0xfdff, 0xfbff, 0xf7ff};    // [12]

    private int monspr[] = {0, 0, 0, 0, 0, 0};    // [6]
    private int monspd[] = {0, 0, 0, 0, 0, 0};    // [6]

    private int digspr = 0;
    private int digspd = 0;
    private int firespr = 0;
    private int fireheight = 8;

    Drawing(Digger d) {
        dig = d;
    }

    private void createdbfspr() {
        digspd = 1;
        digspr = 0;
        firespr = 0;
        dig.getSprite().createspr(0, 0, diggerbuf, 4, 15, 0, 0);
        dig.getSprite().createspr(14, 81, bonusbuf, 4, 15, 0, 0);
        dig.getSprite().createspr(15, 82, firebuf, 2, fireheight, 0, 0);
    }

    protected void creatembspr() {
        int i;
        dig.getSprite().createspr(1, 62, bagbuf1, 4, 15, 0, 0);
        dig.getSprite().createspr(2, 62, bagbuf2, 4, 15, 0, 0);
        dig.getSprite().createspr(3, 62, bagbuf3, 4, 15, 0, 0);
        dig.getSprite().createspr(4, 62, bagbuf4, 4, 15, 0, 0);
        dig.getSprite().createspr(5, 62, bagbuf5, 4, 15, 0, 0);
        dig.getSprite().createspr(6, 62, bagbuf6, 4, 15, 0, 0);
        dig.getSprite().createspr(7, 62, bagbuf7, 4, 15, 0, 0);
        dig.getSprite().createspr(8, 71, monbuf1, 4, 15, 0, 0);
        dig.getSprite().createspr(9, 71, monbuf2, 4, 15, 0, 0);
        dig.getSprite().createspr(10, 71, monbuf3, 4, 15, 0, 0);
        dig.getSprite().createspr(11, 71, monbuf4, 4, 15, 0, 0);
        dig.getSprite().createspr(12, 71, monbuf5, 4, 15, 0, 0);
        dig.getSprite().createspr(13, 71, monbuf6, 4, 15, 0, 0);
        createdbfspr();
        for (i = 0; i < 6; i++) {
            monspr[i] = 0;
            monspd[i] = 1;
        }
    }

    private void drawbackg(int l) {
        int x, y;
        for (y = 14; y < 200; y += 4)
            for (x = 0; x < 320; x += 20)
                dig.getSprite().drawmiscspr(x, y, 93 + l, 5, 4);
    }

    protected void drawbonus(int x, int y) {
        dig.getSprite().initspr(14, 81, 4, 15, 0, 0);
        dig.getSprite().movedrawspr(14, x, y);
    }

    protected void drawbottomblob(int x, int y) {
        dig.getSprite().initmiscspr(x - 4, y + 15, 6, 6);
        dig.getSprite().drawmiscspr(x - 4, y + 15, 105, 6, 6);
        dig.getSprite().getis();
    }

    protected int drawdigger(int t, int x, int y, boolean f) {
        digspr += digspd;
        if (digspr == 2 || digspr == 0)
            digspd = -digspd;
        if (digspr > 2)
            digspr = 2;
        if (digspr < 0)
            digspr = 0;
        if (t >= 0 && t <= 6 && !((t & 1) != 0)) {
            dig.getSprite().initspr(0, (t + (f ? 0 : 1)) * 3 + digspr + 1, 4, 15, 0, 0);
            return dig.getSprite().drawspr(0, x, y);
        }
        if (t >= 10 && t <= 15) {
            dig.getSprite().initspr(0, 40 - t, 4, 15, 0, 0);
            return dig.getSprite().drawspr(0, x, y);
        }
        return 0;
    }

    protected void drawemerald(int x, int y) {
        dig.getSprite().initmiscspr(x, y, 4, 10);
        dig.getSprite().drawmiscspr(x, y, 108, 4, 10);
        dig.getSprite().getis();
    }

    private void drawfield() {
        int x, y, xp, yp;
        for (x = 0; x < 15; x++)
            for (y = 0; y < 10; y++)
                if ((field[y * 15 + x] & 0x2000) == 0) {
                    xp = x * 20 + 12;
                    yp = y * 18 + 18;
                    if ((field[y * 15 + x] & 0xfc0) != 0xfc0) {
                        field[y * 15 + x] &= 0xd03f;
                        drawbottomblob(xp, yp - 15);
                        drawbottomblob(xp, yp - 12);
                        drawbottomblob(xp, yp - 9);
                        drawbottomblob(xp, yp - 6);
                        drawbottomblob(xp, yp - 3);
                        drawtopblob(xp, yp + 3);
                    }
                    if ((field[y * 15 + x] & 0x1f) != 0x1f) {
                        field[y * 15 + x] &= 0xdfe0;
                        drawrightblob(xp - 16, yp);
                        drawrightblob(xp - 12, yp);
                        drawrightblob(xp - 8, yp);
                        drawrightblob(xp - 4, yp);
                        drawleftblob(xp + 4, yp);
                    }
                    if (x < 14)
                        if ((field[y * 15 + x + 1] & 0xfdf) != 0xfdf)
                            drawrightblob(xp, yp);
                    if (y < 9)
                        if ((field[(y + 1) * 15 + x] & 0xfdf) != 0xfdf)
                            drawbottomblob(xp, yp);
                }
    }

    protected int drawfire(int x, int y, int t) {
        if (t == 0) {
            firespr++;
            if (firespr > 2)
                firespr = 0;
            dig.getSprite().initspr(15, 82 + firespr, 2, fireheight, 0, 0);
        } else
            dig.getSprite().initspr(15, 84 + t, 2, fireheight, 0, 0);
        return dig.getSprite().drawspr(15, x, y);
    }

    protected void drawfurryblob(int x, int y) {
        dig.getSprite().initmiscspr(x - 4, y + 15, 6, 8);
        dig.getSprite().drawmiscspr(x - 4, y + 15, 107, 6, 8);
        dig.getSprite().getis();
    }

    protected int drawgold(int n, int t, int x, int y) {
        dig.getSprite().initspr(n, t + 62, 4, 15, 0, 0);
        return dig.getSprite().drawspr(n, x, y);
    }

    protected void drawleftblob(int x, int y) {
        dig.getSprite().initmiscspr(x - 8, y - 1, 2, 18);
        dig.getSprite().drawmiscspr(x - 8, y - 1, 104, 2, 18);
        dig.getSprite().getis();
    }

    private void drawlife(int t, int x, int y) {
        dig.getSprite().drawmiscspr(x, y, t + 110, 4, 12);
    }

    protected void drawlives() {
        int l, n;
        n = dig.getMain().getlives(1) - 1;
        for (l = 1; l < 5; l++) {
            drawlife(n > 0 ? 0 : 2, l * 20 + 60, 0);
            n--;
        }
        if (dig.getMain().getNplayers() == 2) {
            n = dig.getMain().getlives(2) - 1;
            for (l = 1; l < 5; l++) {
                drawlife(n > 0 ? 1 : 2, 244 - l * 20, 0);
                n--;
            }
        }
    }

    protected int drawmon(int n, boolean nobf, int dir, int x, int y) {
        monspr[n] += monspd[n];
        if (monspr[n] == 2 || monspr[n] == 0)
            monspd[n] = -monspd[n];
        if (monspr[n] > 2)
            monspr[n] = 2;
        if (monspr[n] < 0)
            monspr[n] = 0;
        if (nobf)
            dig.getSprite().initspr(n + 8, monspr[n] + 69, 4, 15, 0, 0);
        else
            switch (dir) {
                case 0:
                    dig.getSprite().initspr(n + 8, monspr[n] + 73, 4, 15, 0, 0);
                    break;
                case 4:
                    dig.getSprite().initspr(n + 8, monspr[n] + 77, 4, 15, 0, 0);
            }
        return dig.getSprite().drawspr(n + 8, x, y);
    }

    protected int drawmondie(int n, boolean nobf, int dir, int x, int y) {
        if (nobf)
            dig.getSprite().initspr(n + 8, 72, 4, 15, 0, 0);
        else
            switch (dir) {
                case 0:
                    dig.getSprite().initspr(n + 8, 76, 4, 15, 0, 0);
                    break;
                case 4:
                    dig.getSprite().initspr(n + 8, 80, 4, 14, 0, 0);
            }
        return dig.getSprite().drawspr(n + 8, x, y);
    }

    protected void drawrightblob(int x, int y) {
        dig.getSprite().initmiscspr(x + 16, y - 1, 2, 18);
        dig.getSprite().drawmiscspr(x + 16, y - 1, 102, 2, 18);
        dig.getSprite().getis();
    }

    protected void drawsquareblob(int x, int y) {
        dig.getSprite().initmiscspr(x - 4, y + 17, 6, 6);
        dig.getSprite().drawmiscspr(x - 4, y + 17, 106, 6, 6);
        dig.getSprite().getis();
    }

    protected void drawstatics() {
        int x, y;
        for (x = 0; x < 15; x++)
            for (y = 0; y < 10; y++)
                if (dig.getMain().getcplayer() == 0)
                    field[y * 15 + x] = field1[y * 15 + x];
                else
                    field[y * 15 + x] = field2[y * 15 + x];
        dig.getSprite().setretr(true);
        dig.getPc().gpal(0);
        dig.getPc().ginten(0);
        drawbackg(dig.getMain().levplan());
        drawfield();
        dig.getPc().getCurrentSource().newPixels(0, 0, dig.getPc().getWidth(), dig.getPc().getHeight());
    }

    protected void drawtopblob(int x, int y) {
        dig.getSprite().initmiscspr(x - 4, y - 6, 6, 6);
        dig.getSprite().drawmiscspr(x - 4, y - 6, 103, 6, 6);
        dig.getSprite().getis();
    }

    protected void eatfield(int x, int y, int dir) {
        int h = (x - 12) / 20, xr = ((x - 12) % 20) / 4, v = (y - 18) / 18, yr = ((y - 18) % 18) / 3;
        dig.getMain().increasePenalty();
        switch (dir) {
            case 0:
                h++;
                field[v * 15 + h] &= bitmasks[xr];
                if ((field[v * 15 + h] & 0x1f) != 0)
                    break;
                field[v * 15 + h] &= 0xdfff;
                break;
            case 4:
                xr--;
                if (xr < 0) {
                    xr += 5;
                    h--;
                }
                field[v * 15 + h] &= bitmasks[xr];
                if ((field[v * 15 + h] & 0x1f) != 0)
                    break;
                field[v * 15 + h] &= 0xdfff;
                break;
            case 2:
                yr--;
                if (yr < 0) {
                    yr += 6;
                    v--;
                }
                field[v * 15 + h] &= bitmasks[6 + yr];
                if ((field[v * 15 + h] & 0xfc0) != 0)
                    break;
                field[v * 15 + h] &= 0xdfff;
                break;
            case 6:
                v++;
                field[v * 15 + h] &= bitmasks[6 + yr];
                if ((field[v * 15 + h] & 0xfc0) != 0)
                    break;
                field[v * 15 + h] &= 0xdfff;
        }
    }

    protected void eraseemerald(int x, int y) {
        dig.getSprite().initmiscspr(x, y, 4, 10);
        dig.getSprite().drawmiscspr(x, y, 109, 4, 10);
        dig.getSprite().getis();
    }

    private void initdbfspr() {
        digspd = 1;
        digspr = 0;
        firespr = 0;
        dig.getSprite().initspr(0, 0, 4, 15, 0, 0);
        dig.getSprite().initspr(14, 81, 4, 15, 0, 0);
        dig.getSprite().initspr(15, 82, 2, fireheight, 0, 0);
    }

    protected void initmbspr() {
        dig.getSprite().initspr(1, 62, 4, 15, 0, 0);
        dig.getSprite().initspr(2, 62, 4, 15, 0, 0);
        dig.getSprite().initspr(3, 62, 4, 15, 0, 0);
        dig.getSprite().initspr(4, 62, 4, 15, 0, 0);
        dig.getSprite().initspr(5, 62, 4, 15, 0, 0);
        dig.getSprite().initspr(6, 62, 4, 15, 0, 0);
        dig.getSprite().initspr(7, 62, 4, 15, 0, 0);
        dig.getSprite().initspr(8, 71, 4, 15, 0, 0);
        dig.getSprite().initspr(9, 71, 4, 15, 0, 0);
        dig.getSprite().initspr(10, 71, 4, 15, 0, 0);
        dig.getSprite().initspr(11, 71, 4, 15, 0, 0);
        dig.getSprite().initspr(12, 71, 4, 15, 0, 0);
        dig.getSprite().initspr(13, 71, 4, 15, 0, 0);
        initdbfspr();
    }

    protected void makefield() {
        int c, x, y;
        for (x = 0; x < 15; x++)
            for (y = 0; y < 10; y++) {
                field[y * 15 + x] = -1;
                c = dig.getMain().getlevch(x, y, dig.getMain().levplan());
                if (c == 'S' || c == 'V')
                    field[y * 15 + x] &= 0xd03f;
                if (c == 'S' || c == 'H')
                    field[y * 15 + x] &= 0xdfe0;
                if (dig.getMain().getcplayer() == 0)
                    field1[y * 15 + x] = field[y * 15 + x];
                else
                    field2[y * 15 + x] = field[y * 15 + x];
            }
    }

    protected void outtext(String p, int x, int y, int c) {
        outtext(p, x, y, c, false);
    }

    protected void outtext(String p, int x, int y, int c, boolean b) {
        int i, rx = x;
        for (i = 0; i < p.length(); i++) {
            dig.getPc().gwrite(x, y, p.charAt(i), c);
            x += 12;
        }
        if (b)
            dig.getPc().getCurrentSource().newPixels(rx, y, p.length() * 12, 12);
    }

    protected void savefield() {
        int x, y;
        for (x = 0; x < 15; x++)
            for (y = 0; y < 10; y++)
                if (dig.getMain().getcplayer() == 0)
                    field1[y * 15 + x] = field[y * 15 + x];
                else
                    field2[y * 15 + x] = field[y * 15 + x];
    }
}