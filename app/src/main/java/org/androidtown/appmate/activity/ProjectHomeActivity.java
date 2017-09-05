package org.androidtown.appmate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.androidtown.appmate.R;
import org.androidtown.appmate.component.Config;
import org.androidtown.appmate.model.ProjectListData;
import org.androidtown.appmate.model.ProjectResult;
import org.androidtown.appmate.network.ApplicationController;
import org.androidtown.appmate.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectHomeActivity extends BottomNavigationParentActivity {

    /*******************************************전역*******************************************/
    FloatingActionButton floatingActionButton;
    NetworkService service;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<ProjectListData> projects;
    private int projectID;
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    /*****************************************bottom navigation************************************/
    @Override
    public int getCurrentActivityLayoutName() {
        return R.layout.activity_project_home;
    }

    @Override
    public int getCurrentSelectedBottomMenuItemID() {
        return R.id.firstTab;
    }


    /*******************************************onCreate*******************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //서비스 객체
        service = ApplicationController.getInstance().getNetworkService();


        //Sort 이미지 클릭 ------------------- 구현 예정
        ImageView ivSortProjectHome = (ImageView) findViewById(R.id.ivSortProjectHome);
        ivSortProjectHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProjectHomeActivity.this, "구현예정 ", Toast.LENGTH_SHORT).show();
            }
        });

        //프로젝트 리스트 레이아웃 매니저 설정
        recyclerView = (RecyclerView) findViewById(R.id.rvProjectList);
        layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //어레이 리스트 생성 - 프로젝트
        projects = new ArrayList<>();

        //프로젝트 리스트 어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(projects);
        recyclerView.setAdapter(recyclerAdapter);

        //플로팅 버튼 설정 - 우측 하단
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fabProjectHomeActivity);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && floatingActionButton.isShown()) {
                    floatingActionButton.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    floatingActionButton.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectHomeActivity.this, ProjectRegisterActivity.class);
                startActivity(intent);
            }
        });

        //프로젝트 리스트 가져오기
        getProjectLists();
    }


    /***********************************프로젝트 리스트 가져오기*********************************/
    public void getProjectLists() {
        Call<ProjectResult> requestProjectLists = service.getProjectList(Config.userID);

        requestProjectLists.enqueue(new Callback<ProjectResult>() {
            @Override
            public void onResponse(Call<ProjectResult> call, Response<ProjectResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {
                        projects = response.body().data.projects;
                        recyclerAdapter.setAdapter(projects);
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }


    /***********************************Adapter***************************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<ProjectListData> projectListDatas;

        public RecyclerAdapter(ArrayList<ProjectListData> projectListDatas) {
            this.projectListDatas = projectListDatas;
        }

        public void setAdapter(ArrayList<ProjectListData> projectListDatas) {
            this.projectListDatas = projectListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_project_project_home, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            Log.d("position : ", Integer.toString(position));
            final ProjectListData projectListData = projectListDatas.get(position);

            //프로젝트 ID 저장
            projectID = projectListData.id;

            //유저 이미지
            Glide.with(getApplicationContext())
                    .load(projectListData.Owner.userImage)
//                    .override(24, 24)
                    .into(holder.ivThumbNail);

            //프로젝트 이미지
            Glide.with(getApplicationContext())
                    .load(projectListData.ProjectBackground.projectBackgroundImage)
                    .override(172, 151)
                    .thumbnail(0.1f)
                    .into(holder.ivProjectImage);

            //프로젝트 이름
            holder.tvProjectTitle.setText(projectListData.projectName);

            //디데이
            if(projectListData.dDay==0)
                holder.tvDday.setText("DAY");
            else
                holder.tvDday.setText(String.valueOf(projectListData.dDay));

            //유저 네임
            holder.tvNickName.setText(projectListData.Owner.userNickname);


            //클릭시 상세화면으로 이동, 클릭한 프로젝트 아이디 전달
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProjectHomeActivity.this, ProjectDetailActivity.class);
                    intent.putExtra("projectId", projectListData.id);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return projects.size();
        }
    }

    /**********************************ViewHolder**********************************/
    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProjectImage, ivThumbNail;
        TextView tvProjectTitle, tvNickName, tvDday;
        View vLine;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivProjectImage = (ImageView) itemView.findViewById(R.id.ivProjectImage);
            tvProjectTitle = (TextView) itemView.findViewById(R.id.tvProjectTitle);
            tvNickName = (TextView) itemView.findViewById(R.id.tvNickName);
            vLine = itemView.findViewById(R.id.vLine);
            ivThumbNail = (ImageView) itemView.findViewById(R.id.ivThumbnaill);
            tvDday = (TextView) itemView.findViewById(R.id.tvDday);
        }
    }

    /*********************************back 두번 누렀을 경우********************************/
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        //Back키 두번 연속 클릭 시 앱 종료
        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로 가기 키을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}



