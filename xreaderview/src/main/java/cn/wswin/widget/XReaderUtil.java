package cn.wswin.widget;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

class XReaderUtil {

    private static XReaderUtil util;
    private Context context;
    private TbsReaderView mTbsReaderView;
    private File mFile;
    private XReaderListener mListener;

    private XReaderUtil(){ }

    public static XReaderUtil getInstance() {
        if (util == null) {
            synchronized (XReaderUtil.class) {
                if (util == null) {
                    util = new XReaderUtil();
                }
            }
        }
        return util;
    }

    public void initXReaderUtil(Context context,TbsReaderView mTbsReaderView,File mFile) {
        this.context = context;
        this.mTbsReaderView = mTbsReaderView;
        this.mFile = mFile;
    }

    public void initEnv(final XReaderListener listener) {
        mListener = listener;

        if (QbSdk.getTbsVersion(context) == 0) {//未安装
            mListener.onFirst();
            initX5();
        } else {
            if (QbSdk.isTbsCoreInited()){//已加载
                mListener.onEnvOk();
                display();
            }else {//未加载
                mListener.onEnvPrepare();
                initX5();
            }
        }
    }

    private void initX5(){
        QbSdk.initX5Environment(context.getApplicationContext(), new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
            }

            @Override
            public void onViewInitFinished(boolean b) {
                mListener.onEnvOk();
                display();
            }
        });
    }

    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }

        str = paramString.substring(i + 1);
        return str;
    }

    private String getTempDir(){
        //创建缓存
        String bsReaderTemp = mFile.getParent() + "/Temp";
        File bsReaderTempFile = new File(bsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            bsReaderTempFile.mkdir();
        }
        return bsReaderTemp;
    }

    public void delTempDir(){
        Toast.makeText(context,"清理缓存",Toast.LENGTH_LONG).show();
    }

    public void display(){
        Bundle localBundle = new Bundle();
        localBundle.putString("filePath", mFile.toString());
        localBundle.putString("tempPath", getTempDir());
        boolean bool = mTbsReaderView.preOpen(getFileType(mFile.toString()), false);
        if (bool) {
            mTbsReaderView.openFile(localBundle);
        }
    }
}
