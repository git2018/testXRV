package com.albert.testxrv;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import cn.wswin.widget.XReaderListener;
import cn.wswin.widget.XReaderView;

public class MainActivity extends AppCompatActivity {

    private String path = Environment.getExternalStorageDirectory()+ "/test.ppt";
    private String path1 = "http://192.168.16.16/test.xlsx";
    private XReaderView xReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xReaderView = findViewById(R.id.xReaderView);

        TedPermission.with(MainActivity.this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
//                        xReaderView.display(path,"test.docx");
                        path1 = "http://androidoffice.com/2FFC52C82873B95266E0AC963B4440D1.txt";
                        xReaderView.display(path1, "test.txt");
//                        xReaderView.display(path1, "test.xlsssx", new XReaderListener() {
//                            @Override
//                            public void onEnvInit() {
//                                Toast.makeText(MainActivity.this,"onEnvInit",Toast.LENGTH_LONG).show();
//                            }
//
//                            @Override
//                            public void onEnvLoad() {
//                                Toast.makeText(MainActivity.this,"onEnvLoad",Toast.LENGTH_LONG).show();
//                            }
//
//                            @Override
//                            public void onSuccess() {
//                                Toast.makeText(MainActivity.this,"onSuccess",Toast.LENGTH_LONG).show();
//                            }
//
//                            @Override
//                            public void onError(String msg) {
//                                Toast.makeText(MainActivity.this,"onError -> "+msg,Toast.LENGTH_LONG).show();
//                            }
//                        });
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(MainActivity.this, "需要允许访问SD卡", Toast.LENGTH_LONG).show();
                    }
                })
                .setDeniedMessage("需要允许访问SD卡")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        xReaderView.destroy();
    }
}
