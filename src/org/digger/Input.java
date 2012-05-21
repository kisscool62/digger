package org.digger;

import lombok.Getter;
import lombok.Setter;

class Input {

    private Digger dig;

    private boolean leftpressed = false;
    private boolean rightpressed = false;
    private boolean uppressed = false;
    private boolean downpressed = false;
    private boolean f1pressed = false;

    @Setter
    private boolean firepressed = false;
    private boolean minuspressed = false;
    private boolean pluspressed = false;
    private boolean f10pressed= false;

    @Getter
    @Setter
    private boolean escape = false;

    @Getter
    private int keypressed = 0;

    @Getter
    private int akeypressed;
    private int dynamicdir = -1;
    private int staticdir = -1;
    private int joyx = 0;
    private int joyy = 0;

    private boolean joybut1 = false;
    private boolean joybut2 = false;

    private int keydir = 0;
    private int jleftthresh = 0;
    private int jupthresh = 0;
    private int jrightthresh = 0;
    private int jdownthresh = 0;
    private int joyanax = 0;
    private int joyanay = 0;

    @Getter
    private boolean firepflag = false;

    private boolean joyflag = false;


    Input(Digger d) {
        dig = d;
    }

    public void resetKeyPressed(){
        keypressed = 0;
    }

    public void resetAKeyPressed(){
        akeypressed = 0;
    }

    void checkkeyb() {
        if (pluspressed) {
            if (dig.getFrametime() > Digger.MIN_RATE)
                dig.setFrametime(dig.getFrametime() - 5);
        }
        if (minuspressed) {
            if (dig.getFrametime() < Digger.MAX_RATE)
                dig.setFrametime(dig.getFrametime() + 5);
        }
        if (f10pressed)
            escape = true;

/*  while (kbhit()) {
	akeypressed=getkey();
	switch (akeypressed) {
	  case 321: // F7
		musicflag=!musicflag;
		break;
	  case 323: // F9
		soundflag=!soundflag;
		break;
	  case 324: // F10
		escape=true;
	}
  } */
    }

    public boolean isKeyPressed(int key){
        return (dig.getInput().keypressed & key) == 0 && dig.getInput().keypressed != 0;
    }

    void detectjoy() {
        joyflag = false;
        staticdir = dynamicdir = -1;
    }

    int getasciikey(int make) {
        int k;
        if ((make == ' ') || ((make >= 'a') && (make <= 'z')) || ((make >= '0') && (make <= '9')))
            return make;
        else
            return 0;
/*  if (make<2 || make>=58)
	return 0; 
  if (kbhit())
	k=getkey();
  else
	return 0;
  if (k>='a' && k<='A')
	k+='A'-'a'; */
    }

    int getdir() {
        int bp2 = keydir;
/*  if (joyflag) {
	bp2=-1;
	if (joyx<jleftthresh)
	  bp2=4;
	if (joyx>jrightthresh)
	  bp2=0;
	if (joyx>=jleftthresh && joyx<=jrightthresh) {
	  if (joyy<jupthresh)
		bp2=2;
	  if (joyy>jdownthresh)
		bp2=6;
	}
  } */
        return bp2;
    }

    void initkeyb() {
    }

    void Key_downpressed() {
        downpressed = true;
        dynamicdir = staticdir = 6;
    }

    void Key_downreleased() {
        downpressed = false;
        if (dynamicdir == 6)
            setdirec();
    }

    void Key_f1pressed() {
        firepressed = true;
        f1pressed = true;
    }

    void Key_f1released() {
        f1pressed = false;
    }

    void Key_leftpressed() {
        leftpressed = true;
        dynamicdir = staticdir = 4;
    }

    void Key_leftreleased() {
        leftpressed = false;
        if (dynamicdir == 4)
            setdirec();
    }

    void Key_rightpressed() {
        rightpressed = true;
        dynamicdir = staticdir = 0;
    }

    void Key_rightreleased() {
        rightpressed = false;
        if (dynamicdir == 0)
            setdirec();
    }

    void Key_uppressed() {
        uppressed = true;
        dynamicdir = staticdir = 2;
    }

    void Key_upreleased() {
        uppressed = false;
        if (dynamicdir == 2)
            setdirec();
    }

    void processkey(int key) {
        keypressed = key;
        if (key > 0x80)
            akeypressed = key & 0x7f;
        switch (key) {
            case 0x4b:
                Key_leftpressed();
                break;
            case 0xcb:
                Key_leftreleased();
                break;
            case 0x4d:
                Key_rightpressed();
                break;
            case 0xcd:
                Key_rightreleased();
                break;
            case 0x48:
                Key_uppressed();
                break;
            case 0xc8:
                Key_upreleased();
                break;
            case 0x50:
                Key_downpressed();
                break;
            case 0xd0:
                Key_downreleased();
                break;
            case 0x3b:
                Key_f1pressed();
                break;
            case 0xbb:
                Key_f1released();
                break;
            case 0x78:
                f10pressed = true;
                break;
            case 0xf8:
                f10pressed = false;
                break;
            case 0x2b:
                pluspressed = true;
                break;
            case 0xab:
                pluspressed = false;
                break;
            case 0x2d:
                minuspressed = true;
                break;
            case 0xad:
                minuspressed = false;
                break;
        }
    }

    void readdir() {
/*  int j; */
        keydir = staticdir;
        if (dynamicdir != -1)
            keydir = dynamicdir;
        staticdir = -1;
        if (f1pressed || firepressed)
            firepflag = true;
        else
            firepflag = false;
        firepressed = false;
/*  if (joyflag) {
	incpenalty();
	incpenalty();
	joyanay=0;
	joyanax=0;
	for (j=0;j<4;j++) {
	  readjoy();
	  joyanax+=joyx;
	  joyanay+=joyy;
	}
	joyx=joyanax>>2;
	joyy=joyanay>>2;
	if (joybut1)
	  firepflag=true;
	else
	  firepflag=false;
  } */
    }

    void readjoy() {
    }

    void setdirec() {
        dynamicdir = -1;
        if (uppressed) dynamicdir = staticdir = 2;
        if (downpressed) dynamicdir = staticdir = 6;
        if (leftpressed) dynamicdir = staticdir = 4;
        if (rightpressed) dynamicdir = staticdir = 0;
    }

    boolean teststart() {
/*  int j; */
        boolean startf = false;
/*  if (joyflag) {
	readjoy();
	if (joybut1)
	  startf=true;
  }  */
        if (keypressed != 0 && (keypressed & 0x80) == 0 && keypressed != 27) {
            startf = true;
            joyflag = false;
            keypressed = 0;
        }
        if (!startf)
            return false;
/*  if (joyflag) {
	joyanay=0;
	joyanax=0;
	for (j=0;j<50;j++) {
	  readjoy();
	  joyanax+=joyx;
	  joyanay+=joyy;
	}
	joyx=joyanax/50;
	joyy=joyanay/50;
	jleftthresh=joyx-35;
	if (jleftthresh<0)
	  jleftthresh=0;
	jleftthresh+=10;
	jupthresh=joyy-35;
	if (jupthresh<0)
	  jupthresh=0;
	jupthresh+=10;
	jrightthresh=joyx+35;
	if (jrightthresh>255)
	  jrightthresh=255;
	jrightthresh-=10;
	jdownthresh=joyy+35;
	if (jdownthresh>255)
	  jdownthresh=255;
	jdownthresh-=10;
	joyanax=joyx;
	joyanay=joyy;
  } */
        return true;
    }
}