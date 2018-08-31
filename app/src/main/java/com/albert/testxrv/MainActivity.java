package com.albert.testxrv;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import cn.wswin.widget.XReaderView;

public class MainActivity extends AppCompatActivity {

    private String path = Environment.getExternalStorageDirectory()+ "/test.docx";
    private XReaderView xReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xReaderView = findViewById(R.id.xReaderView);
        xReaderView.display(new File(path));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        xReaderView.stopDisplay();
    }
}
