package com.tiangou.javacvtest;

import java.io.Serializable;

public class Result implements Serializable {

    public Result(boolean succeed, long timeUsed) {
        this.succeed = succeed;
        this.timeUsed = timeUsed;
    }

    public boolean succeed;
    public long timeUsed;
}
