package top.bilibililike.subtitle;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.bilibililike.subtitle.roomInfo.RepoBean;
import top.bilibililike.subtitle.roomInfo.RoomInfoAdapter;
import top.bilibililike.subtitle.roomInfo.RoomRepo;
import top.bilibililike.subtitle.subtitle.SubtitleService;
import top.bilibililike.subtitle.utils.ToastUtil;


public class MainActivity extends AppCompatActivity implements RoomInfoAdapter.ClickCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    private SubtitleService subtitleService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            subtitleService = ((SubtitleService.LocalBinder) service).getService();
            Log.d(TAG, "suntitleService赋值了");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (!commonRomPermissionCheck()) {
            requestAlertWindowPermission();
        }
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        RoomInfoAdapter adapter = new RoomInfoAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        Intent intent = new Intent(this, SubtitleService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        RoomRepo.getLivers(new RoomRepo.LiverCallback() {
            @Override
            public void onSuccess(List<RepoBean.DataBean> liverList) {
                adapter.refreshData(liverList);
                Log.d(TAG, liverList.size() + "");
            }

            @Override
            public void onStartLoading() {
                //todo show dialog
                Log.d(TAG, "startLoading");
            }

            @Override
            public void onError(String reason) {
                ToastUtil.show(reason);
                Log.d(TAG, reason);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_feedback){
                    joinQQGroup("4xWy75NR3Ui5tIIfNwhA6hF-lrMTl1Zv");
                }
                return true;
            }
        });
    }


    @Override
    public void onClicked(String roomId) {
        subtitleService.linkStart(roomId);
        Intent intent = null;
        try {
            intent = Intent.parseUri("bilibili://live/" + roomId, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "应用未安装", Toast.LENGTH_SHORT).show();
        }
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("market://details?id=" + "tv.danmaku.bili");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(goToMarket);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }


    //判断权限
    private boolean commonRomPermissionCheck() {
        Boolean result = true;
        try {
            Class clazz = Settings.class;
            Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
            result = (Boolean) canDrawOverlays.invoke(null, this);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return result;
    }

    //申请权限
    private void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    //处理回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                ToastUtil.show("请通过悬浮窗权限申请！");
                requestAlertWindowPermission();
            }
        }
    }

    public void joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        try {
            startActivity(intent);
        } catch (Exception e) {
            ToastUtil.show("您还没有安装QQ，请先安装软件");
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)){
            drawerLayout.closeDrawer(Gravity.END);
        }else {
            super.onBackPressed();
        }

    }
}
