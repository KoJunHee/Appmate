package org.androidtown.appmate.model;

import java.util.ArrayList;

/**
 * Created by KoJunHee on 2017-06-16.
 */

public class ProjectFieldSkill {
    public String msg;
    public Data data;

    public class Data{
        public ArrayList<Field> fields;
    }

    public class Field{
        public ArrayList<Skill> Skills;
        public ArrayList<ProjectField> ProjectFields;
    }
}
