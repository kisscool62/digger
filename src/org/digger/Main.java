package org.digger;

import lombok.Getter;
import lombok.Setter;

class Main {

    private Digger dig;


    private int digsprorder[] = {14, 13, 7, 6, 5, 4, 3, 2, 1, 12, 11, 10, 9, 8, 15, 0};    // [16]

    private _game[] gamedat = {new _game(), new _game()};

    @Getter
    @Setter
    private String pldispbuf = "";

    private int curplayer = 0;

    @Getter
    private int nplayers = 0;
    private int penalty = 0;
    private boolean levnotdrawn = false;
    private boolean flashplayer = false;

    private boolean levfflag = false;
    private boolean biosflag = false;
    private int speedmul = 40;
    private int delaytime = 0;

    private int randv;

    private String leveldat[][] =        // [8][10][15]
            {{"S   B     HHHHS",
                    "V  CC  C  V B  ",
                    "VB CC  C  V    ",
                    "V  CCB CB V CCC",
                    "V  CC  C  V CCC",
                    "HH CC  C  V CCC",
                    " V    B B V    ",
                    " HHHH     V    ",
                    "C   V     V   C",
                    "CC  HHHHHHH  CC"},
                    {"SHHHHH  B B  HS",
                            " CC  V       V ",
                            " CC  V CCCCC V ",
                            "BCCB V CCCCC V ",
                            "CCCC V       V ",
                            "CCCC V B  HHHH ",
                            " CC  V CC V    ",
                            " BB  VCCCCV CC ",
                            "C    V CC V CC ",
                            "CC   HHHHHH    "},
                    {"SHHHHB B BHHHHS",
                            "CC  V C C V BB ",
                            "C   V C C V CC ",
                            " BB V C C VCCCC",
                            "CCCCV C C VCCCC",
                            "CCCCHHHHHHH CC ",
                            " CC  C V C  CC ",
                            " CC  C V C     ",
                            "C    C V C    C",
                            "CC   C H C   CC"},
                    {"SHBCCCCBCCCCBHS",
                            "CV  CCCCCCC  VC",
                            "CHHH CCCCC HHHC",
                            "C  V  CCC  V  C",
                            "   HHH C HHH   ",
                            "  B  V B V  B  ",
                            "  C  VCCCV  C  ",
                            " CCC HHHHH CCC ",
                            "CCCCC CVC CCCCC",
                            "CCCCC CHC CCCCC"},
                    {"SHHHHHHHHHHHHHS",
                            "VBCCCCBVCCCCCCV",
                            "VCCCCCCV CCBC V",
                            "V CCCC VCCBCCCV",
                            "VCCCCCCV CCCC V",
                            "V CCCC VBCCCCCV",
                            "VCCBCCCV CCCC V",
                            "V CCBC VCCCCCCV",
                            "VCCCCCCVCCCCCCV",
                            "HHHHHHHHHHHHHHH"},
                    {"SHHHHHHHHHHHHHS",
                            "VCBCCV V VCCBCV",
                            "VCCC VBVBV CCCV",
                            "VCCCHH V HHCCCV",
                            "VCC V CVC V CCV",
                            "VCCHH CVC HHCCV",
                            "VC V CCVCC V CV",
                            "VCHHBCCVCCBHHCV",
                            "VCVCCCCVCCCCVCV",
                            "HHHHHHHHHHHHHHH"},
                    {"SHCCCCCVCCCCCHS",
                            " VCBCBCVCBCBCV ",
                            "BVCCCCCVCCCCCVB",
                            "CHHCCCCVCCCCHHC",
                            "CCV CCCVCCC VCC",
                            "CCHHHCCVCCHHHCC",
                            "CCCCV CVC VCCCC",
                            "CCCCHH V HHCCCC",
                            "CCCCCV V VCCCCC",
                            "CCCCCHHHHHCCCCC"},
                    {"HHHHHHHHHHHHHHS",
                            "V CCBCCCCCBCC V",
                            "HHHCCCCBCCCCHHH",
                            "VBV CCCCCCC VBV",
                            "VCHHHCCCCCHHHCV",
                            "VCCBV CCC VBCCV",
                            "VCCCHHHCHHHCCCV",
                            "VCCCC V V CCCCV",
                            "VCCCCCV VCCCCCV",
                            "HHHHHHHHHHHHHHH"}};

