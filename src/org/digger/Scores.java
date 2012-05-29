package org.digger;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

class Scores implements Runnable {

    private Digger dig;

    @Getter
    private Object[][] scores;
    private String substr;

    private char highbuf[] = new char[10];
    private long scorehigh[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};    // [12]
    private String scoreinit[] = new String[11];
    private long scoret = 0;
    private long score1 = 0;
    private long score2 = 0;
    private long nextbs1 = 0;
    private long nextbs2 = 0;
    private String hsbuf;
    private char scorebuf[] = new char[512];
    private int bonusscore = 20000;
    private boolean gotinitflag = false;

    Scores(Digger d) {
        dig = d;
    }

    public Object[][] _submit(String n, int s) {
        if (dig.getSubaddr() != null) {
            int ms = 16 + (int) (System.currentTimeMillis() % (65536 - 16));
            substr = n + '+' + s + '+' + ms + '+' + ((ms + 32768) * s) % 65536;
            new Thread(this).start();
        }
        return scores;
    }

    public void _updatescores(Object[][] o) {

        if (o == null)
            return;

        try {
            String[] in = new String[10];
            int[] sc = new int[10];
            for (int i = 0; i < 10; i++) {
                in[i] = (String) o[i][0];
                sc[i] = ((Integer) o[i][1]).intValue();
            }
            for (int i = 0; i < 10; i++) {
                scoreinit[i + 1] = in[i];
                scorehigh[i + 2] = sc[i];
            }
        } catch (Exception e) {
        }
        ;

    }

    protected void addscore(int score) {
        if (dig.getMain().getcplayer() == 0) {
            score1 += score;
            if (score1 > 999999l)
                score1 = 0;
            writenum(score1, 0, 0, 6, 1);
            if (score1 >= nextbs1) {
                if (dig.getMain().getlives(1) < 5) {
                    dig.getMain().addlife(1);
                    dig.getDrawing().drawlives();
                }
                nextbs1 += bonusscore;
            }
        } else {
            score2 += score;
            if (score2 > 999999l)
                score2 = 0;
            if (score2 < 100000l)
                writenum(score2, 236, 0, 6, 1);
            else
                writenum(score2, 248, 0, 6, 1);
            if (score2 > nextbs2) {   /* Player 2 doesn't get the life until >20,000 ! */
                if (dig.getMain().getlives(2) < 5) {
                    dig.getMain().addlife(2);
                    dig.getDrawing().drawlives();
                }
                nextbs2 += bonusscore;
            }
        }
        dig.getMain().increasePenalty();
        dig.getMain().increasePenalty();
        dig.getMain().increasePenalty();
    }

    protected void drawscores() {
        writenum(score1, 0, 0, 6, 3);
        if (dig.getMain().getNplayers() == 2)
            if (score2 < 100000l)
                writenum(score2, 236, 0, 6, 3);
            else
                writenum(score2, 248, 0, 6, 3);
    }

    protected void endofgame() {
        int i, j, z;
        addscore(0);
        if (dig.getMain().getcplayer() == 0)
            scoret = score1;
        else
            scoret = score2;
        if (scoret > scorehigh[11]) {
            dig.getPc().gclear();
            drawscores();
            dig.getMain().setPldispbuf("PLAYER ");
            if (dig.getMain().getcplayer() == 0)
                dig.getMain().appendPldispbuf("1");
            else
                dig.getMain().appendPldispbuf("2");
            dig.getDrawing().outtext(dig.getMain().getPldispbuf(), 108, 0, 2, true);
            dig.getDrawing().outtext(" NEW HIGH SCORE ", 64, 40, 2, true);
            getinitials();
            _updatescores(_submit(scoreinit[0], (int) scoret));
            shufflehigh();
//	savescores();
        } else {
            dig.getMain().cleartopline();
            dig.getDrawing().outtext("GAME OVER", 104, 0, 3, true);
            _updatescores(_submit("...", (int) scoret));
            dig.getSound().killsound();
            for (j = 0; j < 20; j++) /* Number of times screen flashes * 2 */
                for (i = 0; i < 2; i++) { //i<8;i++) {
                    dig.getSprite().setretr(true);
//		dig.org.digger.Pc.ginten(1);
                    dig.getPc().gpal(1 - (j & 1));
                    dig.getSprite().setretr(false);
                    for (z = 0; z < 111; z++) ; /* A delay loop */
                    dig.getPc().gpal(0);
//		dig.org.digger.Pc.ginten(0);
                    dig.getPc().ginten(1 - i & 1);
                    dig.newframe();
                }
            dig.getSound().setupsound();
            dig.getDrawing().outtext("         ", 104, 0, 3, true);
            dig.getSprite().setretr(true);
        }
    }

    private void flashywait(int n) {
/*  int i,gt,cx,p=0,k=1;
  int gap=19;
  dig.org.digger.Sprite.setretr(false);
  for (i=0;i<(n<<1);i++) {
	for (cx=0;cx<dig.getSound().volume;cx++) {
	  dig.org.digger.Pc.gpal(p=1-p);
	  for (gt=0;gt<gap;gt++);
	}
	} */

        try {
            Thread.sleep(n * 2);
        } catch (Exception e) {
        }

    }

