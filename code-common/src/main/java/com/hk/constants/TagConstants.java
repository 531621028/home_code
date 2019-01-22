package com.hk.constants;

/**
 * Created by kang on 2019/1/16.
 */
public interface TagConstants {

    interface AnnounceStatus {
        /**
         * 有效的
         */
        int VALID = 1;
        /**
         * 无效的
         */
        int INVALID = 2;
    }

    interface MsgType {
        /**
         * 自己的
         */
        int MINE = 1;
        /**
         * 其他人的
         */
        int OTHER = 2;
    }


    interface TagStatus {
        /**
         * 没有被绑定
         */
        int NONE = 1;
        /**
         * 自己的
         */
        int OWNED = 2;
        /**
         * 被其他人绑定
         */
        int OTHER = 3;
    }
}
