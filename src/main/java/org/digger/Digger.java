package org.digger;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

public class Digger extends java.applet.Applet implements Runnable {

    public static final int MAX_RATE = 200;
    public static final int MIN_RATE = 40;

    @Getter
    @Setter
    private int frametime = 66;

    @Getter
    private boolean digonscr = false;

    @Setter
    private boolean bonusvisible = false;

    @Getter
    private boolean bonusmode = false;

    @Getter
    private String subaddr;

    @Getter
    private Bags bags;

    @Getter
    private Main main;

    @Getter
    private Sound sound;

    @Getter
    private Monster monster;

    @Getter
    private Scores scores;

    @Getter
    private Sprite sprite;

    @Getter
    private Drawing drawing;

    @Getter
    private Input input;

    @Getter
    private Pc pc;

    @Getter
    private int diggerx = 0;

    @Getter
    private int diggery = 0;

    @Setter
    private int digtime = 0;

    @Getter
    private int eatmsc = 0;

    @Setter
    private long time = 50;

    @Setter
    private long ftime = 50;

    private int diggerh = 0;
    private int diggerv = 0;
    private int diggerrx = 0;
    private int diggerry = 0;
    private int digmdir = 0;

    private int digdir = 0;

    private int width = 320;
    private int height = 200;

    private int rechargetime = 0;
    private int firex = 0;
    private int firey = 0;
    private int firedir = 0;
    private int expsn = 0;
    private int deathstage = 0;
    private int deathbag = 0;
    private int deathani = 0;
    private int deathtime = 0;
    private int startbonustimeleft = 0;
    private int bonustimeleft = 0;

    private int emocttime = 0;

    private int emmask = 0;

    private Thread gamethread;

    private Image pic;
    private Graphics picg;

    private boolean notfiring = false;

