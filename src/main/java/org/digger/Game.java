package org.digger;

import lombok.Getter;
import lombok.Setter;

@Getter
class Game {

    @Setter
    private int lives;

    @Setter
    private int level;

    @Setter
    private boolean dead;

    private boolean levdone;

    protected void increaseLives() {
        lives++;
    }

    protected void increaseLevel() {
        level++;
    }

    protected void decreaseLives() {
        lives--;
    }

    protected void setLevelUnDone() {
        levdone = false;
    }

    protected void setLevelDone() {
        levdone = true;
    }

    protected void makeNotDead() {
        dead = false;
    }
}