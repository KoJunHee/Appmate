package org.androidtown.appmate.network;

import org.androidtown.appmate.model.ApplyPeopleResult;
import org.androidtown.appmate.model.CommentResult;
import org.androidtown.appmate.model.LikePeopleResult;
import org.androidtown.appmate.model.MapResult;
import org.androidtown.appmate.model.MyPageProjectResult;
import org.androidtown.appmate.model.MypageResult;
import org.androidtown.appmate.model.PeopleResult;
import org.androidtown.appmate.model.ProjectApplyResult;
import org.androidtown.appmate.model.ProjectDetailResult;
import org.androidtown.appmate.model.ProjectFieldSkill;
import org.androidtown.appmate.model.ProjectLikeResult;
import org.androidtown.appmate.model.ProjectRegisterResult;
import org.androidtown.appmate.model.ProjectResult;
import org.androidtown.appmate.model.RecruitingResult;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * Created by pc on 2017-05-14.
 */

public interface NetworkService {

    //코멘트 리스트 가져오기
    @GET("/projects/{projectId}/comment")
    Call<CommentResult> getComments(@Header("userid") int userid,
                                    @Path("projectId") int id);

    //코멘트 쓰기
    @FormUrlEncoded
    @POST("/projects/{projectId}/comment")
    Call<CommentResult> sendComment(@Header("userid") int userid,
                                    @Path("projectId") int projectId,
                                    @Field("commentContents") String commentContents);

    //찜한 사람 리스트 가져오기
    @GET("/projects/{projectId}/likes")
    Call<LikePeopleResult> getLikePeopleList(@Header("userid") int userid,
                                            @Path("projectId") int id);

    //프로젝트 홈 리스트 가져오기
    @GET("/projects")
    Call<ProjectResult> getProjectList(@Header("userid") int userid);


    //상세 프로젝트 정보 가져오기
    @GET("/projects/{projectid}")
    Call<ProjectDetailResult> getProjectDetail(@Header("userid") int userid,
                                                @Path("projectid") int projectid);

    //분야 및 스킬 정보 가져오기
    @GET("/projects/{projectId}/fields")
    Call<ProjectFieldSkill> getProjectSkillField(@Header("userid") int userid,
                                                 @Path("projectId") int id);

    //피플 목록 리스트 가져오기
    @GET("/users/userList")
    Call<PeopleResult> getPeopleList (@Header("userid") int userid);

    //마이페이지 정보 가져오기
    @GET("/users/profile")
    Call<MypageResult> getMyPageInfo (@Header("userid") int userid);

    //다른 사람 프로필 가져오기
    @GET("/users/profile/{userid}")
    Call<MypageResult> getOtherUserPageInfo (@Header("userid") int userid,
                                             @Path("userid") int id);

    //마이페이지 프로젝트 탭
    @GET("/users/project")
    Call<MyPageProjectResult> getMyPageProjectLists (@Header("userid") int userid);


    //지도 정보 가져오기
    @FormUrlEncoded
    @POST("/users/userMapList")
    Call<MapResult> getMapInfo (@Header("userid") int userid,
                                @Field("longitude") double  longitude,
                                @Field("latitude") double latitude);


    //프로젝트 지원하기
    @FormUrlEncoded
    @POST("projects/{projectid}/apply")
    Call<ProjectApplyResult> applyResult(@Header("userid") int userid,
                                             @Path("projectid") int projectid,
                                             @Field("check") Boolean check);

    //프로젝트 등록하기
    @FormUrlEncoded
    @POST("/projects")
    Call<ProjectRegisterResult> registerProject(@Header("userid") int userid,
                                                @Field("projectName") String projectName,
                                                @Field("projectBackgroundId") int projectBackgroundId,
                                                @Field("projectMemberNumber") int projectMemberNumber,
                                                @Field("projectDescription") String projectDescription,
                                                @Field("projectState") String projectState,
                                                @Field("isPass") String isPass,
                                                @Field("projectClosingDate") String projectClosingDate,
                                                @Field("wantedSkillNames") ArrayList<String> wantedSkillNames,
                                                @Field("projectFieldNames") ArrayList<String> projectFieldNames);


    //프로젝트 지원자 목록 보기
    @GET("/projects/{projectId}/recruit")
    Call<ApplyPeopleResult> getApplyPeopleList(@Header("userid") int userid,
                                               @Path("projectId") int id);


    //모집 완료
    @FormUrlEncoded
    @POST("projects/{projectid}/recruit")
    Call<RecruitingResult> completeChoice(@Header("userid") int userid,
                                          @Path("projectid") int projectid,
                                          @Field("recruitUserIds") ArrayList<Integer> recruitUserIds);






}