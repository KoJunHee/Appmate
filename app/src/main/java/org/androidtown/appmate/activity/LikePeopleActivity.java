package org.androidtown.appmate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import org.androidtown.appmate.model.LikePeopleListData;
import org.androidtown.appmate.model.LikePeopleResult;
import org.androidtown.appmate.network.ApplicationController;
import org.androidtown.appmate.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.androidtown.appmate.R.layout.list_item_like_people;

public class LikePeopleActivity extends AppCompatActivity {

    /*******************************************전역*******************************************/
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private GridLayoutManager layoutManager;
    private NetworkService service;
    private ArrayList<LikePeopleListData> likePeoples;
    private int projectID;

    /*******************************************onCreate*******************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_people);

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //해당 프로젝트 id값 가져오기
        Intent intent = getIntent();
        projectID = intent.getExtras().getInt("projectID");

        //뒤로 가기
        LinearLayout llLikeBack = (LinearLayout) findViewById(R.id.llLikeBack);
        llLikeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //레이아웃 매니저 설정
        recyclerView = (RecyclerView) findViewById(R.id.rvLikePeople);
        layoutManager = new GridLayoutManager(LikePeopleActivity.this, 3);
        recyclerView.setLayoutManager(layoutManager);

        //ArrayList 초기화
        likePeoples = new ArrayList<>();

        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(likePeoples);
        recyclerView.setAdapter(recyclerAdapter);

        //찜한 사람 목록 가져오기
        getLikePeopleDatas();
    }


    /*************************************찜한 사람 목록 가져오기**********************/
    public void getLikePeopleDatas() {
        Call<LikePeopleResult> requestLikePeopleList = service.getLikePeopleList(Config.userID, projectID);
        requestLikePeopleList.enqueue(new Callback<LikePeopleResult>() {
            @Override
            public void onResponse(Call<LikePeopleResult> call, Response<LikePeopleResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {
                        likePeoples = response.body().data.users.get(0).Likes;
                        recyclerAdapter.setAdapter(likePeoples);
                    }
                }
            }
            @Override
            public void onFailure(Call<LikePeopleResult> call, Throwable t) {
            }
        });

    }


    /***********************************Adapter**********************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<LikePeopleListData> likePeopleListDatas;

        public RecyclerAdapter(ArrayList<LikePeopleListData> likePeopleListDatas) {
            this.likePeopleListDatas = likePeopleListDatas;
        }

        public void setAdapter(ArrayList<LikePeopleListData> likePeopleListDatas) {
            this.likePeopleListDatas = likePeopleListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(list_item_like_people, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final LikePeopleListData likePeopleListData = likePeopleListDatas.get(position);

            //userImage
            Glide.with(getApplicationContext())
                    .load(likePeopleListData.userImage)
                    .into(holder.ivLikeUser);
            //nickName
            holder.tvLikeUserName.setText(likePeopleListData.userNickname);

            //클릭시 상세화면으로 이동, 클릭한 프로젝트 아이디 전달
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LikePeopleActivity.this, OtherUserPage.class);
                    intent.putExtra("userID", likePeopleListData.id);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return likePeoples.size();
        }
    }

    /**********************************ViewHolder********************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivLikeUser;
        TextView tvLikeUserName;



        public MyViewHolder(View itemView) {
            super(itemView);
            ivLikeUser = (ImageView) itemView.findViewById(R.id.ivLikeUser);
            tvLikeUserName = (TextView) itemView.findViewById(R.id.tvLikeUserName);


        }
    }

}
