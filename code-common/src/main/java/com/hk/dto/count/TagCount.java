package com.hk.dto.count;

/**
 * Created by kang on 2019/1/14.
 */
public class TagCount {
    private int tCnt; //消息总数
    private int urCnt; //未读消息总数

    public int gettCnt() {
        return tCnt;
    }

    public void settCnt(int tCnt) {
        this.tCnt = tCnt;
    }

    public int getUrCnt() {
        return urCnt;
    }

    public void setUrCnt(int urCnt) {
        this.urCnt = urCnt;
    }

    public void updateUrCnt(long tagId, int delta) {
        this.urCnt = urCnt + delta;
    }

    public void updateTCnt(long tagId, int delta) {
        this.tCnt = tCnt + delta;
    }

    public interface Field {
        String tCnt = "tCnt";
        String urCnt = "urCnt";
    }
}
