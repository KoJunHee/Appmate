package org.androidtown.appmate.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.androidtown.appmate.R;
import org.androidtown.appmate.component.Config;
import org.androidtown.appmate.model.ApplyPeopleListData;
import org.androidtown.appmate.model.ApplyPeopleResult;
import org.androidtown.appmate.model.RecruitingResult;
import org.androidtown.appmate.network.ApplicationController;
import org.androidtown.appmate.network.NetworkService;
import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.androidtown.appmate.R.layout.list_item_like_people;

public class CheckApplyUserActivity extends AppCompatActivity {

    /*******************************************전역*******************************************/
    private NetworkService service;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private GridLayoutManager layoutManager;
    private ArrayList<ApplyPeopleListData> applyPeoples;
    private ArrayList<MyViewHolder> myViewHolders;
    private ArrayList<Integer> applicantsID;
    private TextView tvTitle, selectCancelButton, tvNumber, tvNumberText, tvFlag;
    private ImageView ivCheckApply;
    private int dday;
    private int projectId;
    private int checkNumber = 0;
    private Boolean selectStatus = false;

    /*******************************************onCreate*******************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_apply_user);

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();


        //해당 프로젝트 id, d-day
        Intent intent = getIntent();
        projectId = intent.getExtras().getInt("projectId");
        dday = intent.getExtras().getInt("dday");

        //findView
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        selectCancelButton = (TextView) findViewById(R.id.select);
        ivCheckApply = (ImageView) findViewById(R.id.ivCheckApply);
        tvNumber = (TextView) findViewById(R.id.tvNumber);
        tvNumberText = (TextView) findViewById(R.id.tvNumberText);
        tvFlag = (TextView) findViewById(R.id.tvFlag);

        //뒤로 가기
        LinearLayout llLikeBack = (LinearLayout) findViewById(R.id.llApplyBack);
        llLikeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //ArrayList 초기화
        applyPeoples = new ArrayList<>();
        myViewHolders = new ArrayList<>();
        applicantsID = new ArrayList<>();

        //레이아웃 매니저 설정
        recyclerView = (RecyclerView) findViewById(R.id.rvApplyPeople);
        layoutManager = new GridLayoutManager(CheckApplyUserActivity.this, 3);
        recyclerView.setLayoutManager(layoutManager);

        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(applyPeoples);
        recyclerView.setAdapter(recyclerAdapter);

        //초기에 상단 "선택"
        selectCancelButton.setText("선택");

        //상단에 "선택", "취소" 클릭하면
        selectCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //지금 상단에 "취소" 라고 보이면
                if (selectStatus == true)
                {
                    //각 뷰홀더에 접근
                    for (int i = 0; i < myViewHolders.size(); i++)
                    {
                        //아이템에 v 표시가 있으면
                        if (myViewHolders.get(i).tvFlag.getText().toString().equals("1"))
                        {
                            //v 표시 해제, opacity 선명하게, tvFlag를 0으로
                            myViewHolders.get(i).ivChecked.setVisibility(View.GONE);
                            myViewHolders.get(i).ivLikeUser.setAlpha(1f);

                            //flag 취소, 개수 초기화
                            myViewHolders.get(i).tvFlag.setText("0");
                            checkNumber=0;
                        }
                    }
                    selectCancelButton.setText("선택");
                    selectStatus = false;
                    tvNumber.setText("0");
                }
                //지금 상단에 "선택"이라고 보이면
                else
                {
                    selectCancelButton.setText("취소");
                    selectStatus = true;
                }
            }
        });


        //지원한 사람 목록 가져오기
        getApplyPeopleDatas();
    }


    /*************************************지원한 사람 목록 가져오기**********************/
    public void getApplyPeopleDatas() {
        Call<ApplyPeopleResult> requestAppltPeopleList = service.getApplyPeopleList(Config.userID, projectId);
        requestAppltPeopleList.enqueue(new Callback<ApplyPeopleResult>() {
            @Override
            public void onResponse(Call<ApplyPeopleResult> call, Response<ApplyPeopleResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {
                        applyPeoples = response.body().data.users.get(0).Applicants;
                        recyclerAdapter.setAdapter(applyPeoples);

                        //모집인원
                        int projectMemberNumber = response.body().data.users.get(0).projectMemberNumber;
                        tvNumberText.setText(Integer.toString(projectMemberNumber));

                        if (dday == 0) {
                            //디데이라면
                            tvTitle.setText("최종 팀원을 선택해주세요");
                            ivCheckApply.setImageResource(R.drawable.select_impossible);
                        }
                        //디데이가 아니라면
                        else {
                            tvTitle.setText("프로젝트에 지원한 사람들");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApplyPeopleResult> call, Throwable t) {
            }
        });

    }


    /***********************************Adapter**********************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<ApplyPeopleListData> applyPeopleListDatas;


        public RecyclerAdapter(ArrayList<ApplyPeopleListData> applyPeopleListDatas) {
            this.applyPeopleListDatas = applyPeopleListDatas;
        }

        public void setAdapter(ArrayList<ApplyPeopleListData> applyPeopleListDatas) {
            this.applyPeopleListDatas = applyPeopleListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(list_item_like_people, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            myViewHolders.add(viewHolder);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final ApplyPeopleListData applyPeopleListData = applyPeopleListDatas.get(position);

            Glide.with(getApplicationContext())
                    .load(applyPeopleListData.userImage)
                    .into(holder.ivLikeUser);
            holder.tvLikeUserName.setText(applyPeopleListData.userNickname);
            holder.tvFlag.setText("0");
            holder.tvUserID.setText(Integer.toString(applyPeopleListData.id));


            //각 아이템 클릭하는 상황
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //상단에 "선택" 이라고 보이는 상황
                    if (selectStatus == false)
                    {
                        //상세 프로필로 이동
                        Intent intent = new Intent(CheckApplyUserActivity.this, OtherUserPage.class);
                        intent.putExtra("userID", applyPeopleListData.id);
                        startActivity(intent);
                    }
                    //상단에 "취소"라고 보이는 상황
                    else
                    {
                        //v 표시 없는 이미지를 클릭하면
                        if (holder.tvFlag.getText().toString().equals("0"))
                        {
                            //  현재 5/5 상황 이라면
                            if (checkNumber == Integer.parseInt(tvNumberText.getText().toString()))
                            {
                                Toast.makeText(CheckApplyUserActivity.this, "모두 선택하였습니다", Toast.LENGTH_SHORT).show();
                                ivCheckApply.setImageResource(R.drawable.select_possilbe);

                                //선택완료 버튼 누르면
                                ivCheckApply.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //각 뷰홀더에 접근. 선택된 유저의 id값 도출
                                        for (int i = 0; i < myViewHolders.size(); i++)
                                        {
                                            //아이템에 v 표시가 있으면
                                            if (myViewHolders.get(i).tvFlag.getText().toString().equals("1"))
                                            {
                                                applicantsID.add(Integer.parseInt(myViewHolders.get(i).tvUserID.getText().toString()));
                                            }
                                        }
                                        completeChoice();
                                    }
                                });
                            }
                            else
                            //현재 0/2 상황에서, 1/2 상황에서
                            {
                                holder.ivChecked.setVisibility(View.VISIBLE);
                                holder.tvFlag.setText(Integer.toString(1));
                                checkNumber++;
                                tvNumber.setText(Integer.toString(checkNumber));

                                // 현재 2/2 상황으로 됐으면
                                if(checkNumber == Integer.parseInt(tvNumberText.getText().toString()))
                                {
                                    Toast.makeText(CheckApplyUserActivity.this, "모두 선택하였습니다", Toast.LENGTH_SHORT).show();
                                    ivCheckApply.setImageResource(R.drawable.select_possilbe);

                                    //선택완료 버튼 누르면
                                    ivCheckApply.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //각 뷰홀더에 접근. 선택된 유저의 id값 도출
                                            for (int i = 0; i < myViewHolders.size(); i++)
                                            {
                                                //아이템에 v 표시가 있으면
                                                if (myViewHolders.get(i).tvFlag.getText().toString().equals("1"))
                                                {
                                                    applicantsID.add(Integer.parseInt(myViewHolders.get(i).tvUserID.getText().toString()));
                                                }
                                            }
                                            completeChoice();
                                        }
                                    });
                                }
                                // 현재 1/2 상황으로 됐으면
                                else
                                {
                                    Toast.makeText(CheckApplyUserActivity.this, "check 되었습니다.", Toast.LENGTH_SHORT).show();
                                    ivCheckApply.setImageResource(R.drawable.select_impossible);
                                }


                            }
                        }
                        //v 표시 있는 이미지를 클릭하면
                        else
                        {
                            //현재 2/2 라면
                            if(checkNumber == Integer.parseInt(tvNumberText.getText().toString()))
                            {
                                holder.ivLikeUser.setAlpha(1f);
                                holder.ivChecked.setVisibility(View.GONE);
                                holder.tvFlag.setText(Integer.toString(0));
                                if (checkNumber >= 1)
                                    checkNumber--;
                                tvNumber.setText(Integer.toString(checkNumber));
                                Toast.makeText(CheckApplyUserActivity.this, "해제 되었습니다.", Toast.LENGTH_SHORT).show();
                                ivCheckApply.setImageResource(R.drawable.select_impossible);
                            }
                            //현재 0/2  0/1  이라면
                            else
                            {
                                holder.ivLikeUser.setAlpha(1f);
                                holder.ivChecked.setVisibility(View.GONE);
                                holder.tvFlag.setText(Integer.toString(0));
                                if (checkNumber >= 1)
                                    checkNumber--;
                                tvNumber.setText(Integer.toString(checkNumber));
                                Toast.makeText(CheckApplyUserActivity.this, "해제 되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return applyPeoples.size();
        }

    }

    private void completeChoice() {
        Call<RecruitingResult> requestCompleteChoice = service.completeChoice(Config.userID, projectId, applicantsID );
        requestCompleteChoice.enqueue(new Callback<RecruitingResult>() {
            @Override
            public void onResponse(Call<RecruitingResult> call, Response<RecruitingResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {
                        ShowChoiceCompleteDialog();
                    }
                }
            }

            @Override
            public void onFailure(Call<RecruitingResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }

    private void ShowChoiceCompleteDialog() {
        LayoutInflater dialog = LayoutInflater.from(this);
        final View dialogLayout;
        final Dialog myDialog;

        dialogLayout = dialog.inflate(R.layout.dialog_complete_choice, null);
        myDialog = new Dialog(this);
        myDialog.setContentView(dialogLayout);
        myDialog.show();
        ImageView btnApplyCancel = (ImageView) dialogLayout.findViewById(R.id.btnApplyCancel);
        btnApplyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss(); //추가
            }
        });
    }

    /**********************************ViewHolder********************************/
    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivLikeUser;
        ImageView ivChecked;
        TextView tvLikeUserName;
        TextView tvFlag;
        TextView tvUserID;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivLikeUser = (ImageView) itemView.findViewById(R.id.ivLikeUser);
            ivChecked = (ImageView) itemView.findViewById(R.id.ivChecked);
            tvLikeUserName = (TextView) itemView.findViewById(R.id.tvLikeUserName);
            tvFlag = (TextView) itemView.findViewById(R.id.tvFlag);
            tvUserID = (TextView) itemView.findViewById(R.id.tvUserID);
        }
    }


}




