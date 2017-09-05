package org.androidtown.appmate.model;

import java.util.ArrayList;

/**
 * Created by KoJunHee on 2017-06-15.
 */

public class ProjectListData {
    public int id;
    public String projectName;
    public int dDay;
    public int isLike;
    public OWner Owner;
    public PRojectBackground ProjectBackground;

    public class OWner{
        public int id;
        public String userNickname;
        public String userImage;
    }

    public class PRojectBackground{
        public String projectBackgroundImage;

    }
}
