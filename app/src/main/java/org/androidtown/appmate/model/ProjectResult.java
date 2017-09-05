package org.androidtown.appmate.model;

import java.util.ArrayList;

/**
 * Created by KoJunHee on 2017-06-15.
 */

public class ProjectResult {
    public String msg;
    public Data data;

    public class Data{
        public ArrayList<ProjectListData> projects;
    }
}
