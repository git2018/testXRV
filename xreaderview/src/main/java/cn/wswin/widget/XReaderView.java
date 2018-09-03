package cn.wswin.widget;

import android.app.ProgressDialog;
import android.content.Context;
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

    public void display(final String url,final String name) {
        XReader.getInstance().initXReader(context,mTbsReaderView,url,name);

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        XReader.getInstance().start(new XReaderListener() {

            @Override
            public void onFileOk() {
                dialog.setMessage("载入中 ... ");
            }

            @Override
            public void onEnvNull() {
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
            XReader.getInstance().delTempDir();
            mTbsReaderView.onStop();
        }
    }
}
