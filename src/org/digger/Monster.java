package org.digger;

class Monster {

    private Digger dig;

    private _monster[] mondat = {new _monster(), new _monster(), new _monster(), new _monster(), new _monster(), new _monster()};    // [6]

    private int nextmonster = 0;
    private int totalmonsters = 0;
    private int maxmononscr = 0;
    private int nextmontime = 0;
    private int mongaptime = 0;

    private boolean unbonusflag = false;
    private boolean mongotgold = false;

    Monster(Digger d) {
        dig = d;
    }

    private void checkcoincide(int mon, int bits) {
        int m, b;
        for (m = 0, b = 256; m < 6; m++, b <<= 1) {
            if (((bits & b) != 0) && (mondat[mon].getDir() == mondat[m].getDir()) && (mondat[m].getStime() == 0) && (mondat[mon].getStime() == 0)) {
                mondat[m].setDir(dig.reversedir(mondat[m].getDir()));
            }
        }
    }

    protected void checkmonscared(int h) {
        int m;
        for (m = 0; m < 6; m++) {
            if ((h == mondat[m].getH()) && (mondat[m].getDir() == 2)) {
                mondat[m].setDir(6);
            }
        }
    }

    private void createmonster() {
        int i;
        for (i = 0; i < 6; i++)
            if (!mondat[i].isFlag()) {
                mondat[i].create();
                nextmonster++;
                nextmontime = mongaptime;
                dig.getSprite().movedrawspr(i + 8, mondat[i].getX(), mondat[i].getY());
                break;
            }
    }

    protected void domonsters() {
        int i;
        if (nextmontime > 0)
            nextmontime--;
        else {
            if (nextmonster < totalmonsters && nmononscr() < maxmononscr && dig.isDigonscr() &&
                    !dig.isBonusmode())
                createmonster();
            if (unbonusflag && nextmonster == totalmonsters && nextmontime == 0)
                if (dig.isDigonscr()) {
                    unbonusflag = false;
                    dig.createbonus();
                }
        }
        for (i = 0; i < 6; i++)
            if (mondat[i].isFlag()) {
                if (mondat[i].getHnt() > 10 - dig.getMain().levof10()) {
                    if (mondat[i].isNob()) {
                        mondat[i].setNob(false);
                        mondat[i].setHnt(0);
                    }
                }
                if (mondat[i].isAlive())
                    if (mondat[i].getT() == 0) {
                        monai(i);
                        if (dig.getMain().randno(15 - dig.getMain().levof10()) == 0 && mondat[i].isNob())
                            monai(i);
                    } else
                        mondat[i].decreaseT();
                else
                    mondie(i);
            }
    }

    protected void erasemonsters() {
        int i;
        for (i = 0; i < 6; i++)
            if (mondat[i].isFlag())
                dig.getSprite().erasespr(i + 8);
    }

    private boolean fieldclear(int dir, int x, int y) {
        switch (dir) {
            case 0:
                if (x < 14)
                    if ((getfield(x + 1, y) & 0x2000) == 0)
                        if ((getfield(x + 1, y) & 1) == 0 || (getfield(x, y) & 0x10) == 0)
                            return true;
                break;
            case 4:
                if (x > 0)
                    if ((getfield(x - 1, y) & 0x2000) == 0)
                        if ((getfield(x - 1, y) & 0x10) == 0 || (getfield(x, y) & 1) == 0)
                            return true;
                break;
            case 2:
                if (y > 0)
                    if ((getfield(x, y - 1) & 0x2000) == 0)
                        if ((getfield(x, y - 1) & 0x800) == 0 || (getfield(x, y) & 0x40) == 0)
                            return true;
                break;
            case 6:
                if (y < 9)
                    if ((getfield(x, y + 1) & 0x2000) == 0)
                        if ((getfield(x, y + 1) & 0x40) == 0 || (getfield(x, y) & 0x800) == 0)
                            return true;
        }
        return false;
    }

    protected int getfield(int x, int y) {
        return dig.getDrawing().getField()[y * 15 + x];
    }

    protected void incmont(int n) {
        int m;
        if (n > 6)
            n = 6;
        for (m = 1; m < n; m++)
            mondat[m].increaseT();
    }

