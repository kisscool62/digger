package org.digger;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.image.MemoryImageSource;

class Pc {

    private Digger dig;

    @Getter
    private Image[] image = new Image[2];

    @Setter
    @Getter
    private Image currentImage;

    @Getter
    private MemoryImageSource[] source = new MemoryImageSource[2];

    @Getter
    @Setter
    private MemoryImageSource currentSource;

    @Getter
    private int width = 320;

    @Getter
    private int height = 200;
    private int size = width * height;

    @Getter
    @Setter
    private int[] pixels;

    @Getter
    private byte[][][] pal = {

            {{0, (byte) 0x00, (byte) 0xAA, (byte) 0xAA},
                    {0, (byte) 0xAA, (byte) 0x00, (byte) 0x54},
                    {0, (byte) 0x00, (byte) 0x00, (byte) 0x00}},

            {{0, (byte) 0x54, (byte) 0xFF, (byte) 0xFF},
                    {0, (byte) 0xFF, (byte) 0x54, (byte) 0xFF},
                    {0, (byte) 0x54, (byte) 0x54, (byte) 0x54}}};

    Pc(Digger d) {
        dig = d;
    }

    protected void gclear() {
        for (int i = 0; i < size; i++)
            pixels[i] = 0;
        currentSource.newPixels();
    }

    protected long gethrt() {
        return System.currentTimeMillis();
    }

    protected int getkips() {
        return 0;        // phony
    }

    protected void ggeti(int x, int y, short[] p, int w, int h) {

        int src = 0;
        int dest = y * width + (x & 0xfffc);

        for (int i = 0; i < h; i++) {
            int d = dest;
            for (int j = 0; j < w; j++) {
                p[src++] = (short) ((((((pixels[d] << 2) | pixels[d + 1]) << 2) | pixels[d + 2]) << 2) | pixels[d + 3]);
                d += 4;
                if (src == p.length)
                    return;
            }
            dest += width;
        }

    }

    protected int ggetpix(int x, int y) {
        int ofs = width * y + x & 0xfffc;
        return (((((pixels[ofs] << 2) | pixels[ofs + 1]) << 2) | pixels[ofs + 2]) << 2) | pixels[ofs + 3];
    }

    protected void ginit() {
    }

    protected void ginten(int inten) {
        currentSource = source[inten & 1];
        currentImage = image[inten & 1];
        currentSource.newPixels();
    }

    protected void gpal(int pal) {
    }

    protected void gputi(int x, int y, short[] p, int w, int h) {
        gputi(x, y, p, w, h, true);
    }

    protected void gputi(int x, int y, short[] p, int w, int h, boolean b) {

        int src = 0;
        int dest = y * width + (x & 0xfffc);

        for (int i = 0; i < h; i++) {
            int d = dest;
            for (int j = 0; j < w; j++) {
                short px = p[src++];
                pixels[d + 3] = px & 3;
                px >>= 2;
                pixels[d + 2] = px & 3;
                px >>= 2;
                pixels[d + 1] = px & 3;
                px >>= 2;
                pixels[d] = px & 3;
                d += 4;
                if (src == p.length)
                    return;
            }
            dest += width;
        }

    }

    protected void gputim(int x, int y, int ch, int w, int h) {

        short[] spr = CGAgrafx.cgatable[ch * 2];
        short[] msk = CGAgrafx.cgatable[ch * 2 + 1];

        int src = 0;
        int dest = y * width + (x & 0xfffc);

        for (int i = 0; i < h; i++) {
            int d = dest;
            for (int j = 0; j < w; j++) {
                short px = spr[src];
                short mx = msk[src];
                src++;
                if ((mx & 3) == 0)
                    pixels[d + 3] = px & 3;
                px >>= 2;
                if ((mx & (3 << 2)) == 0)
                    pixels[d + 2] = px & 3;
                px >>= 2;
                if ((mx & (3 << 4)) == 0)
                    pixels[d + 1] = px & 3;
                px >>= 2;
                if ((mx & (3 << 6)) == 0)
                    pixels[d] = px & 3;
                d += 4;
                if (src == spr.length || src == msk.length) {
                    return;
                }
            }
            dest += width;
        }

    }

    protected void gtitle() {

        int src = 0, dest = 0, plus = 0;

        while (true) {

            if (src >= CGAgrafx.cgatitledat.length)
                break;

            int b = CGAgrafx.cgatitledat[src++], l, c;

            if (b == 0xfe) {
                l = CGAgrafx.cgatitledat[src++];
                if (l == 0)
                    l = 256;
                c = CGAgrafx.cgatitledat[src++];
            } else {
                l = 1;
                c = b;
            }

            for (int i = 0; i < l; i++) {
                int px = c, adst = 0;
                if (dest < 32768)
                    adst = (dest / 320) * 640 + dest % 320;
                else
                    adst = 320 + ((dest - 32768) / 320) * 640 + (dest - 32768) % 320;
                pixels[adst + 3] = px & 3;
                px >>= 2;
                pixels[adst + 2] = px & 3;
                px >>= 2;
                pixels[adst + 1] = px & 3;
                px >>= 2;
                pixels[adst + 0] = px & 3;
                dest += 4;
                if (dest >= 65535)
                    break;
            }

            if (dest >= 65535)
                break;

        }

    }

    protected void gwrite(int x, int y, int ch, int c) {
        gwrite(x, y, ch, c, false);
    }

    protected void gwrite(int x, int y, int ch, int c, boolean upd) {

        int dest = x + y * width, ofs = 0, color = c & 3;

        ch -= 32;
        if ((ch < 0) || (ch > 0x5f))
            return;

        short[] chartab = Alpha.ascii2cga[ch];

        if (chartab == null)
            return;

        for (int i = 0; i < 12; i++) {
            int d = dest;
            for (int j = 0; j < 3; j++) {
                int px = chartab[ofs++];
                pixels[d + 3] = px & color;
                px >>= 2;
                pixels[d + 2] = px & color;
                px >>= 2;
                pixels[d + 1] = px & color;
                px >>= 2;
                pixels[d] = px & color;
                d += 4;
            }
            dest += width;
        }

        if (upd)
            currentSource.newPixels(x, y, 12, 12);

    }
}