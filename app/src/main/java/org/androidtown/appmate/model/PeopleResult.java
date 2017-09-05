package org.androidtown.appmate.model;

import java.util.ArrayList;

/**
 * Created by KoJunHee on 2017-06-12.
 */

public class PeopleResult {
    public String msg;
    public Data data;

    public class Data{
        public ArrayList<PeopleListData> users;
    }
}

