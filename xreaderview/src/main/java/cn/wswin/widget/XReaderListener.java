package cn.wswin.widget;

public interface XReaderListener {
    void onFileLoad();
    void onEnvInit();
    void onEnvLoad();
    void onSuccess();
    void onError(String msg);
}
