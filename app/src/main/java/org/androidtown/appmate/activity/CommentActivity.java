package org.androidtown.appmate.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.androidtown.appmate.R;
import org.androidtown.appmate.component.Config;
import org.androidtown.appmate.model.CommentListData;
import org.androidtown.appmate.model.CommentResult;
import org.androidtown.appmate.network.ApplicationController;
import org.androidtown.appmate.network.NetworkService;

import java.util.ArrayList;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.androidtown.appmate.R.layout.list_item_comment;

public class CommentActivity extends AppCompatActivity {

    /*******************************************전역*******************************************/
    NetworkService service;
    private RecyclerView recyclerView;
    private RecyclerAdapter madapter;
    private LinearLayoutManager mLayoutManager;
    ArrayList<CommentListData> comments;
    private int projectId;

    /*******************************************onCreate*******************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //서비스 객체 초기화
        service = ApplicationController.getInstance().getNetworkService();

        //해당 프로젝트 id값 가져오기
        Intent intent = getIntent();
        projectId = intent.getExtras().getInt("projectID");

        //뒤로 가기
        LinearLayout llCommentBack = (LinearLayout) findViewById(R.id.llCommentBack);
        llCommentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //레이아웃 설정
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewComment);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        //댓글 리스트
        comments = new ArrayList<>();

        //리사이클러와 어뎁터 연동
        madapter = new RecyclerAdapter(comments);
        recyclerView.setAdapter(madapter);

        //댓글 목록 가져오기
        getCommentsFromServer();

        //댓글 입력 버튼 누르면
        ImageView ivEnter = (ImageView) findViewById(R.id.ivEnter);
        final EditText etCommentWriting = (EditText) findViewById(R.id.etCommentWriting);
        ivEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String writtenComment = etCommentWriting.getText().toString();

                //댓글 입력이 공백이 아니라면
                if (!writtenComment.equals("")) {
                    sendCommentToServer(writtenComment);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etCommentWriting.getWindowToken(), 0);
                    etCommentWriting.setText("");
                }

                //댓글 입력이 공백이라면
                else
                    Toast.makeText(CommentActivity.this, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**************************************getComment********************************************/
    public void getCommentsFromServer() {
        Call<CommentResult> requestPeopleList = service.getComments(Config.userID, projectId);
        requestPeopleList.enqueue(new Callback<CommentResult>() {
            @Override
            public void onResponse(Call<CommentResult> call, Response<CommentResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {
                        comments = response.body().data.comments;
                        madapter.setAdapter(comments);
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }

    /**************************************SendComment********************************************/
    public void sendCommentToServer(String writtenComment) {
        Call<CommentResult> requestPeopleList = service.sendComment(Config.userID, projectId, writtenComment);
        requestPeopleList.enqueue(new Callback<CommentResult>() {
            @Override
            public void onResponse(Call<CommentResult> call, Response<CommentResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {
                        comments = response.body().data.comments;
                        madapter.setAdapter(comments);
                    }
                }
            }

            @Override
            public void onFailure(Call<CommentResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });
    }


    /***********************************Adapter**********************************/
    class RecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        ArrayList<CommentListData> commentListDatas;

        public RecyclerAdapter(ArrayList<CommentListData> commentListDatas) {
            this.commentListDatas = commentListDatas;
        }

        public void setAdapter(ArrayList<CommentListData> commentListDatas) {
            this.commentListDatas = commentListDatas;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(list_item_comment, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            CommentListData commentListData = commentListDatas.get(position);

            holder.tvNickNameComment.setText(commentListData.User.userNickname);
            holder.tvComment.setText(commentListData.commentContents);

            if (commentListData.dateDiff != 0) {
                holder.tvTimeComment.setText(commentListData.dateDiff + "일 전");
            } else if (commentListData.dateDiff == 0) {

                //자르기
                StringTokenizer tokens = new StringTokenizer(commentListData.timeDiff);
                String first = tokens.nextToken(":");
                int firstVal = Integer.parseInt(first);
                String second = tokens.nextToken(":");
                int secondVal = Integer.parseInt(second);

                //첫번재가 0이 아니면
                if (firstVal != 0)
                    holder.tvTimeComment.setText(Integer.toString(firstVal) + "시간 전");
                else if (firstVal == 0) {
                    if(secondVal==0)
                        holder.tvTimeComment.setText("방금 전");
                    else
                        holder.tvTimeComment.setText(Integer.toString(secondVal) + "분 전");
                }
            }

            Glide.with(getApplicationContext())
                    .load(commentListData.User.userImage)
                    .into(holder.ivUserImageComment);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }
    }

    /**********************************ViewHolder********************************/
    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivUserImageComment;
        TextView tvNickNameComment;
        TextView tvComment;
        TextView tvTimeComment;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivUserImageComment = (ImageView) itemView.findViewById(R.id.ivUserImageComment);
            tvNickNameComment = (TextView) itemView.findViewById(R.id.tvNickNameComment);
            tvComment = (TextView) itemView.findViewById(R.id.tvComment);
            tvTimeComment = (TextView) itemView.findViewById(R.id.tvTimeComment);
        }
    }
}
