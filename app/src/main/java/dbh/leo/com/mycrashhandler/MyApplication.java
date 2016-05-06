package dbh.leo.com.mycrashhandler;

import android.app.Application;

/**
 * Created by leo on 2016/5/6.
 */
public class MyApplication extends Application {
    private static MyApplication sInstance;

    public static MyApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //注册MyCrashHandler
        MyCrashHandler myCrashHandler = MyCrashHandler.getInstance();
        myCrashHandler.init(this);
    }
}
