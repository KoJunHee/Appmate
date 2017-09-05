package org.androidtown.appmate.model;

/**
 * Created by KoJunHee on 2017-06-14.
 */

public class CommentListData {

    public String commentContents;
    public int dateDiff;
    public String timeDiff;
    public USer User;

    public class USer {
        public int id;
        public String userNickname;
        public String userImage;
    }
}


