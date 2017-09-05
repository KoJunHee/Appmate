package org.androidtown.appmate.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidtown.appmate.R;
import org.androidtown.appmate.component.Config;
import org.androidtown.appmate.model.Hash;
import org.androidtown.appmate.model.MyPageData;
import org.androidtown.appmate.model.MypageResult;

import org.androidtown.appmate.network.ApplicationController;
import org.androidtown.appmate.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.androidtown.appmate.R.layout.list_item_carrer_profile;
import static org.androidtown.appmate.R.layout.list_item_portfolio;
import static org.androidtown.appmate.R.layout.list_item_tag;

/**
 * Created by KoJunHee on 2017-06-01.
 */

public class FirstMyPageFragment extends Fragment {

    /*******************************************전역*******************************************/
    private View view;
    private NetworkService service;
    private TextView tvFavoritePlace;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView02;
    private RecyclerView recyclerView03;
    private RecyclerView recyclerView04;
    private RecyclerView recyclerView05;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerAdapter02 recyclerAdapter02;
    private RecyclerAdapter03 recyclerAdapter03;
    private RecyclerAdapter04 recyclerAdapter04;
    private RecyclerAdapter05 recyclerAdapter05;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<String> carrerList;
    private String portfolio = "";
    private ArrayList<MyPageData.MyPageHavingSkill> skills;
    private ArrayList<MyPageData.MyPageWantedSkill> wantedSkills;
    private ArrayList<MyPageData.MyPageProjectField> projectFields;
    private int userID;


    /***********************************************생성자*******************************************/
    public FirstMyPageFragment() {
    }


    /***********************************************newInstance***********************************/
    public static Fragment newInstance() {
        Fragment firstMyPageFragment = new FirstMyPageFragment();
        Bundle args = new Bundle();
        firstMyPageFragment.setArguments(args);
        return firstMyPageFragment;
    }


    /***************************************onCreateView****************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first_my_page, container, false);

        //userID 받기
        Bundle extra = getArguments();
        userID = extra.getInt("userID");

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //findView
        findView();

        //경력 사항 리사이클러 설정
        carrerList = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.rvCarrer);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter = new RecyclerAdapter(carrerList);
        recyclerView.setAdapter(recyclerAdapter);

        //포트폴리오 리사이클러 설정
        recyclerView02 = (RecyclerView) view.findViewById(R.id.rvPortfolio);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView02.setLayoutManager(linearLayoutManager);
        recyclerAdapter02 = new RecyclerAdapter02(portfolio);
        recyclerView02.setAdapter(recyclerAdapter02);

        //내가 가진 스킬 리사이클러 설정
        skills = new ArrayList<>();
        recyclerView03 = (RecyclerView) view.findViewById(R.id.rvHavingSkill);
        linearLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView03.setLayoutManager(linearLayoutManager);
        recyclerAdapter03 = new RecyclerAdapter03(skills);
        recyclerView03.setAdapter(recyclerAdapter03);

        //내가 원하는 스킬 리사이클러 설정
        wantedSkills = new ArrayList<>();
        recyclerView04 = (RecyclerView) view.findViewById(R.id.rvHoping);
        linearLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView04.setLayoutManager(linearLayoutManager);
        recyclerAdapter04 = new RecyclerAdapter04(wantedSkills);
        recyclerView04.setAdapter(recyclerAdapter04);

        //관심분야  리사이클러 설정
        projectFields = new ArrayList<>();
        recyclerView05 = (RecyclerView) view.findViewById(R.id.rvFavoriteField);
        linearLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView05.setLayoutManager(linearLayoutManager);
        recyclerAdapter05 = new RecyclerAdapter05(projectFields);
        recyclerView05.setAdapter(recyclerAdapter05);

        getProfileInfo();


        return view;
    }


    /***************************************findView*********************************************/
    private void findView() {
        tvFavoritePlace = (TextView) view.findViewById(R.id.tvFavoritePlace);
    }

    /**********************************프로필 정보 가져오기****************************************/