    private int getinitial(int x, int y) {
        int i, j;
        dig.getInput().resetKeyPressed();
        dig.getPc().gwrite(x, y, '_', 3, true);
        for (j = 0; j < 5; j++) {
            for (i = 0; i < 40; i++) {
                if (dig.getInput().isKeyPressed(0x80))
                    return dig.getInput().getKeypressed();
                flashywait(15);
            }
            for (i = 0; i < 40; i++) {
                if (dig.getInput().isKeyPressed(0x80)) {
                    dig.getPc().gwrite(x, y, '_', 3, true);
                    return dig.getInput().getKeypressed();
                }
                flashywait(15);
            }
        }
        gotinitflag = true;
        return 0;
    }

    private void getinitials() {
        int k, i;
        dig.getDrawing().outtext("ENTER YOUR", 100, 70, 3, true);
        dig.getDrawing().outtext(" INITIALS", 100, 90, 3, true);
        dig.getDrawing().outtext("_ _ _", 128, 130, 3, true);
        scoreinit[0] = "...";
        dig.getSound().killsound();
        gotinitflag = false;
        for (i = 0; i < 3; i++) {
            k = 0;
            while (k == 0 && !gotinitflag) {
                k = getinitial(i * 24 + 128, 130);
                if (i != 0 && k == 8)
                    i--;
                k = dig.getInput().getasciikey(k);
            }
            if (k != 0) {
                dig.getPc().gwrite(i * 24 + 128, 130, k, 3, true);
                StringBuffer sb = new StringBuffer(scoreinit[0]);
                sb.setCharAt(i, (char) k);
                scoreinit[0] = sb.toString();
            }
        }
        dig.getInput().resetKeyPressed();
        for (i = 0; i < 20; i++)
            flashywait(15);
        dig.getSound().setupsound();
        dig.getPc().gclear();
        dig.getPc().gpal(0);
        dig.getPc().ginten(0);
        dig.newframe();    // needed by Java version!!
        dig.getSprite().setretr(true);
    }

    protected void initscores() {
        addscore(0);
    }

    protected void loadscores() {
        int p = 1, i, x;
        //readscores();
        for (i = 1; i < 11; i++) {
            for (x = 0; x < 3; x++)
                scoreinit[i] = "..."; //  scorebuf[p++];	--- zmienic
            p += 2;
            for (x = 0; x < 6; x++)
                highbuf[x] = scorebuf[p++];
            scorehigh[i + 1] = 0; //atol(highbuf);
        }
        if (scorebuf[0] != 's')
            for (i = 0; i < 11; i++) {
                scorehigh[i + 1] = 0;
                scoreinit[i] = "...";
            }
    }

    private String numtostring(long n) {
        int x;
        String p = "";
        for (x = 0; x < 6; x++) {
            p = String.valueOf(n % 10) + p;
            n /= 10;
            if (n == 0) {
                x++;
                break;
            }
        }
        for (; x < 6; x++)
            p = ' ' + p;
        return p;
    }

    public void run() {

        try {
            URL u = new URL(dig.getSubaddr() + '?' + substr);
            URLConnection uc = u.openConnection();
            uc.setUseCaches(false);
            uc.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            Object[][] sc = new Object[10][2];
            for (int i = 0; i < 10; i++) {
                sc[i][0] = br.readLine();
                sc[i][1] = new Integer(br.readLine());
            }
            br.close();
            scores = sc;
        } catch (Exception e) {
        }

    }

    protected void scorebonus() {
        addscore(1000);
    }

    protected void scoreeatm() {
        addscore(dig.getEatmsc() * 200);
        dig.shifftEatmsc();
    }

    protected void scoreemerald() {
        addscore(25);
    }

    protected void scoregold() {
        addscore(500);
    }

    protected void scorekill() {
        addscore(250);
    }

    protected void scoreoctave() {
        addscore(250);
    }

    protected void showtable() {
        int i, col;
        dig.getDrawing().outtext("HIGH SCORES", 16, 25, 3);
        col = 2;
        for (i = 1; i < 11; i++) {
            hsbuf = scoreinit[i] + "  " + numtostring(scorehigh[i + 1]);
            dig.getDrawing().outtext(hsbuf, 16, 31 + 13 * i, col);
            col = 1;
        }
    }

    private void shufflehigh() {
        int i, j;
        for (j = 10; j > 1; j--)
            if (scoret < scorehigh[j])
                break;
        for (i = 10; i > j; i--) {
            scorehigh[i + 1] = scorehigh[i];
            scoreinit[i] = scoreinit[i - 1];
        }
        scorehigh[j + 1] = scoret;
        scoreinit[j] = scoreinit[0];
    }

    protected void writecurscore(int bp6) {
        if (dig.getMain().getcplayer() == 0)
            writenum(score1, 0, 0, 6, bp6);
        else if (score2 < 100000l)
            writenum(score2, 236, 0, 6, bp6);
        else
            writenum(score2, 248, 0, 6, bp6);
    }

    private void writenum(long n, int x, int y, int w, int c) {
        int d, xp = (w - 1) * 12 + x;
        while (w > 0) {
            d = (int) (n % 10);
            if (w > 1 || d > 0)
                dig.getPc().gwrite(xp, y, d + '0', c, false);    //true
            n /= 10;
            w--;
            xp -= 12;
        }
    }

    protected void zeroscores() {
        score2 = 0;
        score1 = 0;
        scoret = 0;
        nextbs1 = bonusscore;
        nextbs2 = bonusscore;
    }
}