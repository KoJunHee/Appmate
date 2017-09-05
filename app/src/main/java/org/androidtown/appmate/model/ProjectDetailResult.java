package org.androidtown.appmate.model;

/**
 * Created by KoJunHee on 2017-06-15.
 */

public class ProjectDetailResult {
    public String msg;
    public Data data;

    public class Data {
        public Project project;
    }

    public class Project {
        public int id;
        public String projectName;
        public String projectDescription;
        public int dDay;
        public int isLike;
        public int isApplied;
        public Ownerr Owner;
        public PRojectBackground ProjectBackground;
    }

    public class Ownerr{
        public int id;
        public String userNickname;
        public String userImage;
    }

    public class PRojectBackground{
        public String projectBackgroundImage;
    }




}

