package cn.wswin.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tencent.smtt.sdk.TbsReaderView;

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

    public void display(final String filePath,final String fileName,XReaderListener listener) {
        XReader.getInstance().initXReader(context,mTbsReaderView,filePath,fileName);
        XReader.getInstance().setOnXReaderListener(listener);
    }

    public void display(final String filePath,final String fileName) {
        XReader.getInstance().initXReader(context,mTbsReaderView,filePath,fileName);

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        XReader.getInstance().setOnXReaderListener(new XReaderListener() {

            @Override
            public void onFileLoad() {
                dialog.setMessage("正在下载文件 ... ");
            }

            @Override
            public void onEnvInit() {
                dialog.setMessage("首次加载，请耐心等待 ... ");
            }

            @Override
            public void onEnvLoad() {
                dialog.setMessage("组件加载中 ... ");
            }

            @Override
            public void onSuccess() {
                dialog.dismiss();
            }

            @Override
            public void onError(String msg) {
                dialog.dismiss();
                AlertDialog dialog = new AlertDialog.Builder(context)
//                        .setIcon(R.mipmap.icon)//设置标题的图片
                        .setTitle("错误信息")//设置对话框的标题
                        .setMessage(msg)//设置对话框的内容
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    public void destroy() {
        if (mTbsReaderView != null) {
            XReader.getInstance().clear();
            mTbsReaderView.onStop();
        }
    }
}
