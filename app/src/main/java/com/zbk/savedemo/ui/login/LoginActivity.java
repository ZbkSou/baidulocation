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
             * ??????????????????????????????????????????????????????????????????????????????
             */
            // ??????????????????
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
        // ????????????????????????????????????,?????????????????????,??????????????????
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
//     * ?????????????????????
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
//     * ?????????????????????????????????
//     *
//     * @param request
//     */
//    @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
//    void showRationaleForMap(PermissionRequest request) {
//        showRationaleDialog("??????????????????????????????????????????", request);
//    }
//
//    /**
//     * ???????????????????????????????????????
//     *
//     * @param messageResId
//     * @param request
//     */
//    private void showRationaleDialog(String messageResId, final PermissionRequest request) {
//        new AlertDialog.Builder(this)
//          .setPositiveButton("??????", new DialogInterface.OnClickListener() {
//              @Override
//              public void onClick(@NonNull DialogInterface dialog, int which) {
//                  request.proceed();//????????????
//              }
//          })
//          .setNegativeButton("??????", new DialogInterface.OnClickListener() {
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
//     * ????????????????????????
//     */
//    @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
//    void onMapDenied() {
//        Toast.makeText(this, "???????????????????????????????????????", Toast.LENGTH_LONG).show();
//    }
//    /**
//     * ?????????????????????????????????????????????
//     */
//    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
//    void onMapNeverAskAgain() {
//        AskForPermission();
//    }
//    /**
//     * ???????????????????????????,?????????????????????????????????????????????
//     */
//    private void AskForPermission() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("??????????????????????????????,????????????????????????\n???????????????????????????????????????????????????");
//        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                return;
//            }
//        });
//        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                intent.setData(Uri.parse("package:" + LoginActivity.this.getPackageName())); // ???????????????????????????????????????
//                startActivity(intent);
//            }
//        });
//        builder.create().show();
//    }
//
//    /**
//     *6????????????????????????PermissionsDispatcher???????????????
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        LoginActivityPermissionsDispatcher.onRequestPermissionsResult(LoginActivity.this, requestCode, grantResults);
//    }
}