    Main(Digger d) {
        dig = d;
    }

    protected void addlife(int pl) {
        gamedat[pl - 1].lives++;
        dig.getSound().sound1up();
    }

    private void calibrate() {
        dig.getSound().setVolume((int) (dig.getPc().getkips() / 291));
        if (dig.getSound().getVolume() == 0)
            dig.getSound().setVolume(1);
    }

    private void checklevdone() {
        if ((dig.countem() == 0 || dig.getMonster().monleft() == 0) && dig.isDigonscr())
            gamedat[curplayer].levdone = true;
        else
            gamedat[curplayer].levdone = false;
    }

    protected void cleartopline() {
        dig.getDrawing().outtext("                          ", 0, 0, 3);
        dig.getDrawing().outtext(" ", 308, 0, 3);
    }

    private void drawscreen() {
        dig.getDrawing().creatembspr();
        dig.getDrawing().drawstatics();
        dig.getBags().drawbags();
        dig.drawemeralds();
        dig.initdigger();
        dig.getMonster().initmonsters();
    }

    protected int getcplayer() {
        return curplayer;
    }

    protected int getlevch(int x, int y, int l) {
        if (l == 0)
            l++;
        return leveldat[l - 1][y].charAt(x);
    }

    protected int getlives(int pl) {
        return gamedat[pl - 1].lives;
    }

    protected void incpenalty() {
        penalty++;
    }

    private void initchars() {
        dig.getDrawing().initmbspr();
        dig.initdigger();
        dig.getMonster().initmonsters();
    }

    private void initlevel() {
        gamedat[curplayer].levdone = false;
        dig.getDrawing().makefield();
        dig.makeemfield();
        dig.getBags().initbags();
        levnotdrawn = true;
    }

    private int levno() {
        return gamedat[curplayer].level;
    }

    protected int levof10() {
        if (gamedat[curplayer].level > 10)
            return 10;
        return gamedat[curplayer].level;
    }

    protected int levplan() {
        int l = levno();
        if (l > 8)
            l = (l & 3) + 5; /* Level plan: 12345678, 678, (5678) 247 times, 5 forever */
        return l;
    }