    private void incpenalties(int bits) {
        int m, b;
        for (m = 0, b = 256; m < 6; m++, b <<= 1) {
            if ((bits & b) != 0)
                dig.getMain().increasePenalty();
            b <<= 1;
        }
    }

    protected void initmonsters() {
        int i;
        for (i = 0; i < 6; i++) {
            mondat[i].setFlag(false);
        }
        nextmonster = 0;
        mongaptime = 45 - (dig.getMain().levof10() << 1);
        totalmonsters = dig.getMain().levof10() + 5;
        switch (dig.getMain().levof10()) {
            case 1:
                maxmononscr = 3;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                maxmononscr = 4;
                break;
            case 8:
            case 9:
            case 10:
                maxmononscr = 5;
        }
        nextmontime = 10;
        unbonusflag = true;
    }

    protected void killmon(int mon) {
        if (mondat[mon].isFlag()) {
            mondat[mon].setFlag(false);
            mondat[mon].setAlive(false);

            dig.getSprite().erasespr(mon + 8);
            if (dig.isBonusmode())
                totalmonsters++;
        }
    }

    protected int killmonsters(int bits) {
        int m, b, n = 0;
        for (m = 0, b = 256; m < 6; m++, b <<= 1)
            if ((bits & b) != 0) {
                killmon(m);
                n++;
            }
        return n;
    }