    private void getProfileInfo() {
        Call<MypageResult> requestMyPageInfo;
        if(userID!=Config.userID){
            requestMyPageInfo = service.getMyPageInfo(userID);
        }else{
            requestMyPageInfo = service.getMyPageInfo(Config.userID);
        }

        requestMyPageInfo.enqueue(new Callback<MypageResult>() {
            @Override
            public void onResponse(Call<MypageResult> call, Response<MypageResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {
                        Log.d("ww", "ww");

                        tvFavoritePlace.setText(response.body().data.user.UserPlace.address);

                        //경력사항
                        if (response.body().data.user.userFirstJob instanceof String) {
                            carrerList.add(response.body().data.user.userFirstJob);
                        } else {
                            carrerList.add("");
                        }
                        if (response.body().data.user.userSecondJob instanceof String) {
                            carrerList.add(response.body().data.user.userSecondJob);
                        } else {
                            carrerList.add("");
                        }
                        if (response.body().data.user.userThirdJob instanceof String) {
                            carrerList.add(response.body().data.user.userThirdJob);
                        } else {
                            carrerList.add("");
                        }
                        recyclerAdapter.setAdapter(carrerList);


                        //포트폴리오
                        if (response.body().data.user.portfolio instanceof String) {
                            portfolio = response.body().data.user.portfolio;
                            recyclerAdapter02.setAdapter(portfolio);
                        }

                        //가진 스킬
                        skills = response.body().data.user.Skills;
                        recyclerAdapter03.setAdapter(skills);

                        //원하는 스킬
                        wantedSkills = response.body().data.user.WantedSkills;
                        recyclerAdapter04.setAdapter(wantedSkills);

                        //관심 분야
                        projectFields = response.body().data.user.ProjectFields;
                        recyclerAdapter05.setAdapter(projectFields);
                    }
                }
            }

            @Override
            public void onFailure(Call<MypageResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }

    /***********************************경력사항**********************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<String> carrers;

        public RecyclerAdapter(ArrayList<String> carrers) {
            this.carrers = carrers;
        }

        public void setAdapter(ArrayList<String> carrers) {
            this.carrers = carrers;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(list_item_carrer_profile, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String carrer = carrers.get(position);
            if (!carrer.equals("")) {
                holder.tvCarrer.setText(carrer);
            }
        }

        @Override
        public int getItemCount() {
            return carrerList.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCarrer;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCarrer = (TextView) itemView.findViewById(R.id.tvCarrer);
        }
    }


    /***********************************포트폴리오**********************************/
    class RecyclerAdapter02 extends RecyclerView.Adapter<MyViewHolder02> {

        String portfolioo;

        public RecyclerAdapter02(String portfolioo) {
            Log.d("r", "1111111111111111111");
            this.portfolioo = portfolioo;
        }

        public void setAdapter(String portfolioo) {
            Log.d("r", "2222222");
            this.portfolioo = portfolioo;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder02 onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("r", "33");

            View view = LayoutInflater.from(parent.getContext()).inflate(list_item_portfolio, parent, false);
            MyViewHolder02 viewHolder = new MyViewHolder02(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder02 holder, int position) {
            Log.d("r", "44");

            holder.tvPortfolio.setText(portfolioo);
        }

        @Override
        public int getItemCount() {
            if (portfolio != "")
                return 1;
            else
                return 0;
        }
    }

    class MyViewHolder02 extends RecyclerView.ViewHolder {
        TextView tvPortfolio;

        public MyViewHolder02(View itemView) {
            super(itemView);
            tvPortfolio = (TextView) itemView.findViewById(R.id.tvPortfolio);
        }
    }


    /***********************************가진스킬**********************************/
    class RecyclerAdapter03 extends RecyclerView.Adapter<MyViewHolderTag> {

        ArrayList<MyPageData.MyPageHavingSkill> skillss;

        public RecyclerAdapter03(ArrayList<MyPageData.MyPageHavingSkill> skillss) {
            this.skillss = skillss;
        }

        public void setAdapter(ArrayList<MyPageData.MyPageHavingSkill> skillss) {
            this.skillss = skillss;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolderTag onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(list_item_tag, parent, false);
            MyViewHolderTag viewHolder = new MyViewHolderTag(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolderTag holder, int position) {
            //hash
            String key = skillss.get(position).skillName;
            Log.d("key", key);
            Hash hash = new Hash();
            hash.hashMap();

            holder.ivTag.setImageResource(hash.map.get(key));
        }

        @Override
        public int getItemCount() {
            return skills.size();
        }
    }





    /***********************************원하는 스킬**********************************/
    class RecyclerAdapter04 extends RecyclerView.Adapter<MyViewHolderTag> {

        ArrayList<MyPageData.MyPageWantedSkill> skillss;

        public RecyclerAdapter04(ArrayList<MyPageData.MyPageWantedSkill> skillss) {
            this.skillss = skillss;
        }

        public void setAdapter(ArrayList<MyPageData.MyPageWantedSkill> skillss) {
            this.skillss = skillss;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolderTag onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(list_item_tag, parent, false);
            MyViewHolderTag viewHolder = new MyViewHolderTag(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolderTag holder, int position) {
            //hash
            String key = skillss.get(position).skillName;
            Log.d("key", key);
            Hash hash = new Hash();
            hash.hashMap();

            holder.ivTag.setImageResource(hash.map.get(key));
        }

        @Override
        public int getItemCount() {
            return wantedSkills.size();
        }
    }




    /***********************************관심분야**********************************/
    class RecyclerAdapter05 extends RecyclerView.Adapter<MyViewHolderTag> {

        ArrayList<MyPageData.MyPageProjectField> skillss;

        public RecyclerAdapter05(ArrayList<MyPageData.MyPageProjectField> skillss) {
            this.skillss = skillss;
        }

        public void setAdapter(ArrayList<MyPageData.MyPageProjectField> skillss) {
            this.skillss = skillss;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolderTag onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(list_item_tag, parent, false);
            MyViewHolderTag viewHolder = new MyViewHolderTag(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolderTag holder, int position) {
            //hash
            String key = skillss.get(position).projectFieldName;
            Log.d("key", key);
            Hash hash = new Hash();
            hash.hashMap();

            holder.ivTag.setImageResource(hash.map.get(key));
        }

        @Override
        public int getItemCount() {
            return projectFields.size();
        }
    }



    /*************************************태그이미지 홀더******************************************/
    class MyViewHolderTag extends RecyclerView.ViewHolder {
        ImageView ivTag;
        public MyViewHolderTag(View itemView) {
            super(itemView);
            ivTag = (ImageView) itemView.findViewById(R.id.ivTag);
        }
    }



}
