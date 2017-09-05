package org.androidtown.appmate.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.appmate.R;
import org.androidtown.appmate.component.Config;
import org.androidtown.appmate.model.ProjectRegisterResult;
import org.androidtown.appmate.network.ApplicationController;
import org.androidtown.appmate.network.NetworkService;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.androidtown.appmate.R.id.tvTextNumber;

public class ProjectRegisterActivity extends AppCompatActivity {


    /*******************************************전역*******************************************/
    private TextView tvTextNumber;
    private TextView tvContentsNumber;
    private EditText etTitle;
    private EditText etContents;
    private TextView tvDueDay;
    private String currentRecruitingNumberString;
    private int currentRecruitingNumberInt;
    private LinearLayout minus;
    private LinearLayout plus;
    private TextView tvRecruitingNumber;
    private RadioGroup rg;
    private NetworkService service;
    private String projectName="";
    private String projectDescription="";
    private String projectClosingDate="";
    private int projectMemberNumber;
    private String isPass;
    private ArrayList<String> wantedSkillNames;
    private ArrayList<String> projectFieldNames;

    /*******************************************onCreate*******************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_project_register);
        }catch(OutOfMemoryError err){
            Toast.makeText(this, "메모리 부족==>나중에 수정요망",Toast.LENGTH_LONG).show();
        }
            //서비스 객체 초기화
            service = ApplicationController.getInstance().getNetworkService();

            //toolBar 설정
            Toolbar toolBar = (Toolbar) findViewById(R.id.tbProjectRegister);
            setSupportActionBar(toolBar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            //findView
            etTitle = (EditText) findViewById(R.id.etTitle);
            etContents = (EditText) findViewById(R.id.etContents);
            tvTextNumber = (TextView) findViewById(R.id.tvTextNumber);
            tvContentsNumber = (TextView) findViewById(R.id.tvContentsNumber);
            tvDueDay = (TextView) findViewById(R.id.tvDueDay);
            rg = (RadioGroup) findViewById(R.id.rg);

            //리스트 생성
            wantedSkillNames = new ArrayList<>();
            projectFieldNames = new ArrayList<>();


            //x 누르면
            ImageView ivXimg = (ImageView) findViewById(R.id.ivXimg);
            ivXimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            //데이트피커
            LinearLayout llDataPicker = (LinearLayout) findViewById(R.id.llDataPicker);
            llDataPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            tvDueDay.setText(Integer.toString(year) + "년" + Integer.toString(monthOfYear + 1) + "월" + Integer.toString(dayOfMonth) + "일");
                            Toast.makeText(getApplicationContext(), year + "년" + (monthOfYear + 1) + "월" + dayOfMonth + "일", Toast.LENGTH_SHORT).show();

                            //마감기한 저장
                            projectClosingDate = year + "-" + (monthOfYear + 1) + "-" + (dayOfMonth+1);
                        }
                    };
                    DatePickerDialog dialog = new DatePickerDialog(ProjectRegisterActivity.this, listener, 2017, 0, 1);
                    dialog.show();
                }
            });

            //팀원수 증가, 감소
            minus = (LinearLayout) findViewById(R.id.minus);
            plus = (LinearLayout) findViewById(R.id.plus);
            tvRecruitingNumber = (TextView) findViewById(R.id.tvRecruitingNumber);
            currentRecruitingNumberString = tvRecruitingNumber.getText().toString();
            currentRecruitingNumberInt = Integer.parseInt(currentRecruitingNumberString);

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1이상이면 -1
                    if (currentRecruitingNumberInt >= 1) {
                        currentRecruitingNumberInt = currentRecruitingNumberInt - 1;
                        currentRecruitingNumberString = String.valueOf(currentRecruitingNumberInt);
                        tvRecruitingNumber.setText(currentRecruitingNumberString);
                    }
                }
            });

            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1이상이면 -1
                    currentRecruitingNumberInt = currentRecruitingNumberInt + 1;
                    currentRecruitingNumberString = String.valueOf(currentRecruitingNumberInt);
                    tvRecruitingNumber.setText(currentRecruitingNumberString);
                }
            });


            //등록 누르면
            LinearLayout llregister = (LinearLayout) findViewById(R.id.llregister);
            llregister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //패스권 여부 어떤걸 선택했는지 확인
                    int checkedButtonID = rg.getCheckedRadioButtonId();
                    if (checkedButtonID == findViewById(R.id.rb01).getId())
                        isPass = "true";
                    else
                        isPass = "false";

                    //프로젝트 타이틀, 내용, 인원
                    projectName = etTitle.getText().toString();
                    projectDescription = etContents.getText().toString();
                    projectMemberNumber = Integer.parseInt(tvRecruitingNumber.getText().toString());

                    checkHoping();
                    checkField();
                    if (!projectName.equals("") && !projectDescription.equals("") && !projectClosingDate.equals(""))
                        registerProject();
                    else
                        Toast.makeText(ProjectRegisterActivity.this, "입력을 완성해주세요", Toast.LENGTH_SHORT).show();
                }
            });


            //제목 부분 글자수 체크
            TextWatcher watcher01 = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    byte[] bytes = null;
                    try {
                        bytes = s.toString().getBytes("EUC-KR");
                        int strCount = bytes.length;
                        tvTextNumber.setText(Integer.toString(strCount));
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String str = s.toString();
                    try {
                        byte[] strBytes = str.getBytes("EUC-KR");
                        if (strBytes.length > 50) {
                            s.delete(s.length() - 2, s.length() - 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            etTitle.addTextChangedListener(watcher01);


            //내용 부분 글자수 체크
            TextWatcher watcher02 = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    byte[] bytes = null;
                    try {
                        bytes = s.toString().getBytes("EUC-KR");
                        int strCount = bytes.length;
                        tvContentsNumber.setText(Integer.toString(strCount));
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String str = s.toString();
                    try {
                        byte[] strBytes = str.getBytes("EUC-KR");
                        if (strBytes.length > 400) {
                            s.delete(s.length() - 2, s.length() - 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            etContents.addTextChangedListener(watcher02);

    }

    private void checkHoping() {
        Log.d("rr", "111111111111111111111");
        CheckBox cbIOS = (CheckBox) findViewById(R.id.cbIOS);
        if (cbIOS.isChecked()) {
            final String IOS = "IOS";
            wantedSkillNames.add(IOS);
        }
        CheckBox cbSwift = (CheckBox) findViewById(R.id.cbSwift);
        if (cbSwift.isChecked()) {
            final String SWIFT = "SWIFT";
            wantedSkillNames.add(SWIFT);
        }
        CheckBox cbAndroid = (CheckBox) findViewById(R.id.cbAndroid);
        if (cbAndroid.isChecked()) {
            final String ANDROID = "ANDROID";
            wantedSkillNames.add(ANDROID);
        }
        CheckBox cbC = (CheckBox) findViewById(R.id.cbC);
        if (cbC.isChecked()) {
            final String C = "C";
            wantedSkillNames.add(C);
        }
        CheckBox cbCsharp = (CheckBox) findViewById(R.id.cbCsharp);
        if (cbCsharp.isChecked()) {
            final String CSHARP = "CSHARP";
            wantedSkillNames.add(CSHARP);
        }
        CheckBox cbObjectiveC = (CheckBox) findViewById(R.id.cbObjectiveC);
        if (cbObjectiveC.isChecked()) {
            final String OBJECTIVEC = "OBJECTIVEC";
            wantedSkillNames.add(OBJECTIVEC);
        }
        CheckBox cbJava = (CheckBox) findViewById(R.id.cbJava);
        if (cbJava.isChecked()) {
            final String JAVA = "JAVA";
            wantedSkillNames.add(JAVA);
        }
        CheckBox cbPython = (CheckBox) findViewById(R.id.cbPython);
        if (cbPython.isChecked()) {
            final String PYTHON = "PYTHON";
            wantedSkillNames.add(PYTHON);
        }
        CheckBox cbRuby = (CheckBox) findViewById(R.id.cbRuby);
        if (cbRuby.isChecked()) {
            final String RUBY = "RUBY";
            wantedSkillNames.add(RUBY);
        }
        CheckBox cbCss = (CheckBox) findViewById(R.id.cbCss);
        if (cbCss.isChecked()) {
            final String CSS = "CSS";
            wantedSkillNames.add(CSS);
        }
        CheckBox cbPhp = (CheckBox) findViewById(R.id.cbPhp);
        if (cbPhp.isChecked()) {
            final String PHP = "PHP";
            wantedSkillNames.add(PHP);
        }
        CheckBox cbJS = (CheckBox) findViewById(R.id.cbJS);
        if (cbJS.isChecked()) {
            final String JS = "JS";
            wantedSkillNames.add(JS);
        }
        CheckBox cbQA = (CheckBox) findViewById(R.id.cbQA);
        if (cbQA.isChecked()) {
            final String QA = "QA";
            wantedSkillNames.add(QA);
        }
        CheckBox cbHtml = (CheckBox) findViewById(R.id.cbHtml);
        if (cbHtml.isChecked()) {
            final String HTML = "HTML";
            wantedSkillNames.add(HTML);
        }
        CheckBox cbDjango = (CheckBox) findViewById(R.id.cbDjango);
        if (cbDjango.isChecked()) {
            final String DJANGO = "DJANGO";
            wantedSkillNames.add(DJANGO);
        }
        CheckBox cbJavaScript = (CheckBox) findViewById(R.id.cbJavaScript);
        if (cbJavaScript.isChecked()) {
            final String JAVASCRIPT = "JAVASCRIPT";
            wantedSkillNames.add(JAVASCRIPT);
        }
        CheckBox cbProductPlan = (CheckBox) findViewById(R.id.cbProductPlan);
        if (cbProductPlan.isChecked()) {
            final String PP = "PP";
            wantedSkillNames.add(PP);
        }
        CheckBox cbContentsPlan = (CheckBox) findViewById(R.id.cbContentsPlan);
        if (cbContentsPlan.isChecked()) {
            final String CP = "CP";
            wantedSkillNames.add(CP);
        }
        CheckBox cbBusuinessPlan = (CheckBox) findViewById(R.id.cbBusuinessPlan);
        if (cbBusuinessPlan.isChecked()) {
            final String BP = "BP";
            wantedSkillNames.add(BP);
        }
        CheckBox cbServicePlan = (CheckBox) findViewById(R.id.cbServicePlan);
        if (cbServicePlan.isChecked()) {
            final String SP = "SP";
            wantedSkillNames.add(SP);
        }
        CheckBox cbOperation = (CheckBox) findViewById(R.id.cbOperation);
        if (cbOperation.isChecked()) {
            final String OP = "OP";
            wantedSkillNames.add(OP);
        }
        CheckBox cbBrandStrategy = (CheckBox) findViewById(R.id.cbBrandStrategy);
        if (cbBrandStrategy.isChecked()) {
            final String BS = "BS";
            wantedSkillNames.add(BS);
        }
        CheckBox cbMargetingManager = (CheckBox) findViewById(R.id.cbMargetingManager);
        if (cbMargetingManager.isChecked()) {
            final String MM = "MM";
            wantedSkillNames.add(MM);
        }
        CheckBox cbServiceMarketor = (CheckBox) findViewById(R.id.cbServiceMarketor);
        if (cbServiceMarketor.isChecked()) {
            final String SM = "SM";
            wantedSkillNames.add(SM);
        }
        CheckBox cbAdMarketing = (CheckBox) findViewById(R.id.cbAdMarketing);
        if (cbAdMarketing.isChecked()) {
            final String AM = "AM";
            wantedSkillNames.add(AM);
        }
        CheckBox cbDataAnalysis = (CheckBox) findViewById(R.id.cbDataAnalysis);
        if (cbDataAnalysis.isChecked()) {
            final String DA = "DA";
            wantedSkillNames.add(DA);
        }
        CheckBox cbPR = (CheckBox) findViewById(R.id.cbPR);
        if (cbPR.isChecked()) {
            final String PR = "PR";
            wantedSkillNames.add(PR);
        }
        CheckBox cbIR = (CheckBox) findViewById(R.id.cbIR);
        if (cbIR.isChecked()) {
            final String IR = "IR";
            wantedSkillNames.add(IR);
        }
        CheckBox cbUIDesign = (CheckBox) findViewById(R.id.cbUIDesign);
        if (cbUIDesign.isChecked()) {
            final String UID = "UID";
            wantedSkillNames.add(UID);
        }
        CheckBox cbUXDesign = (CheckBox) findViewById(R.id.cbUXDesign);
        if (cbUXDesign.isChecked()) {
            final String UXD = "UXD";
            wantedSkillNames.add(UXD);
        }
        CheckBox cbillustDesign = (CheckBox) findViewById(R.id.cbillustDesign);
        if (cbillustDesign.isChecked()) {
            final String ID = "ID";
            wantedSkillNames.add(ID);
        }
        CheckBox cbGameDesign = (CheckBox) findViewById(R.id.cbGameDesign);
        if (cbGameDesign.isChecked()) {
            final String GAMED = "GAMED";
            wantedSkillNames.add(GAMED);
        }
        CheckBox cbGraphicDesign = (CheckBox) findViewById(R.id.cbGraphicDesign);
        if (cbGraphicDesign.isChecked()) {
            final String GRAPHICD = "GRAPHICD";
            wantedSkillNames.add(GRAPHICD);
        }
        CheckBox cbWebDesign = (CheckBox) findViewById(R.id.cbWebDesign);
        if (cbWebDesign.isChecked()) {
            final String WEBD = "WEBD";
            wantedSkillNames.add(WEBD);
        }
        CheckBox cbMobileDeisgn = (CheckBox) findViewById(R.id.cbMobileDeisgn);
        if (cbMobileDeisgn.isChecked()) {
            final String MOBILEDE = "MOBILEDE";
            wantedSkillNames.add(MOBILEDE);
        }
        CheckBox cbSales = (CheckBox) findViewById(R.id.cbSales);
        if (cbSales.isChecked()) {
            final String SALES = "SALES";
            wantedSkillNames.add(SALES);
        }
        CheckBox cbCreateDirector = (CheckBox) findViewById(R.id.cbCreateDirector);
        if (cbCreateDirector.isChecked()) {
            final String CD = "CD";
            wantedSkillNames.add(CD);
        }
        CheckBox cbRecruiting = (CheckBox) findViewById(R.id.cbRecruiting);
        if (cbRecruiting.isChecked()) {
            final String RECRUI = "RECRUI";
            wantedSkillNames.add(RECRUI);
        }
        CheckBox cbProjectManager = (CheckBox) findViewById(R.id.cbProjectManager);
        if (cbProjectManager.isChecked()) {
            final String PM = "PM";
            wantedSkillNames.add(PM);
        }
        CheckBox cbCEO = (CheckBox) findViewById(R.id.cbCEO);
        if (cbCEO.isChecked()) {
            final String CEO = "CEO";
            wantedSkillNames.add(CEO);
        }
        CheckBox cbCFO = (CheckBox) findViewById(R.id.cbCFO);
        if (cbCFO.isChecked()) {
            final String CFO = "CFO";
            wantedSkillNames.add(CFO);
        }
        CheckBox cbVP = (CheckBox) findViewById(R.id.cbVP);
        if (cbVP.isChecked()) {
            final String VP = "VP";
            wantedSkillNames.add(VP);
        }
        CheckBox cbCTO = (CheckBox) findViewById(R.id.cbCTO);
        if (cbCTO.isChecked()) {
            final String CTO = "CTO";
            wantedSkillNames.add(CTO);
        }
        CheckBox cbCOO = (CheckBox) findViewById(R.id.cbCOO);
        if (cbCOO.isChecked()) {
            final String COO = "COO";
            wantedSkillNames.add(COO);
        }
        CheckBox cbHR = (CheckBox) findViewById(R.id.cbHR);
        if (cbHR.isChecked()) {
            final String HR = "HR";
            wantedSkillNames.add(HR);
        }
        CheckBox cbCMO = (CheckBox) findViewById(R.id.cbCMO);
        if (cbCMO.isChecked()) {
            final String CMO = "CMO";
            wantedSkillNames.add(CMO);
        }
    }

    private void checkField() {
        Log.d("rr", "2222222222222222222222");
        CheckBox cbb2b = (CheckBox) findViewById(R.id.cbb2b);
        if (cbb2b.isChecked()) {
            projectFieldNames.add("cbb2b");
        }

        CheckBox cbb2c = (CheckBox) findViewById(R.id.cbb2c);
        if (cbb2c.isChecked()) {
            projectFieldNames.add("cbb2c");
        }

        CheckBox cbc2c = (CheckBox) findViewById(R.id.cbc2c);
        if (cbc2c.isChecked()) {
            projectFieldNames.add("cbc2c");
        }

        CheckBox cbo2o = (CheckBox) findViewById(R.id.cbo2o);
        if (cbo2o.isChecked()) {
            projectFieldNames.add("cbo2o");
        }

        CheckBox cbapp = (CheckBox) findViewById(R.id.cbapp);
        if (cbapp.isChecked()) {
            projectFieldNames.add("cbapp");
        }

        CheckBox cbbigdata = (CheckBox) findViewById(R.id.cbbigdata);
        if (cbbigdata.isChecked()) {
            projectFieldNames.add("cbbigdata");
        }

        CheckBox cbcommerce = (CheckBox) findViewById(R.id.cbcommerce);
        if (cbcommerce.isChecked()) {
            projectFieldNames.add("cbcommerce");
        }

        CheckBox cbnetwork = (CheckBox) findViewById(R.id.cbnetwork);
        if (cbnetwork.isChecked()) {
            projectFieldNames.add("cbnetwork");
        }

        CheckBox cbai = (CheckBox) findViewById(R.id.cbai);
        if (cbai.isChecked()) {
            projectFieldNames.add("cbai");
        }

        CheckBox cbrobot = (CheckBox) findViewById(R.id.cbrobot);
        if (cbrobot.isChecked()) {
            projectFieldNames.add("cbrobot");
        }

        CheckBox cbcar = (CheckBox) findViewById(R.id.cbcar);
        if (cbcar.isChecked()) {
            projectFieldNames.add("cbcar");
        }

        CheckBox cbdrone = (CheckBox) findViewById(R.id.cbdrone);
        if (cbdrone.isChecked()) {
            projectFieldNames.add("cbdrone");
        }

        CheckBox cbentertainment = (CheckBox) findViewById(R.id.cbentertainment);
        if (cbentertainment.isChecked()) {
            projectFieldNames.add("cbentertainment");
        }

        CheckBox cbEcommerce = (CheckBox) findViewById(R.id.cbEcommerce);
        if (cbEcommerce.isChecked()) {
            projectFieldNames.add("cbEcommerce");
        }

        CheckBox cbCommunity = (CheckBox) findViewById(R.id.cbCommunity);
        if (cbCommunity.isChecked()) {
            projectFieldNames.add("cbCommunity");
        }

        CheckBox cbWebtoon = (CheckBox) findViewById(R.id.cbWebtoon);
        if (cbWebtoon.isChecked()) {
            projectFieldNames.add("cbWebtoon");
        }

        CheckBox cbFashion = (CheckBox) findViewById(R.id.cbFashion);
        if (cbFashion.isChecked()) {
            projectFieldNames.add("cbFashion");
        }

        CheckBox cbMusic = (CheckBox) findViewById(R.id.cbMusic);
        if (cbMusic.isChecked()) {
            projectFieldNames.add("cbMusic");
        }

        CheckBox cbMegazine = (CheckBox) findViewById(R.id.cbMegazine);
        if (cbMegazine.isChecked()) {
            projectFieldNames.add("cbMegazine");
        }

        CheckBox cbBlog = (CheckBox) findViewById(R.id.cbBlog);
        if (cbBlog.isChecked()) {
            projectFieldNames.add("cbBlog");
        }

        CheckBox cbLifeStyle = (CheckBox) findViewById(R.id.cbLifeStyle);
        if (cbLifeStyle.isChecked()) {
            projectFieldNames.add("cbLifeStyle");
        }

        CheckBox cbMoney = (CheckBox) findViewById(R.id.cbMoney);
        if (cbMoney.isChecked()) {
            projectFieldNames.add("cbMoney");
        }

        CheckBox cbTrip = (CheckBox) findViewById(R.id.cbTrip);
        if (cbTrip.isChecked()) {
            projectFieldNames.add("cbTrip");
        }

        CheckBox cbGame = (CheckBox) findViewById(R.id.cbGame);
        if (cbGame.isChecked()) {
            projectFieldNames.add("cbGame");
        }

        CheckBox cbFood = (CheckBox) findViewById(R.id.cbFood);
        if (cbFood.isChecked()) {
            projectFieldNames.add("cbFood");
        }

        CheckBox cbSchedule = (CheckBox) findViewById(R.id.cbSchedule);
        if (cbSchedule.isChecked()) {
            projectFieldNames.add("cbSchedule");
        }

        CheckBox cbHobby = (CheckBox) findViewById(R.id.cbHobby);
        if (cbHobby.isChecked()) {
            projectFieldNames.add("cbHobby");
        }

        CheckBox cbFitness = (CheckBox) findViewById(R.id.cbFitness);
        if (cbFitness.isChecked()) {
            projectFieldNames.add("cbFitness");
        }

        CheckBox cbBitCoin = (CheckBox) findViewById(R.id.cbBitCoin);
        if (cbBitCoin.isChecked()) {
            projectFieldNames.add("cbBitCoin");
        }

        CheckBox cbBioTech = (CheckBox) findViewById(R.id.cbBioTech);
        if (cbBioTech.isChecked()) {
            projectFieldNames.add("cbBioTech");
        }

        CheckBox cbSocial = (CheckBox) findViewById(R.id.cbSocial);
        if (cbSocial.isChecked()) {
            projectFieldNames.add("cbSocial");
        }

        CheckBox cbFinTech = (CheckBox) findViewById(R.id.cbFinTech);
        if (cbFinTech.isChecked()) {
            projectFieldNames.add("cbFinTech");
        }

        CheckBox cbHybrid = (CheckBox) findViewById(R.id.cbHybrid);
        if (cbHybrid.isChecked()) {
            projectFieldNames.add("cbHybrid");
        }

        CheckBox cbHealthCare = (CheckBox) findViewById(R.id.cbHealthCare);
        if (cbHealthCare.isChecked()) {
            projectFieldNames.add("cbHealthCare");
        }

        CheckBox cbEducation = (CheckBox) findViewById(R.id.cbEducation);
        if (cbEducation.isChecked()) {
            projectFieldNames.add("cbEducation");
        }

        CheckBox cbEBook = (CheckBox) findViewById(R.id.cbEBook);
        if (cbEBook.isChecked()) {
            projectFieldNames.add("cbEBook");
        }

        CheckBox cbSpace = (CheckBox) findViewById(R.id.cbSpace);
        if (cbSpace.isChecked()) {
            projectFieldNames.add("cbSpace");
        }

        CheckBox cbSocialCompany = (CheckBox) findViewById(R.id.cbSocialCompany);
        if (cbSocialCompany.isChecked()) {
            projectFieldNames.add("cbSocialCompany");
        }

        CheckBox cbPicture = (CheckBox) findViewById(R.id.cbPicture);
        if (cbPicture.isChecked()) {
            projectFieldNames.add("cbPicture");
        }

        CheckBox cbEnergy = (CheckBox) findViewById(R.id.cbEnergy);
        if (cbEnergy.isChecked()) {
            projectFieldNames.add("cbEnergy");
        }

        CheckBox cbWellBeing = (CheckBox) findViewById(R.id.cbWellBeing);
        if (cbWellBeing.isChecked()) {
            projectFieldNames.add("cbWellBeing");
        }

        CheckBox cbRecruiting = (CheckBox) findViewById(R.id.cbRecruiting);
        if (cbRecruiting.isChecked()) {
            projectFieldNames.add("cbRecruiting");
        }

        CheckBox cbWedding = (CheckBox) findViewById(R.id.cbWedding);
        if (cbWedding.isChecked()) {
            projectFieldNames.add("cbWedding");
        }

        CheckBox cbDoctor = (CheckBox) findViewById(R.id.cbDoctor);
        if (cbDoctor.isChecked()) {
            projectFieldNames.add("cbDoctor");
        }

        CheckBox cbArt = (CheckBox) findViewById(R.id.cbArt);
        if (cbArt.isChecked()) {
            projectFieldNames.add("cbArt");
        }

        CheckBox cbProductivity = (CheckBox) findViewById(R.id.cbProductivity);
        if (cbProductivity.isChecked()) {
            projectFieldNames.add("cbProductivity");
        }

        CheckBox cbCoWork = (CheckBox) findViewById(R.id.cbCoWork);
        if (cbCoWork.isChecked()) {
            projectFieldNames.add("cbCoWork");
        }

        CheckBox cbCompare = (CheckBox) findViewById(R.id.cbCompare);
        if (cbCompare.isChecked()) {
            projectFieldNames.add("cbCompare");
        }
    }

    /*************************************프로젝트 등록******************************************/
    private void registerProject() {

        Call<ProjectRegisterResult> requestProjectRegister = service.registerProject(Config.userID, projectName, 2, projectMemberNumber, projectDescription,
                "모집", isPass, projectClosingDate, wantedSkillNames, projectFieldNames);


        requestProjectRegister.enqueue(new Callback<ProjectRegisterResult>() {
            @Override
            public void onResponse(Call<ProjectRegisterResult> call, Response<ProjectRegisterResult> response) {
                if (response.isSuccessful()) {
                    if (response.body().msg.equals("success")) {
                        Log.d("ttt", "rrrrrrrrrrr");
                        ShowRegisterDialog();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectRegisterResult> call, Throwable t) {
                Log.i("err", t.getMessage());
            }
        });

    }

    /*************************************등록 다이얼로그************************************/

    private void ShowRegisterDialog() {
        LayoutInflater dialog = LayoutInflater.from(this);
        final View dialogLayout;
        final Dialog myDialog;

        dialogLayout = dialog.inflate(R.layout.dialog_register, null);
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
}

