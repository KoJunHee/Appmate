package org.androidtown.appmate.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.androidtown.appmate.R;
import org.androidtown.appmate.component.Config;
import org.androidtown.appmate.model.ProjectApplyResult;
import org.androidtown.appmate.model.ProjectDetailResult;
import org.androidtown.appmate.model.ProjectRegisterResult;
import org.androidtown.appmate.network.ApplicationController;
import org.androidtown.appmate.network.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProjectDetailActivity extends AppCompatActivity {

    /*******************************************전역*******************************************/
    private NetworkService service;
    private ImageView progectImg;
    private ImageView ivUerImg;
    private TextView tvProjectDday;
    private TextView tvProjectTitle;
    private TextView tvUserName;
    private TextView tvProjectExplanation;
    private CheckBox cbLikeButton;
    private ImageView dma;
    private ImageView ivProjectApply;
    private int projectId;
    private int isApplied;
    private int userId;
    private int dday;

    /*******************************************onCreate*******************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //해당 프로젝트 id값 가져오기
        Intent intent = getIntent();
        projectId = intent.getExtras().getInt("projectId");

        //toolBar설정
        Toolbar toolBar;
        toolBar = (Toolbar) findViewById(R.id.tbProjecDetail);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //뒤로 가기 버튼 클릭
        LinearLayout llback = (LinearLayout) findViewById(R.id.llback);
        llback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //댓글 클릭
        ImageView ivComment = (ImageView) findViewById(R.id.ivComment);
        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectDetailActivity.this, CommentActivity.class);
                intent.putExtra("projectID", projectId);
                startActivity(intent);
            }
        });

        //찜한사람 클릭
        ImageView ivLikePeople = (ImageView) findViewById(R.id.ivLikePeople);
        ivLikePeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectDetailActivity.this, LikePeopleActivity.class);
                intent.putExtra("projectID", projectId);
                startActivity(intent);
            }
        });


        //분야및스킬 클릭
        ImageView ivFieldSkill = (ImageView) findViewById(R.id.ivFieldSkill);
        ivFieldSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectDetailActivity.this, ProjectPartSkill.class);
                intent.putExtra("projectID", projectId);
                startActivity(intent);
            }
        });

        //회원 사진 클릭 : 해당 프로필로 이동
        ImageView ivUerImg = (ImageView) findViewById(R.id.ivUerImg);
        ivUerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectDetailActivity.this, OtherUserPage.class);
                intent.putExtra("userID", userId );
                startActivity(intent);
            }
        });


        //findView
        findView();


        //프로젝트 상세 정보 가져오기
        getProjectDetail();
    }
    /**********************************상세 정보 가져오기 통신******************************************/
    private void getProjectDetail() {
        Call<ProjectDetailResult> requestProjectDetail = service.getProjectDetail(Config.userID, projectId);
        requestProjectDetail.enqueue(new Callback<ProjectDetailResult>() {
            @Override
            public void onResponse(Call<ProjectDetailResult> call, Response<ProjectDetailResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {

                        Glide.with(getApplicationContext())
                                .load(response.body().data.project.ProjectBackground.projectBackgroundImage)
                                .into(progectImg);
                        Glide.with(getApplicationContext())
                                .load(response.body().data.project.Owner.userImage)
                                .into(ivUerImg);
                        tvProjectTitle.setText(response.body().data.project.projectName);
                        tvUserName.setText(response.body().data.project.Owner.userNickname);
                        tvProjectExplanation.setText(response.body().data.project.projectDescription);
                        userId = response.body().data.project.Owner.id;
                        Log.d("userID", "서버에서 받아온 올린사람 id : " + userId);
                        dday = response.body().data.project.dDay;

                        //======================================================================//
                        //이 프로젝트를 올린게 나
                        if(response.body().data.project.Owner.id==Config.userID)
                        {
                            //d-day인경우
                            if(response.body().data.project.dDay==0) {
                                //디데이 인경우
                                tvProjectDday.setText("day");
                                ivProjectApply.setImageResource(R.drawable.pick);
                            }
                            //d-day가 아닌경우
                            else {
                                tvProjectDday.setText(Integer.toString(response.body().data.project.dDay));
                                ivProjectApply.setImageResource(R.drawable.pick);
                            }
                        }
                        //이 프로젝트를 올린게 내가 아니야
                        else
                        {
                            //d-day인경우
                            if(response.body().data.project.dDay==0) {
                                //디데이 인경우
                                tvProjectDday.setText("day");
                                //지원 여부
                                isApplied = response.body().data.project.isApplied;
                                if(isApplied==0)
                                    ivProjectApply.setImageResource(R.drawable.project_apply_button_image);
                                else
                                    ivProjectApply.setImageResource(R.drawable.apply_cancel);
                            }
                            //d-day가 아닌경우
                            else {
                                tvProjectDday.setText(Integer.toString(response.body().data.project.dDay));
                                //지원 여부
                                isApplied = response.body().data.project.isApplied;
                                if(isApplied==0)
                                    ivProjectApply.setImageResource(R.drawable.project_apply_button_image);
                                else
                                    ivProjectApply.setImageResource(R.drawable.apply_cancel);
                            }
                        }

                        //=================================하단//=================================//
                        //이 프로젝트를 올린게 나
                        if(userId==Config.userID)
                        {
                            Log.d("onClick", "1111111111111");
                            ivProjectApply.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("onClick", "2222222222");
                                    Intent intent = new Intent(ProjectDetailActivity.this, CheckApplyUserActivity.class);
                                    intent.putExtra("dday",  dday);
                                    intent.putExtra("projectId", projectId);
                                    startActivity(intent);
                                }
                            });
                        }
                        //이 프로젝트를 올린게 내가 x
                        else
                        {
                            ivProjectApply.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    registerProject();
                                }
                            });
                        }















                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectDetailResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }
    /**********************************프로젝트 등록 통신******************************************/

    private void registerProject() {

        Boolean sendingFlag;
        if(isApplied==0)
            sendingFlag=true;
        else
            sendingFlag=false;

        Call<ProjectApplyResult> requestRegister = service.applyResult(Config.userID, projectId, sendingFlag);
        requestRegister.enqueue(new Callback<ProjectApplyResult>() {
            @Override
            public void onResponse(Call<ProjectApplyResult> call, Response<ProjectApplyResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success"))
                        ShowDialog();
                }
            }
            @Override
            public void onFailure(Call<ProjectApplyResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }

    /**********************************findView******************************************/
    private void findView() {
        progectImg = (ImageView) findViewById(R.id.progectImg);
        ivUerImg = (ImageView) findViewById(R.id.ivUerImg);
        tvProjectDday = (TextView) findViewById(R.id.tvProjectDday);
        tvProjectTitle = (TextView) findViewById(R.id.tvProjectTitle);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvProjectExplanation = (TextView) findViewById(R.id.tvProjectExplanation);
        cbLikeButton = (CheckBox) findViewById(R.id.cbLikeButton);
        ivProjectApply = (ImageView) findViewById(R.id.ivProjectApply);
        dma = (ImageView) findViewById(R.id.dma);
    }

    /*************************************지원하기 다이얼로그************************************/
    private void ShowDialog() {

        //현재 빨간색 버튼이면 "지원원료되었습니다 다이얼로그 띄움
        if(isApplied==0){

            LayoutInflater dialog = LayoutInflater.from(this);
            final View dialogLayout = dialog.inflate(R.layout.dialog_apply, null);
            final Dialog myDialog = new Dialog(this);

            myDialog.setContentView(dialogLayout);
            myDialog.setCancelable(true);

            myDialog.show();

            ImageView btn_cancel = (ImageView) dialogLayout.findViewById(R.id.btnApplyCancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getProjectDetail();
                    myDialog.cancel();
                }
            });
        }

        //현재 보라색 버튼이면 "취소되었습니다 다이얼로그 띄움
        else if(isApplied==1){
            LayoutInflater dialog = LayoutInflater.from(this);
            final View dialogLayout = dialog.inflate(R.layout.dialog_apply_cancel, null);
            final Dialog myDialog = new Dialog(this);

            myDialog.setContentView(dialogLayout);
            myDialog.show();

            ImageView btn_cancel = (ImageView) dialogLayout.findViewById(R.id.btnApplyCancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getProjectDetail();
                    myDialog.cancel();
                }
            });
        }



    }
}


