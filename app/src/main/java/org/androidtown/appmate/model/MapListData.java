package org.androidtown.appmate.model;

import java.util.ArrayList;

/**
 * Created by KoJunHee on 2017-06-19.
 */

public class MapListData {
    public int id;
    public String userNickname;
    public String userImage;
    public String userFirstJob;
    public double latitude;
    public double longitude;
    public double distance;
    public ArrayList<Skill> Skills;

    public class Skill{
        public String skillName;
    }
}
