package org.androidtown.appmate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.androidtown.appmate.R;
import org.androidtown.appmate.activity.PagerContainer;
import org.androidtown.appmate.activity.ProjectDetailActivity;
import org.androidtown.appmate.component.Config;
import org.androidtown.appmate.model.MyPageProjectResult;
import org.androidtown.appmate.network.ApplicationController;
import org.androidtown.appmate.network.NetworkService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by KoJunHee on 2017-06-01.
 */

public class SecondMyPageFragment extends Fragment {

    /*******************************************전역*******************************************/
    private PagerContainer pcGoingProjectMyPage;
    private PagerContainer pcSelectProjectMyPage;
    private PagerContainer pcApplyProjectMyPage;
    private PagerContainer pcRecruitingProjectMyPage;
    private ArrayList<MyPageProjectResult.ProjectRunning> projectRunnings;
    private ArrayList<MyPageProjectResult.ProjectApply> projectApply;
    private ArrayList<MyPageProjectResult.ProjectRecruit> projectRecruit;
    private ArrayList<MyPageProjectResult.ProjectLike> projectLikes;
    private ImageView ivProjectImage;
    private TextView tvProjectTitle;
    private TextView tvNickName;
    private ImageView ivUerImg;
    private NetworkService service;
    private ViewPager vpGoingProject;
    private ViewPager vpApplyProject;
    private ViewPager vpRecruitingProject;
    private ViewPager vpSelectProject;
    private int userID;


    /***********************************************생성자*******************************************/
    public SecondMyPageFragment() {
    }


    /***********************************************newInstance***********************************/
    public static Fragment newInstance() {
        Fragment secondMyPageFragment = new SecondMyPageFragment();
        Bundle args = new Bundle();
        secondMyPageFragment.setArguments(args);
        return secondMyPageFragment;
    }