    private void monai(int mon) {
        int clbits, monox, monoy, dir, mdirp1, mdirp2, mdirp3, mdirp4, t;
        boolean push;
        monox = mondat[mon].getX();
        monoy = mondat[mon].getY();
        if (mondat[mon].getXr() == 0 && mondat[mon].getYr() == 0) {

            /* If we are here the monster needs to know which way to turn next. */

            /* Turn hobbin back into nobbin if it's had its time */

            if (mondat[mon].getHnt() > 30 + (dig.getMain().levof10() << 1))
                if (!mondat[mon].isNob()) {
                    mondat[mon].setHnt(0);
                    mondat[mon].setNob(true);
                }

            /* Set up monster direction properties to chase dig */

            if (Math.abs(dig.getDiggery() - mondat[mon].getY()) > Math.abs(dig.getDiggerx() - mondat[mon].getX())) {
                if (dig.getDiggery() < mondat[mon].getY()) {
                    mdirp1 = 2;
                    mdirp4 = 6;
                } else {
                    mdirp1 = 6;
                    mdirp4 = 2;
                }
                if (dig.getDiggerx() < mondat[mon].getX()) {
                    mdirp2 = 4;
                    mdirp3 = 0;
                } else {
                    mdirp2 = 0;
                    mdirp3 = 4;
                }
            } else {
                if (dig.getDiggerx() < mondat[mon].getX()) {
                    mdirp1 = 4;
                    mdirp4 = 0;
                } else {
                    mdirp1 = 0;
                    mdirp4 = 4;
                }
                if (dig.getDiggery() < mondat[mon].getY()) {
                    mdirp2 = 2;
                    mdirp3 = 6;
                } else {
                    mdirp2 = 6;
                    mdirp3 = 2;
                }
            }

            /* In bonus mode, run away from digger */

            if (dig.isBonusmode()) {
                t = mdirp1;
                mdirp1 = mdirp4;
                mdirp4 = t;
                t = mdirp2;
                mdirp2 = mdirp3;
                mdirp3 = t;
            }

            /* Adjust priorities so that monsters don't reverse direction unless they
          really have to */

            dir = dig.reversedir(mondat[mon].getDir());
            if (dir == mdirp1) {
                mdirp1 = mdirp2;
                mdirp2 = mdirp3;
                mdirp3 = mdirp4;
                mdirp4 = dir;
            }
            if (dir == mdirp2) {
                mdirp2 = mdirp3;
                mdirp3 = mdirp4;
                mdirp4 = dir;
            }
            if (dir == mdirp3) {
                mdirp3 = mdirp4;
                mdirp4 = dir;
            }

            /* Introduce a randno element on levels <6 : occasionally swap p1 and p3 */

            if (dig.getMain().randno(dig.getMain().levof10() + 5) == 1 && dig.getMain().levof10() < 6) {
                t = mdirp1;
                mdirp1 = mdirp3;
                mdirp3 = t;
            }

            /* Check field and find direction */

            if (fieldclear(mdirp1, mondat[mon].getH(), mondat[mon].getV()))
                dir = mdirp1;
            else if (fieldclear(mdirp2, mondat[mon].getH(), mondat[mon].getV()))
                dir = mdirp2;
            else if (fieldclear(mdirp3, mondat[mon].getH(), mondat[mon].getV()))
                dir = mdirp3;
            else if (fieldclear(mdirp4, mondat[mon].getH(), mondat[mon].getV()))
                dir = mdirp4;

            /* Hobbins don't care about the field: they go where they want. */

            if (!mondat[mon].isNob())
                dir = mdirp1;

            /* Monsters take a time penalty for changing direction */

            if (mondat[mon].getDir() != dir)
                mondat[mon].increaseT();

            /* Save the new direction */

            mondat[mon].setDir(dir);
        }

        /* If monster is about to go off edge of screen, stop it. */

        if ((mondat[mon].getX() == 292 && mondat[mon].getDir() == 0) ||
                (mondat[mon].getX() == 12 && mondat[mon].getDir() == 4) ||
                (mondat[mon].getY() == 180 && mondat[mon].getDir() == 6) ||
                (mondat[mon].getY() == 18 && mondat[mon].getDir() == 2))
            mondat[mon].setDir(-1);

        /* Change hdir for hobbin */

        if (mondat[mon].getDir() == 4 || mondat[mon].getDir() == 0)
            mondat[mon].setHdir(mondat[mon].getDir());

        /* Hobbins digger */

        if (!mondat[mon].isNob())
            dig.getDrawing().eatfield(mondat[mon].getX(), mondat[mon].getY(), mondat[mon].getDir());

        /* (Draw new tunnels) and move monster */

        switch (mondat[mon].getDir()) {
            case 0:
                if (!mondat[mon].isNob()) {
                    dig.getDrawing().drawrightblob(mondat[mon].getX(), mondat[mon].getY());
                }
                mondat[mon].goRight();
                break;
            case 4:
                if (!mondat[mon].isNob())
                    dig.getDrawing().drawleftblob(mondat[mon].getX(), mondat[mon].getY());
                mondat[mon].goLeft();
                break;
            case 2:
                if (!mondat[mon].isNob())
                    dig.getDrawing().drawtopblob(mondat[mon].getX(), mondat[mon].getY());
                mondat[mon].goUp();
                break;
            case 6:
                if (!mondat[mon].isNob())
                    dig.getDrawing().drawbottomblob(mondat[mon].getX(), mondat[mon].getY());
                mondat[mon].goDown();
                break;
        }

        /* Hobbins can eat emeralds */

        if (!mondat[mon].isNob())
            dig.hitemerald((mondat[mon].getX() - 12) / 20, (mondat[mon].getY() - 18) / 18, (mondat[mon].getX() - 12) % 20, (mondat[mon].getY() - 18) % 18, mondat[mon].getDir());

        /* If digger's gone, don't bother */

        if (!dig.isDigonscr()) {
            mondat[mon].dontMoveFrom(monox, monoy);
        }

        /* If monster's just started, don't move yet */

        if (mondat[mon].getStime() != 0) {
            mondat[mon].decreaseStime();
            mondat[mon].dontMoveFrom(monox, monoy);
        }

        /* Increase time counter for hobbin */

        if (!mondat[mon].isNob() && mondat[mon].getHnt() < 100)
            mondat[mon].increaseHobbinCounter();

        /* Draw monster */

        push = true;
        clbits = dig.getDrawing().drawmon(mon, mondat[mon].isNob(), mondat[mon].getHdir(), mondat[mon].getX(), mondat[mon].getY());
        dig.getMain().increasePenalty();

        /* Collision with another monster */

        if ((clbits & 0x3f00) != 0) {
            mondat[mon].increaseT(); /* Time penalty */
            checkcoincide(mon, clbits); /* Ensure both aren't moving in the same dir. */
            incpenalties(clbits);
        }

        /* Check for collision with bag */

        if ((clbits & dig.getBags().bagbits()) != 0) {
            mondat[mon].increaseT(); /* Time penalty */
            mongotgold = false;
            if (mondat[mon].getDir() == 4 || mondat[mon].getDir() == 0) { /* Horizontal push */
                push = dig.getBags().pushbags(mondat[mon].getDir(), clbits);
                mondat[mon].increaseT(); /* Time penalty */
            } else if (!dig.getBags().pushudbags(clbits)) /* Vertical push */
                push = false;
            if (mongotgold) /* No time penalty if monster eats gold */
                mondat[mon].setT(0);
            if (!mondat[mon].isNob() && mondat[mon].getHnt() > 1)
                dig.getBags().removebags(clbits); /* Hobbins eat bags */
        }

        /* Increase hobbin cross counter */

        if (mondat[mon].isNob() && ((clbits & 0x3f00) != 0) && dig.isDigonscr())
            mondat[mon].increaseHobbinCounter();

        /* See if bags push monster back */

        if (!push) {
            mondat[mon].dontMoveFrom(monox, monoy);
            dig.getDrawing().drawmon(mon, mondat[mon].isNob(), mondat[mon].getHdir(), mondat[mon].getX(), mondat[mon].getY());
            dig.getMain().increasePenalty();
            if (mondat[mon].isNob()) /* The other way to create hobbin: stuck on h-bag */
                mondat[mon].increaseHobbinCounter();
            if ((mondat[mon].getDir() == 2 || mondat[mon].getDir() == 6) && mondat[mon].isNob())
                mondat[mon].setDir(dig.reversedir(mondat[mon].getDir())); /* If vertical, give up */
        }

        /* Collision with digger */

        if (((clbits & 1) != 0) && dig.isDigonscr())
            if (dig.isBonusmode()) {
                killmon(mon);
                dig.getScores().scoreeatm();
                dig.getSound().soundeatm(); /* Collision in bonus mode */
            } else
                dig.killdigger(3, 0); /* Kill digger */

        /* Update co-ordinates */

        mondat[mon].setH((mondat[mon].getX() - 12) / 20);
        mondat[mon].setV((mondat[mon].getY() - 18) / 18);
        mondat[mon].setXr((mondat[mon].getX() - 12) % 20);
        mondat[mon].setYr((mondat[mon].getY() - 18) % 18);
    }

