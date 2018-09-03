package cn.wswin.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

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
    private String workDir = Environment.getExternalStorageDirectory()+ "";

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

    private String getFileType() {
        String str = "";
        if (TextUtils.isEmpty(mFileName)) {
            return str;
        }
        int i = mFileName.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }
        str = mFileName.substring(i + 1);
        return str;
    }

//    private String getTempDir(){
//        //创建缓存
//        File mFile = new File(mFilePath);
//        String bsReaderTemp = mFile.getParent() + "/Temp";
//        File bsReaderTempFile = new File(bsReaderTemp);
//        if (!bsReaderTempFile.exists()) {
//            bsReaderTempFile.mkdir();
//        }
//        return bsReaderTemp;
//    }

    public void delTempDir(){
//        Toast.makeText(context,"清理缓存",Toast.LENGTH_LONG).show();
        FileUtil.delete(mFilePath);
        FileUtil.deleteDirectory(workDir);
    }

    private void display(){
        Bundle localBundle = new Bundle();
        localBundle.putString("filePath", mFilePath);
        localBundle.putString("tempPath", workDir+ "/Temp");
        boolean bool = mTbsReaderView.preOpen(getFileType(), false);
        if (bool) {
            mTbsReaderView.openFile(localBundle);
        }
    }

    public void start(final XReaderListener listener) {
        this.mListener = listener;

        if (mFilePath.contains("http")) {//网络地址要先下载
            new DownloaderTask().execute(mFilePath,mFileName);
        } else {
            mListener.onFileOk();
            initEnv();
        }
    }


    @SuppressLint("StaticFieldLeak")
    class DownloaderTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String name = params[1];
            String fileName = name;
            fileName = URLDecoder.decode(fileName);
            try {
                URL url1 = new URL(url);
                HttpURLConnection urlConn = (HttpURLConnection) url1.openConnection();

                if (urlConn.getResponseCode() == 200) {
                    InputStream input = urlConn.getInputStream();
                    mFilePath = workDir + "/" + mFileName;
                    FileUtil.writeToSDCard(mFilePath,input);
                    input.close();
                    return fileName;
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
