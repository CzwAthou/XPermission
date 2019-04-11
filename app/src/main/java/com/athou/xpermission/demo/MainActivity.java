package com.athou.xpermission.demo;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.athou.xpermission.annotation.NeedPermission;
import com.athou.xpermission.annotation.NeedPermissions;
import com.athou.xpermission.annotation.PermissionDenied;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                testPermission1(MainActivity.this);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @NeedPermission(permissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
            })
            @Override
            public void onClick(View v) {
                testPermission2();
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testPermission3(MainActivity.this);
            }
        });
    }

    @NeedPermission(permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    })
    public void testPermission1(Activity activity) {
        Toast.makeText(this, "权限申请成功", Toast.LENGTH_SHORT).show();
    }

    public void testPermission2() {
        Toast.makeText(this, "权限申请成功", Toast.LENGTH_SHORT).show();
    }

    @NeedPermissions({
            @NeedPermission(permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}),
            @NeedPermission(permissions = Manifest.permission.READ_PHONE_STATE, necessary = false)
    })
    public void testPermission3(Activity activity) {
        Toast.makeText(activity, "权限申请成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 权限拒绝回调，如果不需要拒绝的回调，可以不写。<br>
     * 拒绝回调采用注解的方式，对方法名无要求，但是方法参数第一个必须说List<String>，否则收不到权限拒绝回调
     *
     * @param permissions
     */
    @PermissionDenied
    public void permissionDenied(List<String> permissions) {
        Toast.makeText(this, "testPermission，权限拒绝:" + permissions.toString(), Toast.LENGTH_SHORT).show();
    }
}
