package org.androidtown.appmate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidtown.appmate.R;
import org.androidtown.appmate.component.Config;
import org.androidtown.appmate.model.MapListData;
import org.androidtown.appmate.model.MapResult;
import org.androidtown.appmate.network.ApplicationController;
import org.androidtown.appmate.network.NetworkService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    /*******************************************전역*******************************************/
    private PagerContainer mContainer;
    private ViewPager pager;
    private NetworkService service;
    private ArrayList<MapListData> mapListDatas;
    GoogleMap map;


    /*******************************************onCreate*******************************************/
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();


        //내 위치 재검색 클릭하면
        ImageView ivMyLocation = (ImageView) findViewById(R.id.ivMyLocation);
        ivMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapActivity.this, "구현예정", Toast.LENGTH_SHORT).show();

            }
        });

        //재검색 클릭하면
        ImageView ivResearch = (ImageView) findViewById(R.id.ivResearch);
        ivResearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapActivity.this, "구현예정", Toast.LENGTH_SHORT).show();
            }
        });

        //x 클릭하면
        ImageView ivCancelMap = (ImageView) findViewById(R.id.ivCancelMap);
        ivCancelMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //페이저 설정
        mContainer = (PagerContainer) findViewById(R.id.pcMapPeople);
        pager = mContainer.getViewPager();
        pager.setCurrentItem(1);

        //리스트 생성
        mapListDatas = new ArrayList<>();

        //서버에서 정보 가져오기
        getMapInfo();

        //지도
        //구글 맵 지도를 꺼낸다.
        if(savedInstanceState == null){
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
            mapFragment.getMapAsync(this);
        }
    }

    private void getMapInfo() {
        Call<MapResult> requestMapInfoes = service.getMapInfo(Config.userID, 126.6860316, 37.4351259);
        requestMapInfoes.enqueue(new Callback<MapResult>() {
            @Override
            public void onResponse(Call<MapResult> call, Response<MapResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {

                        mapListDatas = response.body().data.users;

                        //페이지 어뎁터 설정
                        PagerAdapter adapter = new MyPagerAdapter(mapListDatas);
                        pager.setAdapter(adapter);
                        pager.setOffscreenPageLimit(adapter.getCount());
                        pager.setPageMargin(15);
                        pager.setClipChildren(false);
                        pager.setCurrentItem(1);

                        //126.6860316, 37.4351259
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(37.4351259,126.6860316), 11);
                        map.animateCamera(update);
                    }
                }
            }
            @Override
            public void onFailure(Call<MapResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }
    private void clearMarker(){
    }

    Map<Marker, Integer> poiResolver = new HashMap<>();

    private void addMarker(LatLng latlag, int  viewPagerIndex) {

        MarkerOptions options = new MarkerOptions();
        options.position(latlag);
        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
        options.icon(icon);

        options.anchor(0.5f, 1);
        options.draggable(true);

        Marker marker = map.addMarker(options);
        poiResolver.put(marker, viewPagerIndex);

    }

    private class MyPagerAdapter extends PagerAdapter {

        ArrayList<MapListData> pagerListDatas;

        public MyPagerAdapter(ArrayList<MapListData> mapListDatas) {
            pagerListDatas = mapListDatas;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.list_item_people_map, container, false);

            MapListData mapListData = pagerListDatas.get(position);
            Log.d("position", Integer.toString(position));
            Log.e("current=" + position , String.valueOf(mapListData.latitude) + "," +String.valueOf(mapListData.longitude));
            addMarker(new LatLng(mapListData.longitude,mapListData.latitude), position);

            //거리
            TextView tvMapPeopleDistance = (TextView) view.findViewById(R.id.tvMapPeopleDistance);
            Double tempDistance = (mapListData.distance * 0.001);
            tempDistance = Double.parseDouble(String.format("%.1f", tempDistance));
            tvMapPeopleDistance.setText(Double.toString(tempDistance));

            //사람 이미지
            ImageView ivMapPeopleImg = (ImageView) view.findViewById(R.id.ivMapPeopleImg);
            Glide.with(getApplicationContext())
                    .load(mapListData.userImage)
                    .into(ivMapPeopleImg);

            //닉네임
            TextView tvMapPeopleName = (TextView) view.findViewById(R.id.tvMapPeopleName);
            tvMapPeopleName.setText(mapListData.userNickname);

            //직군
            TextView tvMapPeopleJob = (TextView) view.findViewById(R.id.tvMapPeopleJob);
            tvMapPeopleJob.setText(mapListData.userFirstJob);

            //userID
            final int userID = mapListData.id;

            //카드 클릭시
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MapActivity.this, OtherUserPage.class);
                    intent.putExtra("userID", userID);
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
            return mapListDatas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int  currentSelectedPosition = poiResolver.get(marker);
        pager.animate();
        pager.setCurrentItem(currentSelectedPosition);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //나의 현재 위치로 이동하는 버튼을 지도에 추가
        //map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnCameraChangeListener(this);
        map.setOnMarkerClickListener(this);

         //126.6860316, 37.4351259
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(37.4351259,126.6860316), 11);
        map.animateCamera(update);
    }
}