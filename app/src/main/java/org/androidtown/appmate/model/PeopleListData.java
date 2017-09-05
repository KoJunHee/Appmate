package org.androidtown.appmate.model;

import java.util.ArrayList;

/**
 * Created by KoJunHee on 2017-05-30.
 */

public class PeopleListData {
    public int id;
    public String userNickname;
    public String userImage;
    public String userFirstJob;
    public double distance;
    public ArrayList<Skill> Skills;

    public class Skill{
        public String skillName;
    }
}