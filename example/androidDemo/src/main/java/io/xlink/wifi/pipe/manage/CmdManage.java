package io.xlink.wifi.pipe.manage;

import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.MyApp;
import io.xlink.wifi.pipe.bean.CmdBean;
import io.xlink.wifi.pipe.util.SharedPreferencesUtil;
import io.xlink.wifi.pipe.util.XlinkUtils;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

/**
 * @author LiuXinYi
 * @Date 2015年7月20日 下午4:01:20
 * @Description [命令指令管理]
 * @version 1.0.0
 */
public class CmdManage {
    private static CmdManage instance;
    private DbHelper dbHelper;

    public static CmdManage getInstance() {
	if (instance == null) {
	    instance = new CmdManage();
	}
	return instance;
    }

    private static ConcurrentHashMap<String, CmdBean> cmdBeanMap = new ConcurrentHashMap<String, CmdBean>();

    private CmdManage() {
	dbHelper = new DbHelper(MyApp.getApp());
	loadProperties();
	initCmd();
    }

    /**
     * 从DB中加载数据
     */
    private void loadProperties() {

	SQLiteDatabase db = dbHelper.getReadableDatabase();
	Cursor rs = db.query(TABLE, new String[] { NAME, VALUE }, null, null,
		null, null, null);
	while (rs.moveToNext()) {
	    String name = rs.getString(rs.getColumnIndex(NAME));
	    String value = rs.getString(rs.getColumnIndex(VALUE));
	    CmdBean cmdBean = new CmdBean();
	    cmdBean.name = name;
	    cmdBean.strCmd = value;
	    cmdBean.setData(XlinkUtils.stringToByteArray(value));
	    cmdBeanMap.put(name, cmdBean);
	    listCmd.add(cmdBean);
	}

	rs.close();
	db.close();
    }

    private void initCmd() {
	boolean b = SharedPreferencesUtil.queryBooleanValue(Constant.IS_INIT);
	if (!b) {//内置4个指令
	    CmdBean c1 = new CmdBean();
	    c1.name = "开";
	    c1.strCmd = "01";
	    c1.setData(XlinkUtils.stringToByteArray(c1.strCmd));
	    CmdBean c2 = new CmdBean();
	    c2.name = "关";
	    c2.strCmd = "02";
	    c2.setData(XlinkUtils.stringToByteArray(c2.strCmd));
	    CmdBean c3 = new CmdBean();
	    c3.name = "加";
	    c3.strCmd = "03"; 
	    c3.setData(XlinkUtils.stringToByteArray(c3.strCmd));
	    CmdBean c4 = new CmdBean();
	    c4.name = "减";
	    c4.strCmd = "04";
	    c4.setData(XlinkUtils.stringToByteArray(c4.strCmd));
	    addCmd(c1);
	    addCmd(c2);
	    addCmd(c3);
	    addCmd(c4);
	    SharedPreferencesUtil.keepShared(Constant.IS_INIT, true);
	}

    }

    private static final ArrayList<CmdBean> listCmd = new ArrayList<CmdBean>();

    public synchronized ArrayList<CmdBean> getCmds() {
	return listCmd;
    }

    public CmdBean getCmd(String name) {
	return cmdBeanMap.get(name);
    }

    public void addCmd(CmdBean cmdBean) {
	if (cmdBeanMap.get(cmdBean) != null) {
	    update(cmdBean.name, cmdBean.strCmd);
	} else {
	    insert(cmdBean.name, cmdBean.strCmd);
	}
	listCmd.add(cmdBean);
	cmdBeanMap.put(cmdBean.name, cmdBean);
    }

    public void deleteCmd(String name) {
	listCmd.remove(cmdBeanMap.get(name));
	cmdBeanMap.remove(name);
	delete(name);
    }

    // public void updateCmd(CmdBean cmdBean) {
    // cmdBeanMap.put(cmdBean.name, cmdBean);
    // update(cmdBean.name, cmdBean.strCmd);
    // }

    private void insert(String name, String value) {
	try {
	    ContentValues values = new ContentValues();
	    values.put(NAME, name);
	    values.put(VALUE, value);

	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    db.insert(TABLE, null, values);
	    db.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private void delete(String name) {
	try {
	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    db.delete(TABLE, "" + NAME + "=?", new String[] { name });
	    db.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void update(String name, String value) {
	try {
	    ContentValues values = new ContentValues();
	    values.put(VALUE, value);

	    SQLiteDatabase db = dbHelper.getWritableDatabase();
	    db.update(TABLE, values, "" + NAME + "=?", new String[] { name });

	    db.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String TABLE = "cmd";

    public class DbHelper extends SQLiteOpenHelper {
	private final static String TAG = "SQLiteHelper";

	private static final int VERSION = 1;

	private static final String name = "cmd.db";

	/**
	 * 采用默认的doda.db
	 * 
	 * @param context
	 */
	public DbHelper(Context context) {

	    this(context, name, VERSION);
	}

	public DbHelper(Context context, String name) {
	    this(context, name, VERSION);
	}

	public DbHelper(Context context, String name, int version) {
	    this(context, name, null, version);
	}

	public DbHelper(Context context, String name, CursorFactory factory,
		int version) {
	    super(context, name, factory, version);
	    // TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	    db.execSQL("CREATE TABLE " + TABLE + "(" + NAME
		    + " varchar(100) primary key, " + VALUE + " TEXT)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int paramInt1, int paramInt2) {
	    // TODO Auto-generated method stub
	    Log.d(TAG, "properties's db onUpgrade!");
	}
    }
}
