package cn.wswin.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

class XReader {

    private static XReader util;
    private Context context;
    private TbsReaderView mTbsReaderView;
    private String mFilePath,mFileName;
    private XReaderListener mListener;
    private static String XReaderDir = Environment.getExternalStorageDirectory()+ "/.XReader";

    private XReader(){ }

    public static XReader getInstance() {
        if (util == null) {
            synchronized (XReader.class) {
                if (util == null) {
                    util = new XReader();
                }
            }
        }
        return util;
    }

    public void initXReader(Context context, TbsReaderView mTbsReaderView, String mFilePath, String mFileName) {
        this.context = context;
        this.mTbsReaderView = mTbsReaderView;
        this.mFilePath = mFilePath;
        this.mFileName = mFileName;
        XReaderUtil.createDir(XReaderDir);
    }

    private void initEnv() {

        if (QbSdk.getTbsVersion(context) == 0) {//未安装
            mListener.onEnvNull();
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

    public void delTempDir(){
        XReaderUtil.deleteDirectory(XReaderDir);
    }

    private void display(){
        Bundle bundle = new Bundle();
        bundle.putString("filePath", mFilePath);
        bundle.putString("tempPath", XReaderDir);
        boolean bool = mTbsReaderView.preOpen(XReaderUtil.getFileType(mFileName), false);
        if (bool) {
            mTbsReaderView.openFile(bundle);
        }
    }

    public void start(final XReaderListener listener) {
        this.mListener = listener;

        if (mFilePath.contains("http")) {//网络地址要先下载
            new DownloaderTask().execute();
        } else {
            mListener.onFileOk();
            initEnv();
        }
    }


    @SuppressLint("StaticFieldLeak")
    class DownloaderTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(mFilePath);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();

                if (urlConn.getResponseCode() == 200) {
                    InputStream input = urlConn.getInputStream();

                    mFileName = URLDecoder.decode(mFileName);
                    mFilePath = XReaderDir + "/" + mFileName;

                    XReaderUtil.writeToSDCard(mFilePath,input);
                    input.close();
                    return "";
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mListener.onFileOk();
            initEnv();
        }
    }
}
