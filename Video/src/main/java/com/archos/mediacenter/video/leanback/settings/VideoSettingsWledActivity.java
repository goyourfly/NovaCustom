package com.archos.mediacenter.video.leanback.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.LeanbackActivity;
import com.archos.mediacenter.video.widget.PreviewView;
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
        EditText etBrightness = findViewById(R.id.et_brightness);

        WledInfo info = read();
        etIp.setText(info.ip);
        etPort.setText(String.valueOf(info.port));
        etLeftNum.setText(String.valueOf(info.leftNum));
        etTopNum.setText(String.valueOf(info.topNum));
        etRightNum.setText(String.valueOf(info.rightNum));
        etBottomNum.setText(String.valueOf(info.bottomNum));
        etLeftPadding.setText(String.valueOf(info.leftPadding));
        etTopPadding.setText(String.valueOf(info.topPadding));
        etRightPadding.setText(String.valueOf(info.rightPadding));
        etBottomPadding.setText(String.valueOf(info.bottomPadding));
        etInset.setText(String.valueOf(info.inset));
        etBrightness.setText(String.valueOf(info.brightness));

        PreviewView preview = findViewById(R.id.v_preview);

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
                info.brightness = Integer.parseInt(etBrightness.getText().toString());

                save(info);
                preview.invalidate();
            }
        });
    }

    public static void save(WledInfo info){
        Paper.book().write("wled_config",info);
    }

    public static WledInfo read() {
        WledInfo wledInfo = Paper.book().read("wled_config");
        if (wledInfo == null){
            wledInfo = new WledInfo();
            wledInfo.ip = "192.168.2.247";
            wledInfo.port = 21324;
            wledInfo.leftNum = 40;
            wledInfo.topNum = 69;
            wledInfo.rightNum = 40;
            wledInfo.bottomNum = 0;
            wledInfo.leftPadding = 10;
            wledInfo.topPadding = 10;
            wledInfo.rightPadding = 10;
            wledInfo.bottomPadding = 0;
            wledInfo.inset = 0;
            wledInfo.brightness = 100;
        }
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
        public int brightness;
    }
}