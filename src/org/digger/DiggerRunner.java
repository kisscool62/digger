package org.digger;

/**
 * Created by IntelliJ IDEA.
 * User: kisscool
 * Date: 4/9/12
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class DiggerRunner {
    public static void main(String [] args) {
        Digger digger = new Digger();
        digger.init();
        digger.start();
    }
}
