package org.androidtown.appmate.model;

import java.util.ArrayList;

/**
 * Created by KoJunHee on 2017-06-17.
 */

public class MyPageData {
    public String userNickname;
    public String userFirstJob;
    public String userSecondJob;
    public String userThirdJob;
    public String introduction;
    public String portfolio;
    public String userImage;
    public int highfiveNumber;
    public int passNumber;
    public ArrayList<MyPageHavingSkill> Skills;
    public ArrayList<MyPageWantedSkill> WantedSkills;
    public ArrayList<MyPageProjectField> ProjectFields;
    public IDentity Identity;
    public USerPlace UserPlace;
    public int followerNumber;
    public int followingNumber;


    public class MyPageHavingSkill {
        public String skillName;
    }

    public class MyPageWantedSkill {
        public String skillName;
    }

    public class MyPageProjectField {
        public String projectFieldName;
    }

    public class IDentity{
        public String identityName;
    }

    public class USerPlace{
        public String address;
    }
}




