package cn.wswin.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
        createDir(XReaderDir);
    }

    private void initEnvironment() {

        if (QbSdk.getTbsVersion(context) == 0) {//未安装
            mListener.onEnvInit();
            QbSdk.initX5Environment(context.getApplicationContext(), new QbSdk.PreInitCallback() {
                @Override
                public void onCoreInitFinished() {
                }

                @Override
                public void onViewInitFinished(boolean b) {
                    display();
                }
            });
        } else {
            if (QbSdk.isTbsCoreInited()){//已加载
                display();
            }else {//未加载
                mListener.onEnvLoad();
                QbSdk.initX5Environment(context.getApplicationContext(), new QbSdk.PreInitCallback() {
                    @Override
                    public void onCoreInitFinished() {
                    }

                    @Override
                    public void onViewInitFinished(boolean b) {
                        display();
                    }
                });
            }
        }
    }

    private void display(){
        File file = new File(mFilePath);
        if (!file.exists()){
            mListener.onError("文件获取失败");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("filePath", mFilePath);
        bundle.putString("tempPath", XReaderDir);
        boolean bool = mTbsReaderView.preOpen(getFileType(mFileName), false);
        if (bool) {
            mTbsReaderView.openFile(bundle);
            mListener.onSuccess();
        }else {
            mListener.onError("不支持" + getFileType(mFileName) + "格式");
        }
    }

    public void setOnXReaderListener(final XReaderListener listener) {
        this.mListener = listener;
        mListener.onFileLoad();
        if (mFilePath.contains("http")) {//网络地址要先下载
            new DownloaderTask().execute();
        } else {
            initEnvironment();
        }
    }

    public void clear(){
        deleteDir(XReaderDir);
    }

//**************************************  以下为辅助方法区  **************************************//
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

                    writeToSDCard(mFilePath,input);
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
            initEnvironment();
        }
    }

    private String getFileType(String mFileName) {
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

    private void createDir(String path) {
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, ".nomedia");
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {

        }
    }

    private void writeToSDCard(String mFilePath,InputStream input) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(mFilePath);
            File fileParent = file.getParentFile();
            if (! fileParent.exists()){
                fileParent.mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                FileOutputStream fos = new FileOutputStream(file);
                byte[] b = new byte[2048];
                int j = 0;

                while ((j = input.read(b)) != -1) {
                    fos.write(b, 0, j);
                }
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mListener.onError("未找到SD卡");
        }
    }

    private boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean deleteDir(String dir) {
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDir(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }


}
