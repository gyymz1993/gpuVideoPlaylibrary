package itbour.onetouchshow.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import itbour.onetouchshow.base.R;
import itbour.onetouchshow.utils.UIUtils;


/**
 * @author: gyymz1993
 * 创建时间：2017/4/26 16:54
 **/
public class NavigationBarView extends RelativeLayout {

    private String letfText;
    private String rightText;
    private int letfTextColor;
    private int rightTextColor;
    private Drawable leftIcon;
    private Drawable rightIcon;
    private int bgColor;
    public String titleText;
    public View mView;
    private int height;
    private int titleColor;
    private TextView leftTv;
    private TextView rightTv;
    private ImageView rightImg;
    private Context mContext;

    public NavigationBarView(Context context) {
        this(context, null);
    }

    public NavigationBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public NavigationBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        if (attrs != null) {// 得到自定义属性
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBarItem);
            leftIcon = ta.getDrawable(R.styleable.TopBarItem_leftIcon);
            rightIcon = ta.getDrawable(R.styleable.TopBarItem_rgihtIcon);
            titleText = ta.getString(R.styleable.TopBarItem_titles);
            letfText = ta.getString(R.styleable.TopBarItem_leftText);
            rightText = ta.getString(R.styleable.TopBarItem_rightText);
            height = ta.getInteger(R.styleable.TopBarItem_topBarheight, UIUtils.dip2px(50));
            bgColor = ta.getColor(R.styleable.TopBarItem_backgrounds, Color.parseColor("#FFFFFFFF"));
            letfTextColor = ta.getColor(R.styleable.TopBarItem_leftColor, Color.parseColor("#ffffff"));
            rightTextColor = ta.getColor(R.styleable.TopBarItem_rightColor, Color.parseColor("#ffffff"));
            ta.recycle();
        }

        initView(context);
    }

    private void initView(Context context) {
        // 将view添加。
        mView = View.inflate(context, R.layout.navigation_default, this);
        leftTv = mView.findViewById(R.id.tv_left);
        rightTv = mView.findViewById(R.id.tv_right);
        rightImg = viewFindById(R.id.iv_right);
        if (letfText != null) {
            setLeftText(letfText);
        }
        if (rightText != null) {
            setRightText(rightText);
        }
        if (leftIcon != null) {
            setleftImageResource(leftIcon);
        }else {
            setleftImageResource(UIUtils.getDrawable(R.drawable.icon_back));
        }
        if (rightIcon != null) {
            setRightImageResource(rightIcon);
        }else {
            setRightImageResource(UIUtils.getDrawable(R.drawable.icon_share));
        }
        if (titleText != null) {
            setTitleText(titleText);
        }
        if (height!=0){
            setTorBarTHeight(height);
        }
        if (titleColor!=0){
            setTitleTextColor(titleColor);
        }else {
            setTitleTextColor(Color.parseColor("#FF212121"));
        }
        if (bgColor!=0){
            setBackgroundColor(bgColor);
        }

        if (letfTextColor!=0){
            setLeftTextColor(letfTextColor);
        }

        if (rightTextColor!=0){
            setRightTextColor(rightTextColor);
        }


        getRightImageView().setVisibility(GONE);
        getRightTextView().setVisibility(GONE);
    }

    public ImageView getLeftimageView() {
        return viewFindById(R.id.iv_left);
    }

    public <T extends View> T viewFindById(int id) {
        return (T) mView.findViewById(id);
    }

    public NavigationBarView setTitleText(String text) {
        TextView textView = viewFindById(R.id.title_tv);
        if (textView != null && text != null) {
            textView.setText(text);
        }
        return this;
    }

    public NavigationBarView setTitleTextColor(int color) {
        TextView textView = viewFindById(R.id.title_tv);
        if (textView != null && color != 0) {
            textView.setTextColor(color);
        }
        return this;
    }


    public NavigationBarView setTorBarTHeight(int height) {
        RelativeLayout relativeLayout = viewFindById(R.id.title_bar);
        ViewGroup.LayoutParams layoutParams = relativeLayout.getLayoutParams();
        layoutParams.width= UIUtils.WHD()[0];
        layoutParams.height=height;
        return this;
    }

    public NavigationBarView setLeftText(CharSequence text) {
        if (leftTv != null && text != null) {
            leftTv.setVisibility(View.VISIBLE);
            leftTv.setText(text);
        }
        return this;
    }

    public NavigationBarView setRightImageVisible() {
        rightImg.setVisibility(VISIBLE);
        return this;
    }

    public NavigationBarView setRightText(CharSequence text) {
        if (rightTv != null && text != null) {
            rightTv.setVisibility(View.VISIBLE);
            rightTv.setText(text);
        }
        return this;
    }

    @Override
    public void setBackgroundColor(int color) {
        View view = viewFindById(R.id.title_bar);
        if (view != null && color != 0) {
            view.setBackgroundColor(color);
        }
    }

    public NavigationBarView setTopBarBackgroundColor(int color) {
        View view = viewFindById(R.id.title_bar);
        if (view != null && color != 0) {
            view.setBackgroundColor(color);
        }
        return this;
    }

    public NavigationBarView setLeftTextColor(int color) {
        if (leftTv != null && color != 0) {
            leftTv.setTextColor(color);
        }
        return this;
    }

    public NavigationBarView setRightTextColor(int color) {
        if (rightTv != null && color != 0) {
            rightTv.setTextColor(color);
        }
        return this;
    }


    public NavigationBarView setLetfTextOnClick(OnClickListener listener) {
        if (leftTv != null) {
            leftTv.setOnClickListener(listener);
        }
        return this;
    }


    public NavigationBarView setLetfIocnOnClick(OnClickListener listener) {
        View view = viewFindById(R.id.iv_left);
        if (view != null) {
            view.setOnClickListener(listener);
        }
        return this;
    }


    public NavigationBarView setRightIocnOnClick(OnClickListener listener) {
        if (rightImg != null) {
            rightImg.setOnClickListener(listener);
        }
        return this;
    }

    public NavigationBarView setRightTextOnClick(OnClickListener listener) {
        if (rightTv != null) {
            rightTv.setOnClickListener(listener);
        }
        return this;
    }

    /**
     * 设置背景资源
     *
     * @param resourceId
     */
    public NavigationBarView setleftImageResource(Drawable resourceId) {
        ImageView imageView = viewFindById(R.id.iv_left);
        if (imageView != null) {
            imageView.setImageDrawable(resourceId);
        }
        return this;
    }

    /**
     * 设置背景资源
     *
     * @param resourceId
     */
    public NavigationBarView setRightImageResource(Drawable resourceId) {
        if (rightImg != null) {
            rightImg.setImageDrawable(resourceId);
        }
        return this;
    }

    public ImageView getRightImageView() {
        return rightImg;
    }

    public TextView getRightTextView() {
        return rightTv;
    }

    public TextView getLeftTextView() {
        return leftTv;
    }


}
