package com.archos.mediacenter.video.leanback.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.LeanbackActivity;
import com.google.gson.Gson;

import io.paperdb.Paper;

public class VideoSettingsWledActivity extends LeanbackActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_settings_wled);
        overridePendingTransition(R.anim.slide_in_from_right, 0);

        EditText etIp = findViewById(R.id.et_ip_address);
        EditText etPort = findViewById(R.id.et_port);

        EditText etLeftNum = findViewById(R.id.et_left_num);
        EditText etTopNum = findViewById(R.id.et_top_num);
        EditText etRightNum = findViewById(R.id.et_right_num);
        EditText etBottomNum = findViewById(R.id.et_bottom_num);

        EditText etLeftPadding = findViewById(R.id.et_left_padding);
        EditText etTopPadding = findViewById(R.id.et_top_padding);
        EditText etRightPadding = findViewById(R.id.et_right_padding);
        EditText etBottomPadding = findViewById(R.id.et_bottom_padding);

        EditText etInset = findViewById(R.id.et_inset);

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WledInfo info = new WledInfo();
                info.ip = etIp.getText().toString();
                info.port = Integer.parseInt(etPort.getText().toString());

                info.leftNum = Integer.parseInt(etLeftNum.getText().toString());
                info.topNum = Integer.parseInt(etTopNum.getText().toString());
                info.rightNum = Integer.parseInt(etRightNum.getText().toString());
                info.bottomNum = Integer.parseInt(etBottomNum.getText().toString());

                info.leftPadding = Integer.parseInt(etLeftPadding.getText().toString());
                info.topPadding = Integer.parseInt(etTopPadding.getText().toString());
                info.rightPadding = Integer.parseInt(etRightPadding.getText().toString());
                info.bottomPadding = Integer.parseInt(etBottomPadding.getText().toString());

                info.inset = Integer.parseInt(etInset.getText().toString());

                save(info);
            }
        });
    }

    public static void save(WledInfo info){
        Paper.book().write("wled_config",info);
    }

    public static WledInfo read() {
        WledInfo wledInfo = Paper.book().read("wled_config");
        return wledInfo;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // FIXME
        overridePendingTransition(0, R.anim.slide_out_to_right);
    }

    public static class WledInfo{
        public String ip;
        public int port;
        public int leftNum;
        public int topNum;
        public int rightNum;
        public int bottomNum;
        public int leftPadding;
        public int topPadding;
        public int rightPadding;
        public int bottomPadding;
        public int inset;
    }
}