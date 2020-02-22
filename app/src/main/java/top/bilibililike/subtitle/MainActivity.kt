package top.bilibililike.subtitle

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import java.net.URISyntaxException
import butterknife.ButterKnife
import top.bilibililike.subtitle.roomInfo.RepoBean
import top.bilibililike.subtitle.roomInfo.RoomInfoAdapter
import top.bilibililike.subtitle.roomInfo.RoomRepo
import top.bilibililike.subtitle.subtitle.SubtitleService
import top.bilibililike.subtitle.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_main.*;

class MainActivity : AppCompatActivity(), RoomInfoAdapter.ClickCallback {

    private var subtitleService: SubtitleService? = null
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            subtitleService = (service as SubtitleService.LocalBinder).service
            Log.d(TAG, "suntitleService赋值了")
        }

        override fun onServiceDisconnected(arg0: ComponentName) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        if (!commonRomPermissionCheck()) {
            requestAlertWindowPermission()
        }
        val layoutManager = GridLayoutManager(this, 2)
        val adapter = RoomInfoAdapter(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        val intent = Intent(this, SubtitleService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        RoomRepo.getLivers(object : RoomRepo.LiverCallback {
            override fun onSuccess(liverList: List<RepoBean.DataBean>) {
                adapter.refreshData(liverList)
                Log.d(TAG, liverList.size.toString() + "")
            }

            override fun onStartLoading() {
                //todo show dialog
                Log.d(TAG, "startLoading")
            }

            override fun onError(reason: String) {
                ToastUtil.show(reason)
                Log.d(TAG, reason)
            }
        })

        navigationView?.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.nav_feedback) {
                joinQQGroup("4xWy75NR3Ui5tIIfNwhA6hF-lrMTl1Zv")
            }
            true
        }
    }


    override fun onClicked(roomId: String) {
        subtitleService?.linkStart(roomId)
        var intent: Intent? = null
        try {
            intent = Intent.parseUri("bilibili://live/$roomId", Intent.URI_INTENT_SCHEME)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "应用未安装", Toast.LENGTH_SHORT).show()
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val uri = Uri.parse("market://details?id=" + "tv.danmaku.bili")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(goToMarket)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        unbindService(mConnection)
    }


    //判断权限
    private fun commonRomPermissionCheck(): Boolean {
        var result = true
        try {
            val clazz = Settings::class.java
            val canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context::class.java)
            result = canDrawOverlays.invoke(null, this) as Boolean
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
        }

        return result
    }

    //申请权限
    private fun requestAlertWindowPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, REQUEST_CODE)
    }

    //处理回调
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                ToastUtil.show("请通过悬浮窗权限申请！")
                requestAlertWindowPermission()
            }
        }
    }

    fun joinQQGroup(key: String) {
        val intent = Intent()
        intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
        try {
            startActivity(intent)
        } catch (e: Exception) {
            ToastUtil.show("您还没有安装QQ，请先安装软件")
        }

    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START)
        } else {
            super.onBackPressed()
        }

    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val REQUEST_CODE = 1
    }
}
