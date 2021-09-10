package com.zbk.savedemo.ui.login;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zbk.savedemo.ForegroundLocationService;
import com.zbk.savedemo.MainActivity;
import com.zbk.savedemo.R;

import java.util.ArrayList;

//@RuntimePermissions
public class LoginActivity extends AppCompatActivity {
     EditText usernameEditText;

     Button loginButton;
    private final int SDK_PERMISSION_REQUEST = 127;

    private String permissionInfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);

        loginButton = findViewById(R.id.login);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                intent.putExtra("name", usernameEditText.getText() + "");
                startActivity(intent);
                finish();
            }
        });
        getPersimmions();
    }


    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }
        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


//    /**
//     * 申请权限成功时
//     */
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
//    void ApplySuccess() {
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("name", usernameEditText.getText()+"");
//        startActivity(intent);
//    }
//    /**
//     * 申请权限告诉用户原因时
//     *
//     * @param request
//     */
//    @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
//    void showRationaleForMap(PermissionRequest request) {
//        showRationaleDialog("使用此功能需要打开定位的权限", request);
//    }
//
//    /**
//     * 告知用户具体需要权限的原因
//     *
//     * @param messageResId
//     * @param request
//     */
//    private void showRationaleDialog(String messageResId, final PermissionRequest request) {
//        new AlertDialog.Builder(this)
//          .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//              @Override
//              public void onClick(@NonNull DialogInterface dialog, int which) {
//                  request.proceed();//请求权限
//              }
//          })
//          .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//              @Override
//              public void onClick(@NonNull DialogInterface dialog, int which) {
//                  request.cancel();
//              }
//          })
//          .setCancelable(false)
//          .setMessage(messageResId)
//          .show();
//    }
//
//    /**
//     * 申请权限被拒绝时
//     */
//    @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
//    void onMapDenied() {
//        Toast.makeText(this, "你拒绝了权限，该功能不可用", Toast.LENGTH_LONG).show();
//    }
//    /**
//     * 申请权限被拒绝并勾选不再提醒时
//     */
//    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
//    void onMapNeverAskAgain() {
//        AskForPermission();
//    }
//    /**
//     * 被拒绝并且不再提醒,提示用户去设置界面重新打开权限
//     */
//    private void AskForPermission() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("当前应用缺少定位权限,请去设置界面打开\n打开之后按两次返回键可回到该应用哦");
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                return;
//            }
//        });
//        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                intent.setData(Uri.parse("package:" + LoginActivity.this.getPackageName())); // 根据包名打开对应的设置界面
//                startActivity(intent);
//            }
//        });
//        builder.create().show();
//    }
//
//    /**
//     *6，权限回调，调用PermissionsDispatcher的回调方法
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        LoginActivityPermissionsDispatcher.onRequestPermissionsResult(LoginActivity.this, requestCode, grantResults);
//    }
}