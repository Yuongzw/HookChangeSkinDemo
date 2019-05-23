package com.yuong.hookchangeskindemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    protected static String[] skins = new String[]{"skin.apk", "skin2.apk"};

    protected static String mCurrentSkin = null;

    private SkinFactory mSkinFactory;
    private Button btn_change;

    // 要申请的权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSkinFactory = new SkinFactory();
        mSkinFactory.setDelegate(getDelegate());
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        Log.d("layoutInflaterTag", layoutInflater.toString());
        layoutInflater.setFactory2(mSkinFactory);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                startRequestPermission();
            }
        }
        btn_change = findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSkin(getPath());
            }
        });
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }


    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        startRequestPermission();
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    /**
     * 等控件创建完成并且可交互之后，再换肤
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("changeTag", null == mCurrentSkin ? "currentSkin是空" : mCurrentSkin);
        if (null != mCurrentSkin) {
            changeSkin(mCurrentSkin); // 换肤操作必须在setContentView之后
        }
    }


    /**
     * 做一个切换方法
     *
     * @return
     */
    protected String getPath() {
        String path;
        if (null == mCurrentSkin) {
            path = skins[0];
        } else if (skins[0].equals(mCurrentSkin)) {
            path = skins[1];
        } else if (skins[1].equals(mCurrentSkin)) {
            path = skins[0];
        } else {
            return "unknown skin";
        }
        return path;
    }

    public void changeSkin(String path) {
        File skinFile = new File(Environment.getExternalStorageDirectory(), path);
        Log.e("changeSkin", skinFile.getAbsolutePath());
        SkinEngine.getInstance().load(skinFile.getAbsolutePath());
        mSkinFactory.changeSkin();
        mCurrentSkin = path;
    }
}
