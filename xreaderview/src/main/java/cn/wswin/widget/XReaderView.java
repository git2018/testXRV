package cn.wswin.widget;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;
import java.util.ArrayList;

public class XReaderView extends FrameLayout {

    private TbsReaderView mTbsReaderView;
    private Context context;

    public XReaderView(Context context) {
        this(context, null, 0);
    }

    public XReaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XReaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        mTbsReaderView = new TbsReaderView(context, new TbsReaderView.ReaderCallback() {
            @Override
            public void onCallBackAction(Integer integer, Object o, Object o1) {

            }
        });
        this.addView(mTbsReaderView, new LinearLayout.LayoutParams(-1, -1));
    }

    public void display(final File mFile){
        TedPermission.with(context)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        display1(mFile);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        Toast.makeText(context, "需要允许访问SD卡", Toast.LENGTH_LONG).show();
                    }
                })
                .setDeniedMessage("需要允许访问SD卡")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void display1(final File mFile) {
        XReaderUtil.getInstance().initXReaderUtil(context,mTbsReaderView,mFile);

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        XReaderUtil.getInstance().initEnv(new XReaderListener() {
            @Override
            public void onFirst() {
                dialog.setMessage("首次加载，请耐心等待 ... ");
            }

            @Override
            public void onEnvPrepare() {
                dialog.setMessage("组件加载中 ... ");
            }

            @Override
            public void onEnvOk() {
                dialog.dismiss();
            }
        });
    }

    public void stopDisplay() {
        if (mTbsReaderView != null) {
            XReaderUtil.getInstance().delTempDir();
            mTbsReaderView.onStop();
        }
    }
}
