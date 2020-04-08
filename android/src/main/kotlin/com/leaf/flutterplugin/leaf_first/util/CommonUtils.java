package com.leaf.flutterplugin.leaf_first.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import java.io.File;
import java.security.MessageDigest;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class CommonUtils {

    private static final String TAG = "CommonUtils";

    public static void goTaobao(Context context, String sid) {
        String url = "https://item.taobao.com/item.htm?id=" + sid;
        if (isPackageInstalled(context, "com.taobao.taobao")) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            context.startActivity(intent);
        } else {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }

    public static void copyToClipboard(Context context, String text) {
        if (TextUtils.isEmpty(text)) {
            showToast(context, "没有可复制内容");
            return;
        }
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(String.valueOf(System.currentTimeMillis()), text.subSequence(0, text.length()));
        clipboard.setPrimaryClip(clip);
        showToast(context, "成功复制到剪切板");
    }

    public static void openInBrowser(Context context, final String url) {
        if (TextUtils.isEmpty(url) || !url.contains("http"))
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    public static boolean isWifi2_4G(Context context) {
        WifiManager wifi;
        WifiInfo info;
        wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        info = wifi.getConnectionInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int frequency = info.getFrequency();
            return frequency <= 3000;
        } else {
            return true;
        }
    }

    public static void goCommentApp(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean isApkInDebug(Context context) {
        if (null == context)
            return false;
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setStatusBarTextColorDark(final Activity pActivity, final boolean isLight) {
        // Fetch the current flags.
        final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
        pActivity.getWindow().getDecorView().setSystemUiVisibility(isLight ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }

    private static final String App_Version_K = "appVersionK";
    private static final String Show_Plant_Tip_K = "showPlantTipK";

    public static boolean getIsFirstRun(Context context) {
        return !CommonUtils.getAppVersionName(context).equals(SharePrefUtil.getString(context, App_Version_K, ""));
    }

    public static void saveVersion(Context context) {
        SharePrefUtil.saveString(context, App_Version_K, CommonUtils.getAppVersionName(context));
    }

    public static void savePlantTip(Context context) {
        SharePrefUtil.saveString(context, App_Version_K, Show_Plant_Tip_K);
    }


    public static String getManifestAppMeta(Context context, @NonNull String key) {

        if (TextUtils.isEmpty(key)) return null;
        String value = null;

        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(key);

            if (TextUtils.isEmpty(value)) return null;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String addParamsToConfigUrl(@NonNull String configUrl, @NonNull String... params) {
        String requestUrl;
        String[] items = configUrl.split("\\?");
        requestUrl = items[0] + "?";

        int paramCount = params.length;
        for (int i = 0; i < paramCount; i++) {
            requestUrl += items[i + 1] + params[i];
        }

        if (paramCount + 1 < items.length) {
            requestUrl += items[paramCount + 1];
        }

        if (true) Log.e(TAG, "req_url:" + requestUrl);

        return requestUrl;
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    public static void shareTextViaSystem(Context context, String shareText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享到：");
        intent.putExtra(Intent.EXTRA_TEXT, "" + shareText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent.createChooser(intent, "分享到");

        context.startActivity(intent);
    }

    public static void sharePictureViaSystem(Context context, String picLocalPath, String shareText) {

        Uri imageUri = Uri.fromFile(new File(picLocalPath));

        Log.d("share", "uri:" + imageUri);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享到：");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "" + shareText);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    public static boolean isSDKVersionNotBelow(final int num) {
        return Build.VERSION.SDK_INT >= num;
    }

    public static boolean isExsitMianActivity(Context context, Class cls) {

        Intent intent = new Intent(context, cls);
        ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) {
                    // 说明它已经启动了
                    flag = true;
                    break;  //跳出循环，优化效率
                }
            }
        }
        return flag;
    }


    public static boolean isAppAlive(Context context) {
        boolean isForeground = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isForeground = true;
                        }
                    }
                }
            }
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                List<ActivityManager.AppTask> taskInfo = am.getAppTasks();
                if (taskInfo.get(0).getTaskInfo().baseActivity.equals(context.getPackageName())) {
                    isForeground = true;
                }
            } else {
                List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);
                if (infos.get(0).topActivity.getPackageName().equals(context.getPackageName())) {
                    isForeground = true;
                }
            }
        }
        return isForeground;
    }


    public static String packageName(Context context)
    {
        String name = context.getPackageName();
        return name == null ? "com.leaf": name;
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "CommonUtils", e);
        }
        return versionName;
    }

    public static int getAppVersionCode(Context context) {

        int versionCode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;

        } catch (Exception e) {
            Log.e("VersionInfo", "CommonUtils", e);
        }
        return versionCode;
    }


    public static void shareImage(final Context context, File file) {
        if(file == null) return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getImageContentUri(context, file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, "分享"));
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    /**
     * 打开设置网页界面
     */
    public static void openSettingNet(Context context) {
        Intent intent = null;
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if (Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        context.startActivity(intent);
    }

    public static void openImage(Context context, String path) {
        if (path != null && path.length() > 0 && context != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
            context.startActivity(intent);
        }
    }


    /**
     * 将指定byte数组转换成16进制字符串
     *
     * @param b
     * @return
     */
    public static String byteToHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    /**
     * 判断当前是否有可用的网络以及网络类型 0：无网络 1：WIFI 2：CMWAP 3：CMNET
     *
     * @param context
     * @return
     */
    public static int isNetworkAvailables(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return 0;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        NetworkInfo netWorkInfo = info[i];
                        if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            return 1;
                        } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            String extraInfo = netWorkInfo.getExtraInfo();
                            if ("cmwap".equalsIgnoreCase(extraInfo)
                                    || "cmwap:gsm".equalsIgnoreCase(extraInfo)) {
                                return 2;
                            }
                            return 3;
                        }
                    }
                }
            }
        }
        return 0;
    }

    private static Drawable createDrawable(Drawable d, Paint p) {

        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap b = bd.getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(bd.getIntrinsicWidth(),
                bd.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(b, 0, 0, p); // 关键代码，使用新的Paint画原图，

        return new BitmapDrawable(bitmap);
    }

    /**
     * 设置Selector。 本次只增加点击变暗的效果，注释的代码为更多的效果
     */
    public static StateListDrawable createSLD(Context context, Drawable drawable) {
        StateListDrawable bg = new StateListDrawable();
        int brightness = 50 - 127;
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0,
                brightness,// 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        Drawable normal = drawable;
        Drawable pressed = createDrawable(drawable, paint);
        bg.addState(new int[]{android.R.attr.state_pressed,}, pressed);
        bg.addState(new int[]{android.R.attr.state_focused,}, pressed);
        bg.addState(new int[]{android.R.attr.state_selected}, pressed);
        bg.addState(new int[]{}, normal);
        return bg;
    }

    /**
     * 更新刷新时间
     *
     * @param created
     * @return
     */
    public static String getUploadtime(long created) {
        StringBuffer when = new StringBuffer();
        int difference_seconds;
        int difference_minutes;
        int difference_hours;
        int difference_days;
        int difference_months;
        long curTime = System.currentTimeMillis();
        difference_months = (int) (((curTime / 2592000) % 12) - ((created / 2592000) % 12));
        if (difference_months > 0) {
            when.append(difference_months + "月");
        }

        difference_days = (int) (((curTime / 86400) % 30) - ((created / 86400) % 30));
        if (difference_days > 0) {
            when.append(difference_days + "天");
        }

        difference_hours = (int) (((curTime / 3600) % 24) - ((created / 3600) % 24));
        if (difference_hours > 0) {
            when.append(difference_hours + "小时");
        }

        difference_minutes = (int) (((curTime / 60) % 60) - ((created / 60) % 60));
        if (difference_minutes > 0) {
            when.append(difference_minutes + "分钟");
        }

        difference_seconds = (int) ((curTime % 60) - (created % 60));
        if (difference_seconds > 0) {
            when.append(difference_seconds + "秒");
        }

        return when.append("前").toString();
    }

    /**
     * 如果软键盘处于弹出状态，将其隐藏
     *
     * @param activity
     */
    public static void hiddenInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 如果软键盘处于不可见状态，让其弹出
     *  @param activity
     * @param view
     */
    public static void showInput(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
            if (activity.getCurrentFocus() != null)
                imm.showSoftInput(view, 0);
        }
    }


    /**
     * 检查当前设备是否有网络连接
     * 无论是哪种形式的连接（3G，Wifi等等），只要有连接就算有网络连接
     *
     * @param context
     * @return
     */

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    /**
     * 当前是否处于Wifi下使用
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            return info.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    /**
     * 是否使用3G网络
     *
     * @param context
     * @return
     */
    public static boolean isMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            return info.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    /**
     * 获得描述当前设备网络状况的NetworkInfo对象
     *
     * @param context
     * @return
     */
    private static NetworkInfo getNetworkInfo(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean checkSdCard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取设备屏幕的宽度
     *
     * @param act
     * @return
     */
    public static int getScreenWidth(Activity act) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取设备屏幕的高度
     *
     * @param act
     * @return
     */
    public static int getScreenHeight(Activity act) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 将目标字符串使用MD5算法加密
     *
     * @param str 目标字符串
     * @return 使用MD5加密后的字符串
     */
    public static String getMD5String(String str) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] bytes = str.getBytes();
            md.update(bytes);
            byte[] md5bytes = md.digest();
            StringBuffer sb = new StringBuffer();
            String temp = null;
            for (int i = 0; i < md5bytes.length; i++) {
                temp = Integer.toHexString(md5bytes[i] & 0xFF);
                if (temp.length() == 1) {
                    sb.append("0");
                } else {
                    sb.append(temp);
                }
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 专门用于提示网络无连接的提示 .
     * @param context
     */
    public static void toashNetWorkTip(Context context) {
        Toast.makeText(context, "网络暂不可用", Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    public static void toastLogin(Context context){
        Toast.makeText(context,"请登录",Toast.LENGTH_SHORT).show();
    }
}
