package cn.wswin.widget;

public interface XReaderListener {
    void onCreate();
    void onLoading();
    void onSuccess();
    void onError(String msg);
}