    private byte emfield[] = {    //[150]
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

    private boolean diggervisible = false;

    private int embox[] = {8, 12, 12, 9, 16, 12, 6, 9};    // [8]
    private int deatharc[] = {3, 5, 6, 6, 5, 3, 0};            // [7]

    public Digger() {
        bags = new Bags(this);
        main = new Main(this);
        sound = new Sound(this);
        monster = new Monster(this);
        scores = new Scores(this);
        sprite = new Sprite(this);
        drawing = new Drawing(this);
        input = new Input(this);
        pc = new Pc(this);
    }

    public void shiftEatmsc() {
        eatmsc <<= 1;
    }

    public boolean checkdiggerunderbag(int h, int v) {
        if (digmdir == 2 || digmdir == 6)
            if ((diggerx - 12) / 20 == h)
                if ((diggery - 18) / 18 == v || (diggery - 18) / 18 + 1 == v)
                    return true;
        return false;
    }

    public int countem() {
        int x, y, n = 0;
        for (x = 0; x < 15; x++)
            for (y = 0; y < 10; y++)
                if ((emfield[y * 15 + x] & emmask) != 0)
                    n++;
        return n;
    }

    public void createbonus() {
        bonusvisible = true;
        drawing.drawbonus(292, 18);
    }

    public void destroy() {
        if (gamethread != null)
            gamethread.stop();
    }

    private void diggerdie() {
        int clbits;
        switch (deathstage) {
            case 1:
                if (bags.bagy(deathbag) + 6 > diggery)
                    diggery = bags.bagy(deathbag) + 6;
                drawing.drawdigger(15, diggerx, diggery, false);
                main.increasePenalty();
                if (bags.getbagdir(deathbag) + 1 == 0) {
                    sound.soundddie();
                    deathtime = 5;
                    deathstage = 2;
                    deathani = 0;
                    diggery -= 6;
                }
                break;
            case 2:
                if (deathtime != 0) {
                    deathtime--;
                    break;
                }
                if (deathani == 0)
                    sound.music(2);
                clbits = drawing.drawdigger(14 - deathani, diggerx, diggery, false);
                main.increasePenalty();
                if (deathani == 0 && ((clbits & 0x3f00) != 0))
                    monster.killmonsters(clbits);
                if (deathani < 4) {
                    deathani++;
                    deathtime = 2;
                } else {
                    deathstage = 4;
                    if (sound.isMusicflag())
                        deathtime = 60;
                    else
                        deathtime = 10;
                }
                break;
            case 3:
                deathstage = 5;
                deathani = 0;
                deathtime = 0;
                break;
            case 5:
                if (deathani >= 0 && deathani <= 6) {
                    drawing.drawdigger(15, diggerx, diggery - deatharc[deathani], false);
                    if (deathani == 6)
                        sound.musicoff();
                    main.increasePenalty();
                    deathani++;
                    if (deathani == 1)
                        sound.soundddie();
                    if (deathani == 7) {
                        deathtime = 5;
                        deathani = 0;
                        deathstage = 2;
                    }
                }
                break;
            case 4:
                if (deathtime != 0)
                    deathtime--;
                else
                    main.setdead(true);
        }
    }

    public void dodigger() {
        newframe();
        if (expsn != 0)
            drawexplosion();
        else
            updatefire();
        if (diggervisible)
            if (digonscr)
                if (digtime != 0) {
                    drawing.drawdigger(digmdir, diggerx, diggery, notfiring && rechargetime == 0);
                    main.increasePenalty();
                    digtime--;
                } else
                    updatedigger();
            else
                diggerdie();
        if (bonusmode && digonscr) {
            if (bonustimeleft != 0) {
                bonustimeleft--;
                if (startbonustimeleft != 0 || bonustimeleft < 20) {
                    startbonustimeleft--;
                    if ((bonustimeleft & 1) != 0) {
                        pc.ginten(0);
                        sound.soundbonus();
                    } else {
                        pc.ginten(1);
                        sound.soundbonus();
                    }
                    if (startbonustimeleft == 0) {
                        sound.music(0);
                        sound.soundbonusoff();
                        pc.ginten(1);
                    }
                }
            } else {
                endbonusmode();
                sound.soundbonusoff();
                sound.music(1);
            }
        }
        if (bonusmode && !digonscr) {
            endbonusmode();
            sound.soundbonusoff();
            sound.music(1);
        }
        if (emocttime > 0)
            emocttime--;
    }

    public void drawemeralds() {
        int x, y;
        emmask = 1 << main.getcplayer();
        for (x = 0; x < 15; x++)
            for (y = 0; y < 10; y++)
                if ((emfield[y * 15 + x] & emmask) != 0)
                    drawing.drawemerald(x * 20 + 12, y * 18 + 21);
    }

    private void drawexplosion() {
        switch (expsn) {
            case 1:
                sound.soundexplode();
            case 2:
            case 3:
                drawing.drawfire(firex, firey, expsn);
                main.increasePenalty();
                expsn++;
                break;
            default:
                killfire();
                expsn = 0;
        }
    }

    private void endbonusmode() {
        bonusmode = false;
        pc.ginten(0);
    }

    public void erasebonus() {
        if (bonusvisible) {
            bonusvisible = false;
            sprite.erasespr(14);
        }
        pc.ginten(0);
    }

    public void erasedigger() {
        sprite.erasespr(0);
        diggervisible = false;
    }

    public String getAppletInfo() {
        return "The Digger Remastered -- http://www.digger.org, Copyright (c) Andrew Jenner & Marek Futrega / MAF";
    }

    private boolean getfirepflag() {
        return input.isFirepflag();
    }

    public boolean hitemerald(int x, int y, int rx, int ry, int dir) {
        boolean hit = false;
        int r;
        if (dir < 0 || dir > 6 || ((dir & 1) != 0))
            return hit;
        if (dir == 0 && rx != 0)
            x++;
        if (dir == 6 && ry != 0)
            y++;
        if (dir == 0 || dir == 4)
            r = rx;
        else
            r = ry;
        if ((emfield[y * 15 + x] & emmask) != 0) {
            if (r == embox[dir]) {
                drawing.drawemerald(x * 20 + 12, y * 18 + 21);
                main.increasePenalty();
            }
            if (r == embox[dir + 1]) {
                drawing.eraseemerald(x * 20 + 12, y * 18 + 21);
                main.increasePenalty();
                hit = true;
                emfield[y * 15 + x] &= ~emmask;
            }
        }
        return hit;
    }

    public void init() {

        if (gamethread != null)
            gamethread.stop();

        subaddr = "null";//getParameter("submit");

        try {
            frametime = Integer.parseInt(getParameter("speed"));
            if (frametime > MAX_RATE)
                frametime = MAX_RATE;
            else if (frametime < MIN_RATE)
                frametime = MIN_RATE;
        } catch (Exception e) {
        }

        pc.setPixels(new int[65536]);

        for (int i = 0; i < 2; i++) {
            pc.getSource()[i] = new MemoryImageSource(pc.getWidth(), pc.getHeight(), new IndexColorModel(8, 4, pc.getPal()[i][0], pc.getPal()[i][1], pc.getPal()[i][2]), pc.getPixels(), 0, pc.getWidth());
            pc.getSource()[i].setAnimated(true);
            pc.getImage()[i] = createImage(pc.getSource()[i]);
            pc.getSource()[i].newPixels();
        }

        pc.setCurrentImage(pc.getImage()[0]);
        pc.setCurrentSource(pc.getSource()[0]);

        gamethread = new Thread(this);
        gamethread.start();

    }

    private void initbonusmode() {
        bonusmode = true;
        erasebonus();
        pc.ginten(1);
        bonustimeleft = 250 - main.levof10() * 20;
        startbonustimeleft = 20;
        eatmsc = 1;
    }

    public void initdigger() {
        diggerv = 9;
        digmdir = 4;
        diggerh = 7;
        diggerx = diggerh * 20 + 12;
        digdir = 0;
        diggerrx = 0;
        diggerry = 0;
        digtime = 0;
        digonscr = true;
        deathstage = 1;
        diggervisible = true;
        diggery = diggerv * 18 + 18;
        sprite.movedrawspr(0, diggerx, diggery);
        notfiring = true;
        emocttime = 0;
        bonusvisible = bonusmode = false;
        input.setFirepressed(false);
        expsn = 0;
        rechargetime = 0;
    }

    public boolean keyDown(Event e, int key) {
        switch (key) {
            case 1006:
                input.processkey(0x4b);
                break;
            case 1007:
                input.processkey(0x4d);
                break;
            case 1004:
                input.processkey(0x48);
                break;
            case 1005:
                input.processkey(0x50);
                break;
            case 1008:
                input.processkey(0x3b);
                break;
            default:
                key &= 0x7f;
                if ((key >= 65) && (key <= 90))
                    key += (97 - 65);
                input.processkey(key);
                break;
        }
        return true;
    }

    public boolean keyUp(Event e, int key) {
        switch (key) {
            case 1006:
                input.processkey(0xcb);
                break;
            case 1007:
                input.processkey(0xcd);
                break;
            case 1004:
                input.processkey(0xc8);
                break;
            case 1005:
                input.processkey(0xd0);
                break;
            case 1008:
                input.processkey(0xbb);
                break;
            default:
                key &= 0x7f;
                if ((key >= 65) && (key <= 90))
                    key += (97 - 65);
                input.processkey(0x80 | key);
                break;
        }
        return true;
    }

    public void killdigger(int stage, int bag) {
        if (deathstage < 2 || deathstage > 4) {
            digonscr = false;
            deathstage = stage;
            deathbag = bag;
        }
    }

    public void killemerald(int x, int y) {
        if ((emfield[y * 15 + x + 15] & emmask) != 0) {
            emfield[y * 15 + x + 15] &= ~emmask;
            drawing.eraseemerald(x * 20 + 12, (y + 1) * 18 + 21);
        }
    }

    public void killfire() {
        if (!notfiring) {
            notfiring = true;
            sprite.erasespr(15);
            sound.soundfireoff();
        }
    }

    public void makeemfield() {
        int x, y;
        emmask = 1 << main.getcplayer();
        for (x = 0; x < 15; x++)
            for (y = 0; y < 10; y++)
                if (main.getlevch(x, y, main.levplan()) == 'C')
                    emfield[y * 15 + x] |= emmask;
                else
                    emfield[y * 15 + x] &= ~emmask;
    }

    public void newframe() {
        input.checkkeyb();
        time += frametime;
        long l = time - pc.gethrt();
        if (l > 0) {
            try {
                Thread.sleep((int) l);
            } catch (Exception e) {
            }
        }
        pc.getCurrentSource().newPixels();
    }

    public void paint(Graphics g) {
        update(g);
    }

    public int reversedir(int dir) {
        switch (dir) {
            case 0:
                return 4;
            case 4:
                return 0;
            case 2:
                return 6;
            case 6:
                return 2;
        }
        return dir;
    }

    public void run() {
        main.main();
    }

    public void start() {
        requestFocus();
    }

    public void update(Graphics g) {
        g.drawImage(pc.getCurrentImage(), 0, 0, this);
    }

    private void updatedigger() {
        int dir, ddir, clbits, diggerox, diggeroy, nmon;
        boolean push = false;
        input.readdir();
        dir = input.getdir();
        if (dir == 0 || dir == 2 || dir == 4 || dir == 6)
            ddir = dir;
        else
            ddir = -1;
        if (diggerrx == 0 && (ddir == 2 || ddir == 6))
            digdir = digmdir = ddir;
        if (diggerry == 0 && (ddir == 4 || ddir == 0))
            digdir = digmdir = ddir;
        if (dir == -1)
            digmdir = -1;
        else
            digmdir = digdir;
        if ((diggerx == 292 && digmdir == 0) || (diggerx == 12 && digmdir == 4) ||
                (diggery == 180 && digmdir == 6) || (diggery == 18 && digmdir == 2))
            digmdir = -1;
        diggerox = diggerx;
        diggeroy = diggery;
        if (digmdir != -1)
            drawing.eatfield(diggerox, diggeroy, digmdir);
        switch (digmdir) {
            case 0:
                drawing.drawrightblob(diggerx, diggery);
                diggerx += 4;
                break;
            case 4:
                drawing.drawleftblob(diggerx, diggery);
                diggerx -= 4;
                break;
            case 2:
                drawing.drawtopblob(diggerx, diggery);
                diggery -= 3;
                break;
            case 6:
                drawing.drawbottomblob(diggerx, diggery);
                diggery += 3;
                break;
        }
        if (hitemerald((diggerx - 12) / 20, (diggery - 18) / 18, (diggerx - 12) % 20,
                (diggery - 18) % 18, digmdir)) {
            scores.scoreemerald();
            sound.soundem();
            sound.soundemerald(emocttime);
            emocttime = 9;
        }
        clbits = drawing.drawdigger(digdir, diggerx, diggery, notfiring && rechargetime == 0);
        main.increasePenalty();
        if ((bags.bagbits() & clbits) != 0) {
            if (digmdir == 0 || digmdir == 4) {
                push = bags.pushbags(digmdir, clbits);
                digtime++;
            } else if (!bags.pushudbags(clbits))
                push = false;
            if (!push) { /* Strange, push not completely defined */
                diggerx = diggerox;
                diggery = diggeroy;
                drawing.drawdigger(digmdir, diggerx, diggery, notfiring && rechargetime == 0);
                main.increasePenalty();
                digdir = reversedir(digmdir);
            }
        }
        if (((clbits & 0x3f00) != 0) && bonusmode)
            for (nmon = monster.killmonsters(clbits); nmon != 0; nmon--) {
                sound.soundeatm();
                scores.scoreeatm();
            }
        if ((clbits & 0x4000) != 0) {
            scores.scorebonus();
            initbonusmode();
        }
        diggerh = (diggerx - 12) / 20;
        diggerrx = (diggerx - 12) % 20;
        diggerv = (diggery - 18) / 18;
        diggerry = (diggery - 18) % 18;
    }

    private void updatefire() {
        int clbits, b, mon, pix = 0;
        if (notfiring) {
            if (rechargetime != 0)
                rechargetime--;
            else if (getfirepflag())
                if (digonscr) {
                    rechargetime = main.levof10() * 3 + 60;
                    notfiring = false;
                    switch (digdir) {
                        case 0:
                            firex = diggerx + 8;
                            firey = diggery + 4;
                            break;
                        case 4:
                            firex = diggerx;
                            firey = diggery + 4;
                            break;
                        case 2:
                            firex = diggerx + 4;
                            firey = diggery;
                            break;
                        case 6:
                            firex = diggerx + 4;
                            firey = diggery + 8;
                    }
                    firedir = digdir;
                    sprite.movedrawspr(15, firex, firey);
                    sound.soundfire();
                }
        } else {
            switch (firedir) {
                case 0:
                    firex += 8;
                    pix = pc.ggetpix(firex, firey + 4) | pc.ggetpix(firex + 4, firey + 4);
                    break;
                case 4:
                    firex -= 8;
                    pix = pc.ggetpix(firex, firey + 4) | pc.ggetpix(firex + 4, firey + 4);
                    break;
                case 2:
                    firey -= 7;
                    pix = (pc.ggetpix(firex + 4, firey) | pc.ggetpix(firex + 4, firey + 1) |
                            pc.ggetpix(firex + 4, firey + 2) | pc.ggetpix(firex + 4, firey + 3) |
                            pc.ggetpix(firex + 4, firey + 4) | pc.ggetpix(firex + 4, firey + 5) |
                            pc.ggetpix(firex + 4, firey + 6)) & 0xc0;
                    break;
                case 6:
                    firey += 7;
                    pix = (pc.ggetpix(firex, firey) | pc.ggetpix(firex, firey + 1) |
                            pc.ggetpix(firex, firey + 2) | pc.ggetpix(firex, firey + 3) |
                            pc.ggetpix(firex, firey + 4) | pc.ggetpix(firex, firey + 5) |
                            pc.ggetpix(firex, firey + 6)) & 3;
                    break;
            }
            clbits = drawing.drawfire(firex, firey, 0);
            main.increasePenalty();
            if ((clbits & 0x3f00) != 0)
                for (mon = 0, b = 256; mon < 6; mon++, b <<= 1)
                    if ((clbits & b) != 0) {
                        monster.killmon(mon);
                        scores.scorekill();
                        expsn = 1;
                    }
            if ((clbits & 0x40fe) != 0)
                expsn = 1;
            switch (firedir) {
                case 0:
                    if (firex > 296)
                        expsn = 1;
                    else if (pix != 0 && clbits == 0) {
                        expsn = 1;
                        firex -= 8;
                        drawing.drawfire(firex, firey, 0);
                    }
                    break;
                case 4:
                    if (firex < 16)
                        expsn = 1;
                    else if (pix != 0 && clbits == 0) {
                        expsn = 1;
                        firex += 8;
                        drawing.drawfire(firex, firey, 0);
                    }
                    break;
                case 2:
                    if (firey < 15)
                        expsn = 1;
                    else if (pix != 0 && clbits == 0) {
                        expsn = 1;
                        firey += 7;
                        drawing.drawfire(firex, firey, 0);
                    }
                    break;
                case 6:
                    if (firey > 183)
                        expsn = 1;
                    else if (pix != 0 && clbits == 0) {
                        expsn = 1;
                        firey -= 7;
                        drawing.drawfire(firex, firey, 0);
                    }
            }
        }
    }
}