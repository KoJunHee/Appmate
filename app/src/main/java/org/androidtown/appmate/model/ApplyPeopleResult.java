package org.androidtown.appmate.model;

import java.util.ArrayList;

/**
 * Created by KoJunHee on 2017-06-21.
 */

public class ApplyPeopleResult {
    public String msg;
    public Data data;


    public class Data{
        public ArrayList<User> users;
    }

    public class User{
        public int projectMemberNumber;
        public ArrayList<ApplyPeopleListData>Applicants;
    }

}
