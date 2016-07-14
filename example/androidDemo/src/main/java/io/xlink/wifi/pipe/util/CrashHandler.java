package io.xlink.wifi.pipe.util;

import io.xlink.wifi.pipe.MyApp;
import io.xlink.wifi.sdk.XlinkAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

public class CrashHandler implements UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler handler;

    private static CrashHandler crashHandler;

    public static CrashHandler init(Context context) {
	if (crashHandler == null) {
	    crashHandler = new CrashHandler(context);
	}
	return crashHandler;
    }

    private CrashHandler(Context context) {

	handler = Thread.getDefaultUncaughtExceptionHandler();

	Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
	SimpleDateFormat simpledateformat = new SimpleDateFormat(
		"dd-MM-yyyy hh:mm:ss");

	// 日期、app版本信息
	StringBuilder buff = new StringBuilder();
	buff.append("Date: ").append(simpledateformat.format(new Date()))
		.append("\n");
	buff.append("===========\n");
	// 崩溃的堆栈信息
	buff.append("Stacktrace:\n\n");
	StringWriter stringwriter = new StringWriter();
	PrintWriter printwriter = new PrintWriter(stringwriter);
	throwable.printStackTrace(printwriter);
	buff.append(stringwriter.toString());
	buff.append("===========\n");
	printwriter.close();

	write2ErrorLog(buff.toString());
	if (handler != null) {
	    // 交给还给系统处理
	    handler.uncaughtException(thread, throwable);
	}
	XlinkAgent.getInstance().stop();
    }

    // 到SD卡目录
    private final static String DISK_CACHE_PATH = "/"
	    + MyApp.getApp().getPackageName() + "/";

    /**
     * 创建总文件夹
     */
    public String getFilePath() {
	String cachePath;
	// /mnt/sdcard判断有无SD卡
	if (Environment.getExternalStorageState().equals(
		Environment.MEDIA_MOUNTED)) {
	    cachePath = Environment.getExternalStorageDirectory()
		    + DISK_CACHE_PATH;
	} else {
	    // 没有就创建到手机内存
	    cachePath = MyApp.getApp().getCacheDir() + DISK_CACHE_PATH;
	}
	File file = new File(cachePath);
	if (!file.exists()) {
	    // 创建文件夹
	    file.mkdirs();
	}
	return cachePath;
    }

    private void write2ErrorLog(String content) {
	File file = new File(getFilePath() + "/crash.txt");
	FileOutputStream fos = null;
	try {
	    if (file.exists()) {
		// 清空之前的记录
		file.delete();
	    } else {
		file.getParentFile().mkdirs();
	    }
	    file.createNewFile();
	    fos = new FileOutputStream(file);
	    fos.write(content.getBytes());
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (fos != null) {
		    fos.close();
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
}