    protected void main() {

        int frame, t, x = 0;
        boolean start;

        randv = (int) dig.getPc().gethrt();
        calibrate();
//  parsecmd(argc,argv);
        dig.setFtime(speedmul * 2000l);
        dig.getSprite().setretr(false);
        dig.getPc().ginit();
        dig.getSprite().setretr(true);
        dig.getPc().gpal(0);
        dig.getInput().initkeyb();
        dig.getInput().detectjoy();
        dig.getScores().loadscores();
        dig.getSound().initsound();

        dig.getScores().run();        // ??
        dig.getScores()._updatescores(dig.getScores().getScores());

        nplayers = 1;
        do {
            dig.getSound().soundstop();
            dig.getSprite().setsprorder(digsprorder);
            dig.getDrawing().creatembspr();
            dig.getInput().detectjoy();
            dig.getPc().gclear();
            dig.getPc().gtitle();
            dig.getDrawing().outtext("D I G G E R", 100, 0, 3);
            shownplayers();
            dig.getScores().showtable();
            start = false;
            frame = 0;

            dig.setTime(dig.getPc().gethrt());

            while (!start) {
                start = dig.getInput().teststart();
                if (dig.getInput().getAkeypressed() == 27) {  //	esc
                    switchnplayers();
                    shownplayers();
                    dig.getInput().resetAKeyPressed();
                    dig.getInput().resetKeyPressed();
                }
                if (frame == 0)
                    for (t = 54; t < 174; t += 12)
                        dig.getDrawing().outtext("            ", 164, t, 0);
                if (frame == 50) {
                    dig.getSprite().movedrawspr(8, 292, 63);
                    x = 292;
                }
                if (frame > 50 && frame <= 77) {
                    x -= 4;
                    dig.getDrawing().drawmon(0, true, 4, x, 63);
                }
                if (frame > 77)
                    dig.getDrawing().drawmon(0, true, 0, 184, 63);
                if (frame == 83)
                    dig.getDrawing().outtext("NOBBIN", 216, 64, 2);
                if (frame == 90) {
                    dig.getSprite().movedrawspr(9, 292, 82);
                    dig.getDrawing().drawmon(1, false, 4, 292, 82);
                    x = 292;
                }
                if (frame > 90 && frame <= 117) {
                    x -= 4;
                    dig.getDrawing().drawmon(1, false, 4, x, 82);
                }
                if (frame > 117)
                    dig.getDrawing().drawmon(1, false, 0, 184, 82);
                if (frame == 123)
                    dig.getDrawing().outtext("HOBBIN", 216, 83, 2);
                if (frame == 130) {
                    dig.getSprite().movedrawspr(0, 292, 101);
                    dig.getDrawing().drawdigger(4, 292, 101, true);
                    x = 292;
                }
                if (frame > 130 && frame <= 157) {
                    x -= 4;
                    dig.getDrawing().drawdigger(4, x, 101, true);
                }
                if (frame > 157)
                    dig.getDrawing().drawdigger(0, 184, 101, true);
                if (frame == 163)
                    dig.getDrawing().outtext("DIGGER", 216, 102, 2);
                if (frame == 178) {
                    dig.getSprite().movedrawspr(1, 184, 120);
                    dig.getDrawing().drawgold(1, 0, 184, 120);
                }
                if (frame == 183)
                    dig.getDrawing().outtext("GOLD", 216, 121, 2);
                if (frame == 198)
                    dig.getDrawing().drawemerald(184, 141);
                if (frame == 203)
                    dig.getDrawing().outtext("EMERALD", 216, 140, 2);
                if (frame == 218)
                    dig.getDrawing().drawbonus(184, 158);
                if (frame == 223)
                    dig.getDrawing().outtext("BONUS", 216, 159, 2);
                dig.newframe();
                frame++;
                if (frame > 250)
                    frame = 0;
            }
            gamedat[0].level = 1;
            gamedat[0].lives = 3;
            if (nplayers == 2) {
                gamedat[1].level = 1;
                gamedat[1].lives = 3;
            } else
                gamedat[1].lives = 0;
            dig.getPc().gclear();
            curplayer = 0;
            initlevel();
            curplayer = 1;
            initlevel();
            dig.getScores().zeroscores();
            dig.setBonusvisible(true);
            if (nplayers == 2)
                flashplayer = true;
            curplayer = 0;
//	if (dig.org.digger.Input.escape)
//	  break;
//    if (recording)
//	  recputinit();
            while ((gamedat[0].lives != 0 || gamedat[1].lives != 0) && !dig.getInput().isEscape()) {
                gamedat[curplayer].dead = false;
                while (!gamedat[curplayer].dead && gamedat[curplayer].lives != 0 && !dig.getInput().isEscape()) {
                    dig.getDrawing().initmbspr();
                    play();
                }
                if (gamedat[1 - curplayer].lives != 0) {
                    curplayer = 1 - curplayer;
                    flashplayer = levnotdrawn = true;
                }
            }
            dig.getInput().setEscape(false);
        } while (!false); //dig.org.digger.Input.escape);
/*  dig.getSound().soundoff();
  restoreint8();
  restorekeyb();
  graphicsoff(); */
    }

