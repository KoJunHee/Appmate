package org.androidtown.appmate.model;

import java.util.ArrayList;

/**
 * Created by KoJunHee on 2017-06-18.
 */

public class MyPageProjectResult {
    public String msg;
    public Data data;

    public class Data{
        public ArrayList<ProjectRunning> projectRunning;
        public ArrayList<ProjectComplete> projectComplete;
        public ArrayList<ProjectApply> projectApply;
        public ArrayList<ProjectRecruit> projectRecruit;
        public ArrayList<ProjectLike> projectLike;
    }


    public class ProjectRunning{
        public int id;
        public String projectName;
        public int dDay;
        public OWner Owner;
        public PRojectBackground ProjectBackground;
    }


    public class ProjectComplete{
        public int id;
        public String projectName;
        public int dDay;
        public OWner Owner;
        public PRojectBackground ProjectBackground;
    }


    public class ProjectApply{
        public int id;
        public String projectName;
        public int dDay;
        public OWner Owner;
        public PRojectBackground ProjectBackground;
    }

    public class ProjectRecruit{
        public int id;
        public String projectName;
        public int dDay;
        public OWner Owner;
        public PRojectBackground ProjectBackground;
    }

    public class ProjectLike{
        public int id;
        public String projectName;
        public int dDay;
        public OWner Owner;
        public PRojectBackground ProjectBackground;
    }

    public class OWner{
        public int id;
        public String userNickname;
        public String userImage;
    }

    public class PRojectBackground{
        public String projectBackgroundImage;
    }



}