    /***************************************onCreateView****************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second_my_page, container, false);

        //userID 받기
        Bundle extra = getArguments();
        userID = extra.getInt("userID");

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //뷰페이저 설정
        pcGoingProjectMyPage = (PagerContainer) view.findViewById(R.id.pcGoingProjectMyPage);
        vpGoingProject = pcGoingProjectMyPage.getViewPager();

        pcApplyProjectMyPage = (PagerContainer) view.findViewById(R.id.pcApplyProjectMyPage);
        vpApplyProject = pcApplyProjectMyPage.getViewPager();

        pcRecruitingProjectMyPage = (PagerContainer) view.findViewById(R.id.pcRecruitingProjectMyPage);
        vpRecruitingProject = pcRecruitingProjectMyPage.getViewPager();

        pcSelectProjectMyPage = (PagerContainer) view.findViewById(R.id.pcSelectProjectMyPage);
        vpSelectProject = pcSelectProjectMyPage.getViewPager();


        //리스트 생성
        projectRunnings = new ArrayList<>();
        projectLikes = new ArrayList<>();
        projectApply = new ArrayList<>();
        projectRecruit = new ArrayList<>();


        //서버에서 데이터를 가져오는 메소드
        getProjectLists();


        return view;
    }

    /*******************************************프로젝트 리스트 가져오기***********************************/
    private void getProjectLists() {

        Call<MyPageProjectResult> requestProjectLists;
        if (userID != Config.userID) {
            requestProjectLists = service.getMyPageProjectLists(userID);
        } else {
            requestProjectLists = service.getMyPageProjectLists(Config.userID);
        }


        requestProjectLists.enqueue(new Callback<MyPageProjectResult>() {
            @Override
            public void onResponse(Call<MyPageProjectResult> call, Response<MyPageProjectResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {

                        projectRunnings = response.body().data.projectRunning;
                        projectLikes = response.body().data.projectLike;
                        projectApply = response.body().data.projectApply;
                        projectRecruit = response.body().data.projectRecruit;


                        //진행중인
                        PagerAdapter paGoingProject = new MyPagerAdapterGoingProjectMyPage(projectRunnings);
                        vpGoingProject.setAdapter(paGoingProject);
                        vpGoingProject.setOffscreenPageLimit(paGoingProject.getCount());
                        vpGoingProject.setPageMargin(15);
                        vpGoingProject.setClipChildren(false);
                        vpGoingProject.setCurrentItem(1);

                        //지원한
                        PagerAdapter paApplyProject = new MyPagerAdapterApplyProjectMyPage(projectApply);
                        vpApplyProject.setAdapter(paApplyProject);
                        vpApplyProject.setOffscreenPageLimit(paApplyProject.getCount());
                        vpApplyProject.setPageMargin(15);
                        vpApplyProject.setClipChildren(false);
                        vpApplyProject.setCurrentItem(1);

                        //모집중인
                        PagerAdapter paRecruitingProject = new MyPagerAdapterRecruitingProjectMyPage(projectRecruit);
                        vpRecruitingProject.setAdapter(paRecruitingProject);
                        vpRecruitingProject.setOffscreenPageLimit(paRecruitingProject.getCount());
                        vpRecruitingProject.setPageMargin(15);
                        vpRecruitingProject.setClipChildren(false);
                        vpRecruitingProject.setCurrentItem(1);


                        //찜한
                        PagerAdapter paSelectProject = new MyPagerAdapterSelectProjectMyPage(projectLikes);
                        vpSelectProject.setAdapter(paSelectProject);
                        vpSelectProject.setOffscreenPageLimit(paSelectProject.getCount());
                        vpSelectProject.setPageMargin(15);
                        vpSelectProject.setClipChildren(false);
                        vpSelectProject.setCurrentItem(1);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyPageProjectResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }


    /********************************진행중인프로젝트 어뎁터****************************************/
    private class MyPagerAdapterGoingProjectMyPage extends PagerAdapter {

        ArrayList<MyPageProjectResult.ProjectRunning> projectRunnings;

        public MyPagerAdapterGoingProjectMyPage(ArrayList<MyPageProjectResult.ProjectRunning> projectRunnings) {
            this.projectRunnings = projectRunnings;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.list_item_going_project_mypage, container, false);

            //findView
            ivProjectImage = (ImageView) view.findViewById(R.id.ivProjectImage);
            tvProjectTitle = (TextView) view.findViewById(R.id.tvProjectTitle);
            tvNickName = (TextView) view.findViewById(R.id.tvNickName);
            ivUerImg = (ImageView) view.findViewById(R.id.ivUerImg);

            //get position
            MyPageProjectResult.ProjectRunning projectRunning = projectRunnings.get(position);

            //화면 세팅
            tvProjectTitle.setText(projectRunning.projectName);
            tvNickName.setText(projectRunning.Owner.userNickname);
            Glide.with(getActivity())
                    .load(projectRunning.ProjectBackground.projectBackgroundImage)
                    .into(ivProjectImage);
            Glide.with(getActivity())
                    .load(projectRunning.Owner.userImage)
                    .into(ivUerImg);


            //프로젝트 id
            final int goingProjectID = projectRunning.id;

            //카드 클릭시
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
                    intent.putExtra("projectId", goingProjectID);
                    startActivity(intent);
                }
            });

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return projectRunnings.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }

    /********************************지원한 프로젝트 어뎁터****************************************/
    private class MyPagerAdapterApplyProjectMyPage extends PagerAdapter {

        ArrayList<MyPageProjectResult.ProjectApply> projectApplies;

        public MyPagerAdapterApplyProjectMyPage(ArrayList<MyPageProjectResult.ProjectApply> projectApplies) {
            this.projectApplies = projectApplies;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.list_item_project_my_page, container, false);

            MyPageProjectResult.ProjectApply projectApply = projectApplies.get(position);

            //findView
            ImageView ivProjectImage = (ImageView) view.findViewById(R.id.ivProjectImage);
            ImageView userImg = (ImageView) view.findViewById(R.id.userImg);
            TextView projectTitle = (TextView) view.findViewById(R.id.projectTitle);
            TextView userNickname = (TextView) view.findViewById(R.id.userNickname);
            TextView dDay = (TextView) view.findViewById(R.id.dDay);

            projectTitle.setText(projectApply.projectName);
            userNickname.setText(projectApply.Owner.userNickname);
            dDay.setText(Integer.toString(projectApply.dDay));

            Glide.with(getActivity())
                    .load(projectApply.ProjectBackground.projectBackgroundImage)
                    .into(ivProjectImage);
            Glide.with(getActivity())
                    .load(projectApply.Owner.userImage)
                    .into(userImg);

            //프로젝트 id
            final int appliedProjectID = projectApply.id;

            //카드 클릭시
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
                    intent.putExtra("projectId", appliedProjectID);
                    startActivity(intent);
                }
            });

            //컨테이너에 추가하고 return
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return projectApplies.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }


    /********************************모집중인 프로젝트 어뎁터****************************************/
    private class MyPagerAdapterRecruitingProjectMyPage extends PagerAdapter {

        ArrayList<MyPageProjectResult.ProjectRecruit> projectRecruits;

        public MyPagerAdapterRecruitingProjectMyPage(ArrayList<MyPageProjectResult.ProjectRecruit> projectRecruits) {
            this.projectRecruits = projectRecruits;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.list_item_project_my_page, container, false);
            MyPageProjectResult.ProjectRecruit projectRecruit = projectRecruits.get(position);

            //findView
            ImageView ivProjectImage = (ImageView) view.findViewById(R.id.ivProjectImage);
            ImageView userImg = (ImageView) view.findViewById(R.id.userImg);
            TextView projectTitle = (TextView) view.findViewById(R.id.projectTitle);
            TextView userNickname = (TextView) view.findViewById(R.id.userNickname);
            TextView dDay = (TextView) view.findViewById(R.id.dDay);

            projectTitle.setText(projectRecruit.projectName);
            userNickname.setText(projectRecruit.Owner.userNickname);
            dDay.setText(Integer.toString(projectRecruit.dDay));

            Glide.with(getActivity())
                    .load(projectRecruit.ProjectBackground.projectBackgroundImage)
                    .into(ivProjectImage);
            Glide.with(getActivity())
                    .load(projectRecruit.Owner.userImage)
                    .into(userImg);

            //프로젝트 id
            final int recruitingProjectId = projectRecruit.id;

            //카드 클릭시
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
                    intent.putExtra("projectId", recruitingProjectId);
                    startActivity(intent);
                }
            });

            //컨테이너에 추가하고 return
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return projectRecruits.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }


    /********************************찜한 프로젝트 어뎁터****************************************/
    private class MyPagerAdapterSelectProjectMyPage extends PagerAdapter {

        ArrayList<MyPageProjectResult.ProjectLike> projectLikes;

        public MyPagerAdapterSelectProjectMyPage(ArrayList<MyPageProjectResult.ProjectLike> projectLikes) {
            this.projectLikes = projectLikes;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.list_item_project_my_page, container, false);
            MyPageProjectResult.ProjectLike projectLike = projectLikes.get(position);

            //findView
            ImageView ivProjectImage = (ImageView) view.findViewById(R.id.ivProjectImage);
            ImageView userImg = (ImageView) view.findViewById(R.id.userImg);
            TextView projectTitle = (TextView) view.findViewById(R.id.projectTitle);
            TextView userNickname = (TextView) view.findViewById(R.id.userNickname);
            TextView dDay = (TextView) view.findViewById(R.id.dDay);

            projectTitle.setText(projectLike.projectName);
            userNickname.setText(projectLike.Owner.userNickname);
            dDay.setText(Integer.toString(projectLike.dDay));

            Glide.with(getActivity())
                    .load(projectLike.ProjectBackground.projectBackgroundImage)
                    .into(ivProjectImage);
            Glide.with(getActivity())
                    .load(projectLike.Owner.userImage)
                    .into(userImg);

            //프로젝트 id
            final int likeProjectId = projectLike.id;

            //카드 클릭시
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
                    intent.putExtra("projectId", likeProjectId);
                    startActivity(intent);
                }
            });

            //컨테이너에 추가하고 return
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return projectLikes.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }
}


