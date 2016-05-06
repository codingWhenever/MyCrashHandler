package dbh.leo.com.mycrashhandler;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by leo on 2016/5/6.
 */
public class MyCrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "MyCrashHandler";
    private static final boolean DEBUG = true;

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/MyCrash/log/";
    private static final String FILE_NAME = "crash";//文件名
    private static final String FILE_NAME_SUFFIX = ".log";//文件名后缀

    private static MyCrashHandler mInstance = new MyCrashHandler();

    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

    private Context mContext;

    private MyCrashHandler() {

    }

    public static MyCrashHandler getInstance() {
        return mInstance;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        try {
            //写入sdcard
            writeExceptionToSDCard(ex);

        } catch (IOException e) {
            e.printStackTrace();
        }
        ex.printStackTrace();

        if (mUncaughtExceptionHandler != null) {
            mUncaughtExceptionHandler.uncaughtException(thread, ex);

        } else {
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * @param context
     */
    public void init(Context context) {
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }


    /**
     * 将异常信息写入sdcard
     * @param ex
     * @throws IOException
     */
    private void writeExceptionToSDCard(Throwable ex) throws IOException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.w(TAG, "未找到SD卡");
            }
            return;
        }
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        long now = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(now));
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //先记录当前时间
            pw.println(time);
            //记录当前机型相关信息
            writePhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);

            pw.close();

        } catch (Exception e) {
            Log.e(TAG, "记录崩溃信息出错");
        }

    }

    /**
     * 记录当前手机相关信息
     *
     * @param pw
     */
    private void writePhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        //当前app的版本名称和版本号
        pw.print("App Version: " + packageInfo.versionName + "-" + packageInfo.versionCode);
        pw.println();
        //手机系统
        pw.print("OS Version: " + Build.VERSION.RELEASE + "-" + Build.VERSION.SDK_INT);
        pw.println();
        //手机厂商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);
        //手机型号
        pw.println("Model: " + Build.MODEL);
        //CPU
        pw.println("CPU_ABI: " + Build.CPU_ABI);
        //网络类型
        pw.println("networkType: " + Utils.GetNetworkType(mContext));

    }
}
