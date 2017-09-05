package org.androidtown.appmate.model;

import java.util.ArrayList;

/**
 * Created by KoJunHee on 2017-06-14.
 */

public class CommentResult {
    public String msg;
    public Data data;

    public class Data{
        public ArrayList<CommentListData> comments;
    }
}


