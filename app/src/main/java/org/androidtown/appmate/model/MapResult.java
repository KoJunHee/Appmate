package org.androidtown.appmate.model;

import java.util.ArrayList;

/**
 * Created by KoJunHee on 2017-06-19.
 */

public class MapResult {
    public String msg;
    public Data data;


    public class Data {
        public ArrayList<MapListData> users;
    }

}
