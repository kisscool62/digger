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
        for (m = 0, b = 256; m < 6; m++, b <<= 1)
            if (((bits & b) != 0) && (mondat[mon].dir == mondat[m].dir) && (mondat[m].stime == 0) && (mondat[mon].stime == 0))
                mondat[m].dir = dig.reversedir(mondat[m].dir);
    }

    protected void checkmonscared(int h) {
        int m;
        for (m = 0; m < 6; m++)
            if ((h == mondat[m].h) && (mondat[m].dir == 2))
                mondat[m].dir = 6;
    }

    private void createmonster() {
        int i;
        for (i = 0; i < 6; i++)
            if (!mondat[i].flag) {
                mondat[i].flag = true;
                mondat[i].alive = true;
                mondat[i].t = 0;
                mondat[i].nob = true;
                mondat[i].hnt = 0;
                mondat[i].h = 14;
                mondat[i].v = 0;
                mondat[i].x = 292;
                mondat[i].y = 18;
                mondat[i].xr = 0;
                mondat[i].yr = 0;
                mondat[i].dir = 4;
                mondat[i].hdir = 4;
                nextmonster++;
                nextmontime = mongaptime;
                mondat[i].stime = 5;
                dig.getSprite().movedrawspr(i + 8, mondat[i].x, mondat[i].y);
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
            if (mondat[i].flag) {
                if (mondat[i].hnt > 10 - dig.getMain().levof10()) {
                    if (mondat[i].nob) {
                        mondat[i].nob = false;
                        mondat[i].hnt = 0;
                    }
                }
                if (mondat[i].alive)
                    if (mondat[i].t == 0) {
                        monai(i);
                        if (dig.getMain().randno(15 - dig.getMain().levof10()) == 0 && mondat[i].nob)
                            monai(i);
                    } else
                        mondat[i].t--;
                else
                    mondie(i);
            }
    }

    protected void erasemonsters() {
        int i;
        for (i = 0; i < 6; i++)
            if (mondat[i].flag)
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
            mondat[m].t++;
    }

    private void incpenalties(int bits) {
        int m, b;
        for (m = 0, b = 256; m < 6; m++, b <<= 1) {
            if ((bits & b) != 0)
                dig.getMain().incpenalty();
            b <<= 1;
        }
    }

    protected void initmonsters() {
        int i;
        for (i = 0; i < 6; i++)
            mondat[i].flag = false;
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
        if (mondat[mon].flag) {
            mondat[mon].flag = mondat[mon].alive = false;
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
        monox = mondat[mon].x;
        monoy = mondat[mon].y;
        if (mondat[mon].xr == 0 && mondat[mon].yr == 0) {

            /* If we are here the monster needs to know which way to turn next. */

            /* Turn hobbin back into nobbin if it's had its time */

            if (mondat[mon].hnt > 30 + (dig.getMain().levof10() << 1))
                if (!mondat[mon].nob) {
                    mondat[mon].hnt = 0;
                    mondat[mon].nob = true;
                }

            /* Set up monster direction properties to chase dig */

            if (Math.abs(dig.getDiggery() - mondat[mon].y) > Math.abs(dig.getDiggerx() - mondat[mon].x)) {
                if (dig.getDiggery() < mondat[mon].y) {
                    mdirp1 = 2;
                    mdirp4 = 6;
                } else {
                    mdirp1 = 6;
                    mdirp4 = 2;
                }
                if (dig.getDiggerx() < mondat[mon].x) {
                    mdirp2 = 4;
                    mdirp3 = 0;
                } else {
                    mdirp2 = 0;
                    mdirp3 = 4;
                }
            } else {
                if (dig.getDiggerx() < mondat[mon].x) {
                    mdirp1 = 4;
                    mdirp4 = 0;
                } else {
                    mdirp1 = 0;
                    mdirp4 = 4;
                }
                if (dig.getDiggery() < mondat[mon].y) {
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

            dir = dig.reversedir(mondat[mon].dir);
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

            if (fieldclear(mdirp1, mondat[mon].h, mondat[mon].v))
                dir = mdirp1;
            else if (fieldclear(mdirp2, mondat[mon].h, mondat[mon].v))
                dir = mdirp2;
            else if (fieldclear(mdirp3, mondat[mon].h, mondat[mon].v))
                dir = mdirp3;
            else if (fieldclear(mdirp4, mondat[mon].h, mondat[mon].v))
                dir = mdirp4;

            /* Hobbins don't care about the field: they go where they want. */

            if (!mondat[mon].nob)
                dir = mdirp1;

            /* Monsters take a time penalty for changing direction */

            if (mondat[mon].dir != dir)
                mondat[mon].t++;

            /* Save the new direction */

            mondat[mon].dir = dir;
        }

        /* If monster is about to go off edge of screen, stop it. */

        if ((mondat[mon].x == 292 && mondat[mon].dir == 0) ||
                (mondat[mon].x == 12 && mondat[mon].dir == 4) ||
                (mondat[mon].y == 180 && mondat[mon].dir == 6) ||
                (mondat[mon].y == 18 && mondat[mon].dir == 2))
            mondat[mon].dir = -1;

        /* Change hdir for hobbin */

        if (mondat[mon].dir == 4 || mondat[mon].dir == 0)
            mondat[mon].hdir = mondat[mon].dir;

        /* Hobbins digger */

        if (!mondat[mon].nob)
            dig.getDrawing().eatfield(mondat[mon].x, mondat[mon].y, mondat[mon].dir);

        /* (Draw new tunnels) and move monster */

        switch (mondat[mon].dir) {
            case 0:
                if (!mondat[mon].nob)
                    dig.getDrawing().drawrightblob(mondat[mon].x, mondat[mon].y);
                mondat[mon].x += 4;
                break;
            case 4:
                if (!mondat[mon].nob)
                    dig.getDrawing().drawleftblob(mondat[mon].x, mondat[mon].y);
                mondat[mon].x -= 4;
                break;
            case 2:
                if (!mondat[mon].nob)
                    dig.getDrawing().drawtopblob(mondat[mon].x, mondat[mon].y);
                mondat[mon].y -= 3;
                break;
            case 6:
                if (!mondat[mon].nob)
                    dig.getDrawing().drawbottomblob(mondat[mon].x, mondat[mon].y);
                mondat[mon].y += 3;
                break;
        }

        /* Hobbins can eat emeralds */

        if (!mondat[mon].nob)
            dig.hitemerald((mondat[mon].x - 12) / 20, (mondat[mon].y - 18) / 18, (mondat[mon].x - 12) % 20, (mondat[mon].y - 18) % 18, mondat[mon].dir);

        /* If digger's gone, don't bother */

        if (!dig.isDigonscr()) {
            mondat[mon].x = monox;
            mondat[mon].y = monoy;
        }

        /* If monster's just started, don't move yet */

        if (mondat[mon].stime != 0) {
            mondat[mon].stime--;
            mondat[mon].x = monox;
            mondat[mon].y = monoy;
        }

        /* Increase time counter for hobbin */

        if (!mondat[mon].nob && mondat[mon].hnt < 100)
            mondat[mon].hnt++;

        /* Draw monster */

        push = true;
        clbits = dig.getDrawing().drawmon(mon, mondat[mon].nob, mondat[mon].hdir, mondat[mon].x, mondat[mon].y);
        dig.getMain().incpenalty();

        /* Collision with another monster */

        if ((clbits & 0x3f00) != 0) {
            mondat[mon].t++; /* Time penalty */
            checkcoincide(mon, clbits); /* Ensure both aren't moving in the same dir. */
            incpenalties(clbits);
        }

        /* Check for collision with bag */

        if ((clbits & dig.getBags().bagbits()) != 0) {
            mondat[mon].t++; /* Time penalty */
            mongotgold = false;
            if (mondat[mon].dir == 4 || mondat[mon].dir == 0) { /* Horizontal push */
                push = dig.getBags().pushbags(mondat[mon].dir, clbits);
                mondat[mon].t++; /* Time penalty */
            } else if (!dig.getBags().pushudbags(clbits)) /* Vertical push */
                push = false;
            if (mongotgold) /* No time penalty if monster eats gold */
                mondat[mon].t = 0;
            if (!mondat[mon].nob && mondat[mon].hnt > 1)
                dig.getBags().removebags(clbits); /* Hobbins eat bags */
        }

        /* Increase hobbin cross counter */

        if (mondat[mon].nob && ((clbits & 0x3f00) != 0) && dig.isDigonscr())
            mondat[mon].hnt++;

        /* See if bags push monster back */

        if (!push) {
            mondat[mon].x = monox;
            mondat[mon].y = monoy;
            dig.getDrawing().drawmon(mon, mondat[mon].nob, mondat[mon].hdir, mondat[mon].x, mondat[mon].y);
            dig.getMain().incpenalty();
            if (mondat[mon].nob) /* The other way to create hobbin: stuck on h-bag */
                mondat[mon].hnt++;
            if ((mondat[mon].dir == 2 || mondat[mon].dir == 6) && mondat[mon].nob)
                mondat[mon].dir = dig.reversedir(mondat[mon].dir); /* If vertical, give up */
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

        mondat[mon].h = (mondat[mon].x - 12) / 20;
        mondat[mon].v = (mondat[mon].y - 18) / 18;
        mondat[mon].xr = (mondat[mon].x - 12) % 20;
        mondat[mon].yr = (mondat[mon].y - 18) % 18;
    }

    private void mondie(int mon) {
        switch (mondat[mon].death) {
            case 1:
                if (dig.getBags().bagy(mondat[mon].bag) + 6 > mondat[mon].y)
                    mondat[mon].y = dig.getBags().bagy(mondat[mon].bag);
                dig.getDrawing().drawmondie(mon, mondat[mon].nob, mondat[mon].hdir, mondat[mon].x, mondat[mon].y);
                dig.getMain().incpenalty();
                if (dig.getBags().getbagdir(mondat[mon].bag) == -1) {
                    mondat[mon].dtime = 1;
                    mondat[mon].death = 4;
                }
                break;
            case 4:
                if (mondat[mon].dtime != 0)
                    mondat[mon].dtime--;
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
            if (mondat[i].flag)
                n++;
        return n;
    }

    private void squashmonster(int mon, int death, int bag) {
        mondat[mon].alive = false;
        mondat[mon].death = death;
        mondat[mon].bag = bag;
    }

    protected void squashmonsters(int bag, int bits) {
        int m, b;
        for (m = 0, b = 256; m < 6; m++, b <<= 1)
            if ((bits & b) != 0)
                if (mondat[m].y >= dig.getBags().bagy(bag))
                    squashmonster(m, 1, bag);
    }
}