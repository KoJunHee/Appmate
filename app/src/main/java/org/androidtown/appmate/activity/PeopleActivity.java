package org.androidtown.appmate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
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
import org.androidtown.appmate.component.AppmateApplication;
import org.androidtown.appmate.component.Config;
import org.androidtown.appmate.model.Hash;
import org.androidtown.appmate.model.PeopleListData;
import org.androidtown.appmate.model.PeopleResult;
import org.androidtown.appmate.network.ApplicationController;
import org.androidtown.appmate.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleActivity extends BottomNavigationParentActivity {


    /*******************************************전역*******************************************/
    private RecyclerView recyclerView;
    private ArrayList<PeopleListData> persons;
    private RecyclerAdapter recyclerAdapter;
    private LinearLayoutManager layoutManager;
    private NetworkService service;
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private int userID;


    /*****************************************bottom navigation************************************/
    @Override
    public int getCurrentActivityLayoutName() {
        return R.layout.activity_people;
    }

    @Override
    public int getCurrentSelectedBottomMenuItemID() {
        return R.id.secondTab;
    }



    /*******************************************onCreate*******************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //toolBar 설정
        Toolbar toolBar = (Toolbar) findViewById(R.id.tbPeople);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //지도 이미지 클릭 -> 지도 화면으로 이동
        ImageView ivMap = (ImageView) findViewById(R.id.ivMap);
        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PeopleActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        //레이아웃 매니저 설정
        recyclerView = (RecyclerView) findViewById(R.id.rvPeople);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //사람 리스트
        persons = new ArrayList<>();

        //어뎁터 생성, 리사이클러뷰에 붙임
        recyclerAdapter = new RecyclerAdapter(persons);
        recyclerView.setAdapter(recyclerAdapter);

        //피플 리스트 가져오기
        getPeopleList();
    }

    /*****************************************네트워킹********************************************/
    public void getPeopleList() {
        Call<PeopleResult> requestPeopleLists = service.getPeopleList(Config.userID);
        requestPeopleLists.enqueue(new Callback<PeopleResult>() {
            @Override
            public void onResponse(Call<PeopleResult> call, Response<PeopleResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {
                        persons = response.body().data.users;
                        recyclerAdapter.setAdapter(persons);
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<PeopleResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });

    }

    /***********************************Adapter**********************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<PeopleListData> peopleListDatas;

        public RecyclerAdapter(ArrayList<PeopleListData> peopleListDatas) {
            this.peopleListDatas = peopleListDatas;
        }

        public void setAdapter(ArrayList<PeopleListData> peopleListDatas) {
            this.peopleListDatas = peopleListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_people, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            PeopleListData peopleListData = peopleListDatas.get(position);

            //userImage
            Glide.with(getApplicationContext())
                    .load(peopleListData.userImage)
                    .override(60, 60)
                    .into(holder.ivPersonCharacter);

            //태그 이미지
            Hash hash = new Hash();
            hash.hashMap();
            String key;

            key = peopleListData.Skills.get(0).skillName;

            holder.ivPeopleChoiceImage01.setImageResource(hash.map.get(key));

            key = peopleListData.Skills.get(1).skillName;
            holder.ivPeopleChoiceImage02.setImageResource(hash.map.get(key));
            try {
                key = peopleListData.Skills.get(2).skillName;
                holder.ivPeopleChoiceImage03.setImageResource(hash.map.get(key));
            } catch (ArrayIndexOutOfBoundsException aobe) {
                holder.ivPeopleChoiceImage03.setImageResource(R.drawable.operation);
            }
            //닉네임
            holder.tvPersonNickName.setText(peopleListData.userNickname);

            //거리
            Double tempDistance = (peopleListData.distance * 0.001);
            tempDistance = Double.parseDouble(String.format("%.1f", tempDistance));
            holder.tvDistance.setText(Double.toString(tempDistance));

            //직업
            holder.tvPersonJobPart.setText(peopleListData.userFirstJob);

            //userId
            holder.tvUserID.setText(Integer.toString(peopleListData.id));


            //상세 프로필로 이동
            //클릭시 상세화면으로 이동, 클릭한 프로젝트 아이디 전달
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PeopleActivity.this, OtherUserPage.class);
                    intent.putExtra("userID", Integer.parseInt(holder.tvUserID.getText().toString()));
                    Log.d("userID", "people목록에서 보내는 id 값 : " + Integer.toString(userID));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return persons != null ? persons.size() : 0;
        }
    }

    /**********************************ViewHolder********************************/

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPersonCharacter;
        TextView tvPersonNickName;
        TextView tvPersonJobPart;
        ImageView ivPeopleChoiceImage01, ivPeopleChoiceImage02, ivPeopleChoiceImage03;
        ImageView ivPeopleMyPage01, ivPeopleMyPage02, ivMyPageMore;
        View line;
        ImageView ivIndicator;
        TextView tvDistance;
        TextView tvUserID;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivPersonCharacter = (ImageView) itemView.findViewById(R.id.ivPersonCharacter);
            tvPersonNickName = (TextView) itemView.findViewById(R.id.tvPersonNickName);
            tvPersonJobPart = (TextView) itemView.findViewById(R.id.tvPersonJobPart);
            ivPeopleChoiceImage01 = (ImageView) itemView.findViewById(R.id.ivPersonChoiceImage01);
            ivPeopleChoiceImage02 = (ImageView) itemView.findViewById(R.id.ivPersonChoiceImage02);
            ivPeopleChoiceImage03 = (ImageView) itemView.findViewById(R.id.ivPersonChoiceImage03);
            ivPeopleMyPage01 = (ImageView) itemView.findViewById(R.id.ivPersonMyPage01);
            ivPeopleMyPage02 = (ImageView) itemView.findViewById(R.id.ivPersonMyPage02);
            ivMyPageMore = (ImageView) itemView.findViewById(R.id.ivMyPageMore);
            line = itemView.findViewById(R.id.linePeople);
            ivIndicator = (ImageView) itemView.findViewById(R.id.ivIndicatorPeople);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            tvUserID = (TextView) itemView.findViewById(R.id.tvUserID);
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
