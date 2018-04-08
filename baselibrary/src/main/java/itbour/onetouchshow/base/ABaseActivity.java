package itbour.onetouchshow.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import itbour.onetouchshow.listener.PermissionListener;
import itbour.onetouchshow.utils.UIUtils;
import itbour.onetouchshow.view.ActivityUtils;
import itbour.onetouchshow.view.NavigationBarView;
import itbour.onetouchshow.view.NetworkStateView;
import itbour.onetouchshow.view.ShowDialogUtils;

/**
 * 创建人：$ gyymz1993
 * 创建时间：2017/6/28 14:21
 */

public abstract class ABaseActivity extends BaseActivity implements NetworkStateView.OnRefreshListener {


    private Unbinder unbinder;

    private ShowDialogUtils progressDialog;

    private NetworkStateView networkStateView;

    private PermissionListener mPermissionListener;
    private static final int CODE_REQUEST_PERMISSION = 1;

    private OnTopBarClickListener onTopBarLeftListener;
    private OnTopBarClickListener onTopBarRightListener;
    private NavigationBarView navigationBarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        initPresenter();
        ActivityUtils.addActivity(this);
        initDialog();
        afterCreate(savedInstanceState);
        // 初始化界面
        initView();

        // 初始化头部
        initTitle();

        // 初始化数据
        initData();


    }


    protected abstract void initPresenter();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化界面
     */
    protected abstract void initView();

    /**
     * 初始化头部
     */
    protected abstract void initTitle();


    @SuppressLint("InflateParams")
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View view = getLayoutInflater().inflate(R.layout.activity_a_base, null);
        //设置填充activity_base布局
        super.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setFitsSystemWindows(true);
        }
        //加载子类Activity的布局
        initDefaultView(layoutResID);
    }

    public LinearLayout getRootView() {
        LinearLayout viewById = getLayoutInflater().inflate(R.layout.activity_a_base, null)
                .findViewById(R.id.ll_base_root);
        return viewById;
    }

    /**
     * 初始化默认布局的View
     *
     * @param layoutResId 子View的布局id
     */
    @SuppressLint("RestrictedApi")
    private void initDefaultView(int layoutResId) {
        networkStateView = (NetworkStateView) findViewById(R.id.nsv_state_view);
        navigationBarView = (NavigationBarView) findViewById(R.id.id_bar_view);
        navigationBarView.setVisibility(View.GONE);
        navigationBarView.setLetfIocnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FrameLayout container = (FrameLayout) findViewById(R.id.fl_activity_child_container);
        View childView = LayoutInflater.from(this).inflate(layoutResId, null);
        container.addView(childView, 0);
    }


    public NavigationBarView getToolBarView() {
        return navigationBarView;
    }


    public NavigationBarView rightImageViewVisible() {
        navigationBarView.getRightImageView().setVisibility(View.VISIBLE);
        return navigationBarView;
    }

    public NavigationBarView setTitleText(String title) {
        navigationBarView.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(title)) {
            navigationBarView.setTitleText(title);
        }
        return navigationBarView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (onTopBarLeftListener != null) {
                onTopBarLeftListener.onClick();
            }
        }
        return true;
    }


    /*默认 是返回键*/
    protected NavigationBarView setTopLeftButton(int iconResId) {
        navigationBarView.setleftImageResource(iconResId);
        navigationBarView.setLetfIocnOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return navigationBarView;
    }


    public interface OnTopBarClickListener {
        void onClick();
    }

    protected abstract int getLayoutId();

    protected abstract void afterCreate(Bundle savedInstanceState);

    private void initDialog() {
        progressDialog = new ShowDialogUtils(this, R.style.dialog_transparent_style);
    }

    /**
     * 显示加载中的布局
     */
    public void showLoadingView() {
        networkStateView.showLoading();
    }

    /**
     * 显示加载完成后的布局(即子类Activity的布局)
     */
    public void showContentView() {
        networkStateView.showSuccess();
    }

    /**
     * 显示没有网络的布局
     */
    public void showNoNetworkView() {
        networkStateView.showNoNetwork();
        networkStateView.setOnRefreshListener(this);
    }

    /**
     * 显示没有数据的布局
     */
    public void showEmptyView(String text,int resId) {
        networkStateView.showEmpty(resId,text);
        networkStateView.setOnRefreshListener(this);
    }

    /**
     * 显示没有数据的布局
     */
    public void showEmptyView(int resId) {
        networkStateView.showEmpty(resId,"");
        networkStateView.setOnRefreshListener(this);
    }


    /**
     * 显示没有数据的布局
     */
    public void showEmptyView(int resId,String text) {
        networkStateView.showEmpty(resId,text);
        networkStateView.setOnRefreshListener(this);
    }


    /**
     * 显示没有数据的布局
     */
    public void showEmptyView() {
        networkStateView.showEmpty();
        networkStateView.setOnRefreshListener(this);
    }

    /**
     * 显示数据错误，网络错误等布局
     */
    public void showErrorView() {
        networkStateView.showError();
        networkStateView.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        onNetworkViewRefresh();
    }

    /**
     * 重新请求网络
     */
    public void onNetworkViewRefresh() {
    }

    /**
     * 显示加载的ProgressDialog
     */
    public void showProgressDialog() {
        progressDialog.showProgressDialog();
    }

    /**
     * 显示有加载文字ProgressDialog，文字显示在ProgressDialog的下面
     *
     * @param text 需要显示的文字
     */
    public void showProgressDialogWithText(String text) {
        progressDialog.showProgressDialogWithText(text);
    }

    /**
     * 显示加载成功的ProgressDialog，文字显示在ProgressDialog的下面
     *
     * @param message 加载成功需要显示的文字
     * @param time    需要显示的时间长度(以毫秒为单位)
     */
    public void showProgressSuccess(String message, long time) {
        progressDialog.showProgressSuccess(message, time);
    }

    /**
     * 显示加载成功的ProgressDialog，文字显示在ProgressDialog的下面
     * ProgressDialog默认消失时间为1秒(1000毫秒)
     *
     * @param message 加载成功需要显示的文字
     */
    public void showProgressSuccess(String message) {
        progressDialog.showProgressSuccess(message);
    }

    /**
     * 显示加载失败的ProgressDialog，文字显示在ProgressDialog的下面
     *
     * @param message 加载失败需要显示的文字
     * @param time    需要显示的时间长度(以毫秒为单位)
     */
    public void showProgressFail(String message, long time) {
        progressDialog.showProgressFail(message, time);
    }

    /**
     * 显示加载失败的ProgressDialog，文字显示在ProgressDialog的下面
     * ProgressDialog默认消失时间为1秒(1000毫秒)
     *
     * @param message 加载成功需要显示的文字
     */
    public void showProgressFail(String message) {
        progressDialog.showProgressFail(message);
    }

    /**
     * 隐藏加载的ProgressDialog
     */
    public void dismissProgressDialog() {
        progressDialog.dismissProgressDialog();
    }


    /**
     * 申请权限
     *
     * @param permissions 需要申请的权限(数组)
     * @param listener    权限回调接口
     */
    public  void requestPermissions(String[] permissions, PermissionListener listener) {
        Activity topactivity  = ActivityUtils.getTopActivity();
        if (null == topactivity) {
            return;
        }
        mPermissionListener = listener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            //权限没有授权
            if (ContextCompat.checkSelfPermission(topactivity.getApplication(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(topactivity, permissionList.toArray(new String[permissionList.size()]), CODE_REQUEST_PERMISSION);
        } else {
            mPermissionListener.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODE_REQUEST_PERMISSION:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int result = grantResults[i];
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            String permission = permissions[i];
                            deniedPermissions.add(permission);
                        }
                    }

                    if (deniedPermissions.isEmpty()) {
                        mPermissionListener.onGranted();
                    } else {
                        mPermissionListener.onDenied(deniedPermissions);
                    }
                }
                break;

            default:
                break;
        }
    }


    /**
     * 判断请求权限是否成功
     * @param grantResults
     * @return
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除view绑定
        if (unbinder != null) {
            unbinder.unbind();
        }
        //progressDialog.destoryProgressDialog();
        ActivityUtils.removeActivity(this);


    }

    public void openActivity(Class<?> pClass, Bundle bundle) {
        Intent intent = new Intent(this, pClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void openActivity(Class<?> pClass) {
        Intent intent = new Intent(this, pClass);
        startActivity(intent);
    }

    /*跳转到登录页面  登录成功回调到刚刚页面*/
    public void loginToServer(Class<?> c, Activity resultActivcity) {
        Intent loginIntent = new Intent(this, resultActivcity.getClass());
        loginIntent.putExtra("", c);
        startActivity(loginIntent);
        finish();
    }

    public void jumpToActivity(Intent intent) {
        startActivity(intent);
    }

    public void jumpToActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    public void jumpToActivityAndClearTask(Class activity) {
        Intent intent = new Intent(this, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void jumpToActivityAndClearTop(Class activity) {
        Intent intent = new Intent(this, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    //添加fragment
    protected void addFragment(ABaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    //移除fragment
    protected void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    public abstract int getFragmentContentId();

    public void toastCenter(String text) {
        Toast toast = Toast.makeText(getApplicationContext(),
                text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