    private void mondie(int mon) {
        switch (mondat[mon].getDeath()) {
            case 1:
                if (dig.getBags().bagy(mondat[mon].getBag()) + 6 > mondat[mon].getY())
                    mondat[mon].setY(dig.getBags().bagy(mondat[mon].getBag()));
                dig.getDrawing().drawmondie(mon, mondat[mon].isNob(), mondat[mon].getHdir(), mondat[mon].getX(), mondat[mon].getY());
                dig.getMain().increasePenalty();
                if (dig.getBags().getbagdir(mondat[mon].getBag()) == -1) {
                    mondat[mon].setDtime(1);
                    mondat[mon].setDeath(4);
                }
                break;
            case 4:
                if (mondat[mon].getDtime() != 0)
                    mondat[mon].decreaseDtime();
                else {
                    killmon(mon);
                    dig.getScores().scorekill();
                }
        }
    }

    protected void mongold() {
        mongotgold = true;
    }

    protected int monleft() {
        return nmononscr() + totalmonsters - nextmonster;
    }

    private int nmononscr() {
        int i, n = 0;
        for (i = 0; i < 6; i++)
            if (mondat[i].isFlag())
                n++;
        return n;
    }

    private void squashmonster(int mon, int death, int bag) {
        mondat[mon].setAlive(false);
        mondat[mon].setDeath(death);
        mondat[mon].setBag(bag);
    }

    protected void squashmonsters(int bag, int bits) {
        int m, b;
        for (m = 0, b = 256; m < 6; m++, b <<= 1)
            if ((bits & b) != 0)
                if (mondat[m].getY() >= dig.getBags().bagy(bag))
                    squashmonster(m, 1, bag);
    }
}