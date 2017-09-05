package org.androidtown.appmate.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.androidtown.appmate.R;
import org.androidtown.appmate.component.Config;
import org.androidtown.appmate.fragment.FirstMyPageFragment;
import org.androidtown.appmate.fragment.SecondMyPageFragment;
import org.androidtown.appmate.model.MypageResult;
import org.androidtown.appmate.network.ApplicationController;
import org.androidtown.appmate.network.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageActivity extends BottomNavigationParentActivity {


    /*******************************************전역*******************************************/
    private PagerAdapter pagerAdapter;
    private Fragment selectedFragment;
    private NetworkService service;
    private TextView tvIdentityName;
    private TextView tvUserNickname;
    private TextView tvIntroduction;
    private TextView tvFollowingNumber;
    private TextView tvFollowerNumber;
    private TextView tvHighfiveNumber;
    private TextView tvBadgeNumber;
    private ImageView ivUserImage;
    private int userID;
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;


    /*****************************************bottom navigation************************************/
    @Override
    public int getCurrentActivityLayoutName() {
        return R.layout.activity_my_page;
    }

    @Override
    public int getCurrentSelectedBottomMenuItemID() {
        return R.id.thirdTab;
    }


    /*******************************************onCreate*******************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //toolBar 설정
        Toolbar toolBar = (Toolbar) findViewById(R.id.tool_bar_mypage);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //수정하기 이미지 클릭시 - 구현 예정
        LinearLayout llMypageModify = (LinearLayout) findViewById(R.id.llMypageModify);
        llMypageModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyPageActivity.this, "구현예정", Toast.LENGTH_SHORT).show();
            }
        });


        //탭 레이아웃 설정
        if (savedInstanceState == null) {
            selectedFragment = FirstMyPageFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("userID", Config.userID);
            selectedFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.flMyPageContainer, selectedFragment).commit();
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tlMyPage);
        tabLayout.addTab(tabLayout.newTab().setText("프로필"));
        tabLayout.addTab(tabLayout.newTab().setText("프로젝트"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //탭 클릭시
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        selectedFragment = FirstMyPageFragment.newInstance();
                        break;
                    case 1:
                        selectedFragment = SecondMyPageFragment.newInstance();
                        break;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();

                Bundle bundle = new Bundle();
                bundle.putInt("userID", Config.userID);
                selectedFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flMyPageContainer, selectedFragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        pagerAdapter = new PageAdapter(getSupportFragmentManager());
        finvView();
        getProfileInfo();
    }

    /*****************************findView*******************************************/
    public void finvView() {
        tvIdentityName = (TextView) findViewById(R.id.tvIdentityName);
        tvUserNickname = (TextView) findViewById(R.id.tvUserNickname);
        tvIntroduction = (TextView) findViewById(R.id.tvIntroduction);
        tvFollowingNumber = (TextView) findViewById(R.id.tvFollowingNumber);
        tvFollowerNumber = (TextView) findViewById(R.id.tvFollowerNumber);
        tvHighfiveNumber = (TextView) findViewById(R.id.tvHighfiveNumber);
        tvBadgeNumber = (TextView) findViewById(R.id.tvBadgeNumber);
        ivUserImage = (ImageView) findViewById(R.id.ivUserImage);
    }

    /**********************************프로필 정보 가져오기****************************************/

    public void getProfileInfo() {
        Call<MypageResult> requestMyPageInfo = service.getMyPageInfo(Config.userID);
        requestMyPageInfo.enqueue(new Callback<MypageResult>() {
            @Override
            public void onResponse(Call<MypageResult> call, Response<MypageResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {

                        //유저 이미지
                        Glide.with(getApplicationContext())
                                .load(response.body().data.user.userImage)
                                .override(110, 110)
                                .thumbnail(0.1f)
                                .into(ivUserImage);

                        //아이덴티티
                        tvIdentityName.setText(response.body().data.user.Identity.identityName);

                        //닉네임
                        tvUserNickname.setText(response.body().data.user.userNickname);

                        //자기소개
                        tvIntroduction.setText(response.body().data.user.introduction);

                        //팔로잉 숫자
                        tvFollowingNumber.setText(Integer.toString(response.body().data.user.followingNumber));

                        //팔로워 숫자
                        tvFollowerNumber.setText(Integer.toString(response.body().data.user.followerNumber));

                        //하이파이브 숫자
                        tvHighfiveNumber.setText("0");

                        //뱃지 숫자
                        tvBadgeNumber.setText("0");

                    }
                }
            }

            @Override
            public void onFailure(Call<MypageResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }

    /********************************page adapter***********************************/
    class PageAdapter extends FragmentStatePagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FirstMyPageFragment();
                case 1:
                    return new SecondMyPageFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

    /*********************************back 두번 누렀을 경우********************************/
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        /**
         * Back키 두번 연속 클릭 시 앱 종료
         */
        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로 가기 키을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }


}

