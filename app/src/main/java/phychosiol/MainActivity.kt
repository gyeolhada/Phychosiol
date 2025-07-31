package com.example.phychosiolz

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.phychosiolz.databinding.ActivityMainBinding
import com.example.phychosiolz.service.UserService
import com.example.phychosiolz.utils.ChaquopyUtil
import com.example.phychosiolz.utils.IOUtil
import com.example.phychosiolz.utils.PermissionUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var userService = MutableLiveData<UserService?>()

    private val connectionUser = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as UserService.UserBinder
            userService.postValue(binder.getService())
            Log.i("UserService", "onServiceConnected${binder.getService()}")
            binder.getService().dCallback = object : UserService.DepressionCallback {
                override fun onDepression() {
                    userDepressionAttack()
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        test()
        PermissionUtil.setUp(this)
        if (!PermissionUtil.checkPermission()) {
            Toast.makeText(this, "请允许APP访问所有文件，否则无法使用。", Toast.LENGTH_SHORT).show();
            this.finish()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 设置状态栏字体颜色为黑色
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val navView: BottomNavigationView = binding.bottomNavigation

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_observe -> {
                    try {
                        navController.popBackStack(R.id.observeFragment, false)
                        navController.navigate(R.id.observeFragment)
                    } catch (e: Exception) {
                        Log.i("nav", e.toString())
                    }
                    true
                }

                R.id.navigation_emotion -> {
                    try {
                        navController.popBackStack(R.id.observeFragment, false)
                        navController.navigate(R.id.emotionFragment)
                    } catch (e: Exception) {
                        Log.i("nav", e.toString())
                    }
                    true
                }

//                R.id.navigation_warning -> {
//                    try {
//                        navController.popBackStack(R.id.observeFragment, false)
//                        navController.navigate(R.id.warningFragment)
//                    } catch (e: Exception) {
//                        Log.i("nav", e.toString())
//                    }
//                    true
//                }

                R.id.navigation_mine -> {
                    try {
                        navController.popBackStack(R.id.observeFragment, false)
                        navController.navigate(R.id.mineFragment)
                    } catch (e: Exception) {
                        Log.i("nav", e.toString())
                    }
                    true
                }

                else -> {
                    false
                }
            }
        }

        // Hide the bottom
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.observeFragment, R.id.emotionFragment,
//                R.id.warningFragment,
                R.id.mineFragment -> {
                    navView.visibility = View.VISIBLE
                    navView.selectedItemId = destination.id
                    when (destination.id) {
                        R.id.mineFragment -> {
                            navView.menu.findItem(R.id.navigation_mine).isChecked = true
                        }

//                        R.id.warningFragment -> {
//                            navView.menu.findItem(R.id.navigation_warning).isChecked = true
//                        }

                        R.id.emotionFragment -> {
                            navView.menu.findItem(R.id.navigation_emotion).isChecked = true
                        }

                        R.id.observeFragment -> {
                            navView.menu.findItem(R.id.navigation_observe).isChecked = true
                        }
                    }
                }

                else -> {
                    navView.visibility = View.GONE
                }
            }
        }
    }

    /***
     * 测试
     */
    private var testTimes = 100
    private fun testC() {
        if (testTimes-- == 0) {
            Log.i("test", "time=${System.currentTimeMillis() - MyApplication.instance.testStartTime}")
            return
        }
        ChaquopyUtil.runDepressionAttackRecognition(
            ::testC,
            IOUtil.getUserDir("2"),
            IOUtil.getDepressionAttackFineTuneModelPath("2")
        )
    }

    private fun test() {
//        ChaquopyUtil.setup(this)
//        MyApplication.instance.testStartTime = System.currentTimeMillis()
//        testC()
//        ChaquopyUtil.setup(this)
//        ChaquopyUtil.runDepressionRecognitionMigration("/storage/emulated/0/PhychosiolZ/test/",
//            "/storage/emulated/0/PhychosiolZ/1/2024年1月23日/")
//        try {
//            ChaquopyUtil.runDepressionAttackRecognition(::testC, "/storage/emulated/0/PhychosiolZ/test/",
//                "/storage/emulated/0/PhychosiolZ/1/2024年1月22日/new_model.pth")
//        }catch (e:Exception){
//            e.printStackTrace()
//        }
//        IOUtil.catFineTuneData("1")
    }

    fun getUserService(): MutableLiveData<UserService?> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(this, "请允许APP访问所有文件，否则无法使用。", Toast.LENGTH_SHORT)
                    .show();
                startActivity(Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
            }
        }
        if (userService.value == null) {
            Log.i("UserService", "start service")
            Intent(this, UserService::class.java).also { intent ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else startService(intent)
                bindService(intent, connectionUser, Context.BIND_AUTO_CREATE)
            }
        }
        return userService
    }

    fun userLogout() {
        //停止服务
        if (userService.value != null) {
            userService.value!!.stopSelf()
            unbindService(connectionUser)
            userService.value = null
        }
    }

    fun userDepressionAttack() {
        //跳转到警告界面
        findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.warningFragment)
    }
}