    private void play() {
        int t, c;
/*  if (playing)
	randv=recgetrand();
  else
	randv=getlrt();
  if (recording)
	recputrand(randv); */
        if (levnotdrawn) {
            levnotdrawn = false;
            drawscreen();
            dig.setTime(dig.getPc().gethrt());
            if (flashplayer) {
                flashplayer = false;
                pldispbuf = "PLAYER ";
                if (curplayer == 0)
                    pldispbuf += "1";
                else
                    pldispbuf += "2";
                cleartopline();
                for (t = 0; t < 15; t++)
                    for (c = 1; c <= 3; c++) {
                        dig.getDrawing().outtext(pldispbuf, 108, 0, c);
                        dig.getScores().writecurscore(c);
                        /* olddelay(20); */
                        dig.newframe();
                        if (dig.getInput().isEscape())
                            return;
                    }
                dig.getScores().drawscores();
                dig.getScores().addscore(0);
            }
        } else
            initchars();
        dig.getInput().resetKeyPressed();
        dig.getDrawing().outtext("        ", 108, 0, 3);
        dig.getScores().initscores();
        dig.getDrawing().drawlives();
        dig.getSound().music(1);
        dig.getInput().readdir();
        dig.setTime(dig.getPc().gethrt());
        while (!gamedat[curplayer].dead && !gamedat[curplayer].levdone && !dig.getInput().isEscape()) {
            penalty = 0;
            dig.dodigger();
            dig.getMonster().domonsters();
            dig.getBags().dobags();
/*  if (penalty<8)
	  for (t=(8-penalty)*5;t>0;t--)
		olddelay(1); */
            if (penalty > 8)
                dig.getMonster().incmont(penalty - 8);
            testpause();
            checklevdone();
        }
        dig.erasedigger();
        dig.getSound().musicoff();
        t = 20;
        while ((dig.getBags().getnmovingbags() != 0 || t != 0) && !dig.getInput().isEscape()) {
            if (t != 0)
                t--;
            penalty = 0;
            dig.getBags().dobags();
            dig.dodigger();
            dig.getMonster().domonsters();
            if (penalty < 8)
/*    for (t=(8-penalty)*5;t>0;t--)
		 olddelay(1); */
                t = 0;
        }
        dig.getSound().soundstop();
        dig.killfire();
        dig.erasebonus();
        dig.getBags().cleanupbags();
        dig.getDrawing().savefield();
        dig.getMonster().erasemonsters();
        dig.newframe();        // needed by Java version!!
        if (gamedat[curplayer].levdone)
            dig.getSound().soundlevdone();
        if (dig.countem() == 0) {
            gamedat[curplayer].level++;
            if (gamedat[curplayer].level > 1000)
                gamedat[curplayer].level = 1000;
            initlevel();
        }
        if (gamedat[curplayer].dead) {
            gamedat[curplayer].lives--;
            dig.getDrawing().drawlives();
            if (gamedat[curplayer].lives == 0 && !dig.getInput().isEscape())
                dig.getScores().endofgame();
        }
        if (gamedat[curplayer].levdone) {
            gamedat[curplayer].level++;
            if (gamedat[curplayer].level > 1000)
                gamedat[curplayer].level = 1000;
            initlevel();
        }
    }

    protected int randno(int n) {
        randv = randv * 0x15a4e35 + 1;
        return (randv & 0x7fffffff) % n;
    }

    protected void setdead(boolean bp6) {
        gamedat[curplayer].dead = bp6;
    }

    private void shownplayers() {
        if (nplayers == 1) {
            dig.getDrawing().outtext("ONE", 220, 25, 3);
            dig.getDrawing().outtext(" PLAYER ", 192, 39, 3);
        } else {
            dig.getDrawing().outtext("TWO", 220, 25, 3);
            dig.getDrawing().outtext(" PLAYERS", 184, 39, 3);
        }
    }

    private void switchnplayers() {
        nplayers = 3 - nplayers;
    }

    private void testpause() {
        if (dig.getInput().getAkeypressed() == 32) { /* Space bar */
            dig.getInput().resetAKeyPressed();;
            dig.getSound().soundpause();
            dig.getSound().sett2val(40);
            dig.getSound().setsoundt2();
            cleartopline();
            dig.getDrawing().outtext("PRESS ANY KEY", 80, 0, 1);
            dig.newframe();
            dig.getInput().resetKeyPressed();
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                }
                if (dig.getInput().getKeypressed() != 0)
                    break;
            }
            cleartopline();
            dig.getScores().drawscores();
            dig.getScores().addscore(0);
            dig.getDrawing().drawlives();
            dig.newframe();
            dig.setTime(dig.getPc().gethrt() - dig.getFrametime());
//	olddelay(200);
            dig.getInput().resetKeyPressed();
        } else
            dig.getSound().soundpauseoff();
    }

    protected void appendPldispbuf(String _pldispbuf) {
        pldispbuf += _pldispbuf;
    }
}