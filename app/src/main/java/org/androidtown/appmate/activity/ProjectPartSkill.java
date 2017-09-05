package org.androidtown.appmate.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.androidtown.appmate.R;
import org.androidtown.appmate.component.Config;
import org.androidtown.appmate.model.Hash;
import org.androidtown.appmate.model.ProjectField;
import org.androidtown.appmate.model.ProjectFieldSkill;
import org.androidtown.appmate.model.Skill;
import org.androidtown.appmate.network.ApplicationController;
import org.androidtown.appmate.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.androidtown.appmate.R.layout.list_item_tag;

public class ProjectPartSkill extends AppCompatActivity {

    /*******************************************전역*******************************************/
    private RecyclerView recyclerView01, recyclerView02;
    private RecyclerAdapterForSkill recyclerAdapterForSkill;
    private RecyclerAdapterForField recyclerAdapterForField;
    private GridLayoutManager layoutManager;
    private NetworkService service;
    private ArrayList<Skill> skills;
    private ArrayList<ProjectField> projectFields;
    private int projectId;

    /*******************************************onCreate*******************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_part_skill);

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //해당 프로젝트 id값 가져오기
        Intent intent = getIntent();
        projectId = intent.getExtras().getInt("projectID");

        //뒤로 가기
        LinearLayout llpartskill = (LinearLayout) findViewById(R.id.llpartskill);
        llpartskill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //ArrayList 생성
        skills = new ArrayList<>();
        projectFields = new ArrayList<>();


        /**************************************프로젝트 관련 스킬*************************************/
        //레이아웃 매니저 설정
        recyclerView01 = (RecyclerView) findViewById(R.id.rvPartSkill01);
        layoutManager = new GridLayoutManager(this, 3);
        recyclerView01.setLayoutManager(layoutManager);

        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapterForSkill = new RecyclerAdapterForSkill(skills);
        recyclerView01.setAdapter(recyclerAdapterForSkill);

        /**************************************프로젝트 관련 분야*************************************/
        //레이아웃 매니저 설정
        recyclerView02 = (RecyclerView) findViewById(R.id.rvPartSkill02);
        layoutManager = new GridLayoutManager(this, 3);
        recyclerView02.setLayoutManager(layoutManager);


        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapterForField = new RecyclerAdapterForField(projectFields);
        recyclerView02.setAdapter(recyclerAdapterForField);


        //서버에서 태그 목록 가져오기
        getTags();
    }


    /*************************************tags 목록 가져오기**********************/
    public void getTags() {
        Call<ProjectFieldSkill> requestSkillField = service.getProjectSkillField(Config.userID, projectId);
        requestSkillField.enqueue(new Callback<ProjectFieldSkill>() {
            @Override
            public void onResponse(Call<ProjectFieldSkill> call, Response<ProjectFieldSkill> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {
                        skills = response.body().data.fields.get(0).Skills;
                        recyclerAdapterForSkill.setAdapter(skills);

                        projectFields = response.body().data.fields.get(0).ProjectFields;
                        recyclerAdapterForField.setAdapter(projectFields);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectFieldSkill> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });

    }


    /***********************************Adapter-관련스킬**********************************/
    class RecyclerAdapterForSkill extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<Skill> skills;

        public RecyclerAdapterForSkill(ArrayList<Skill> skills) {
            this.skills = skills;
        }

        public void setAdapter(ArrayList<Skill> skills) {
            this.skills = skills;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(list_item_tag, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Skill skill = skills.get(position);
            String key = skill.skillName;
            Hash hash = new Hash();
            hash.hashMap();
            holder.ivTag.setImageResource(hash.map.get(key));
        }

        @Override
        public int getItemCount() {
            return skills.size();
        }
    }

    /***********************************Adapter-관련분야**********************************/
    class RecyclerAdapterForField extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<ProjectField> projectFields;

        public RecyclerAdapterForField(ArrayList<ProjectField> projectFields) {
            this.projectFields = projectFields;
        }

        public void setAdapter(ArrayList<ProjectField> projectFields) {
            this.projectFields = projectFields;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(list_item_tag, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ProjectField projectField = projectFields.get(position);
            String key = projectField.projectFieldName;
            Hash hash = new Hash();
            hash.hashMap();
            holder.ivTag.setImageResource(hash.map.get(key));
        }

        @Override
        public int getItemCount() {
            return projectFields.size();
        }
    }

    /**********************************ViewHolder********************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTag;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivTag = (ImageView) itemView.findViewById(R.id.ivTag);
        }
    }
}

