package io.xlink.wifi.pipe.util;

import io.xlink.wifi.pipe.MyApp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * db存储实体类
 * 
 * @author Liuxy
 * @2015年2月25日下午3:58:51 </br>
 * @explain
 */
public class XTProperties implements Map<String, String> {

    private static final String LOG_TAG = "XTProperties";
    private static final String MAC = "mac";
    private static final String VALUE = "value";
    private static String TABLE = "properties";
    private DbHelper dbHelper;

    // private SQLiteDatabase db;

    private static class DodaPropertyHolder {
	private static final XTProperties instance = new XTProperties();
	static {
	    instance.init();
	}
    }

    private Map<String, String> properties;

    /**
     * Returns a singleton instance of XTProperties.
     * 
     * @return an instance of XTProperties.
     */
    public static XTProperties getInstance() {
	return DodaPropertyHolder.instance;
    }

    private XTProperties() {

    }

    public void init() {
	if (properties == null) {
	    properties = new ConcurrentHashMap<String, String>();
	} else {
	    properties.clear();
	}

	dbHelper = new DbHelper(MyApp.getApp());

	loadProperties();
    }

    public String getProperty(String name, String defaultValue) {
	String value = properties.get(name);
	if (value != null) {
	    return value;
	} else {
	    return defaultValue;
	}
    }

    public boolean getBooleanProperty(String name) {
	return Boolean.valueOf(get(name));
    }

    public boolean getBooleanProperty(String name, boolean defaultValue) {
	String value = get(name);
	if (value != null) {
	    return Boolean.valueOf(value);
	} else {
	    return defaultValue;
	}
    }

    /**
     * 从DB中加载数据
     */
    private void loadProperties() {

	SQLiteDatabase db = dbHelper.getReadableDatabase();
	Cursor rs = db.query(TABLE, new String[] { MAC, VALUE }, null, null,
		null, null, null);
	while (rs.moveToNext()) {
	    String name = rs.getString(rs.getColumnIndex(MAC));
	    String value = rs.getString(rs.getColumnIndex(VALUE));

	    properties.put(name, value);
	}

	rs.close();
	db.close();
    }

    private void insertProperty(String name, String value) {

	ContentValues values = new ContentValues();
	values.put(MAC, name);
	values.put(VALUE, value);

	SQLiteDatabase db = dbHelper.getWritableDatabase();
	db.insert(TABLE, null, values);

	db.close();

	Log.d(LOG_TAG, "property inserted: " + name);
    }

    private void deleteProperty(String name) {

	SQLiteDatabase db = dbHelper.getWritableDatabase();
	db.delete(TABLE, "" + MAC + "=?", new String[] { name });

	db.close();

	Log.d(LOG_TAG, "property deleted: " + name);
    }

    private void updateProperty(String name, String value) {

	ContentValues values = new ContentValues();
	values.put(VALUE, value);

	SQLiteDatabase db = dbHelper.getWritableDatabase();
	db.update(TABLE, values, "" + MAC + "=?", new String[] { name });

	db.close();

	Log.d(LOG_TAG, "property updated: " + name);
    }

    @Override
    public void clear() {

	throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
	return properties.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
	return properties.containsValue(value);
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
	return Collections.unmodifiableSet(properties.entrySet());
    }

    @Override
    public String get(Object key) {
	return properties.get(key);
    }

    public Map<String, String> getProperties() {
	return properties;
    }

    @Override
    public boolean isEmpty() {

	return properties.isEmpty();
    }

    @Override
    public Set<String> keySet() {
	return Collections.unmodifiableSet(properties.keySet());
    }

    @Override
    public String put(String key, String value) {
	if (value == null) {
	    // This is the same as deleting, so remove it.
	    return remove(key);
	}
	if (key == null) {
	    throw new NullPointerException("Key cannot be null. Key=" + key
		    + ", " + VALUE + "=" + value);
	}
	if (key.endsWith(".")) {
	    key = key.substring(0, key.length() - 1);
	}
	key = key.trim();
	String result;
	synchronized (this) {
	    if (properties.containsKey(key)) {
		if (!properties.get(key).equals(value)) {
		    updateProperty(key, value);
		}
	    } else {
		insertProperty(key, value);
	    }

	    result = properties.put(key, value);
	}

	// Generate event.
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(VALUE, value);

	return result;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> t) {
	for (Entry<? extends String, ? extends String> entry : t.entrySet()) {
	    put(entry.getKey(), entry.getValue());
	}
    }

    /**
     * Return all children property names of a parent property as a Collection
     * of String objects. For example, given the properties <tt>X.Y.A</tt>,
     * <tt>X.Y.B</tt>, and <tt>X.Y.C</tt>, then the child properties of
     * <tt>X.Y</tt> are <tt>X.Y.A</tt>, <tt>X.Y.B</tt>, and <tt>X.Y.C</tt>. The
     * method is not recursive; ie, it does not return children of children.
     * 
     * @param parentKey
     *            the name of the parent property.
     * @return all child property names for the given parent.
     */
    public Collection<String> getChildrenNames(String parentKey) {
	Collection<String> results = new HashSet<String>();
	for (String key : properties.keySet()) {
	    if (key.startsWith(parentKey + ".")) {
		if (key.equals(parentKey)) {
		    continue;
		}
		int dotIndex = key.indexOf(".", parentKey.length() + 1);
		if (dotIndex < 1) {
		    if (!results.contains(key)) {
			results.add(key);
		    }
		} else {
		    String name = parentKey
			    + key.substring(parentKey.length(), dotIndex);
		    results.add(name);
		}
	    }
	}
	return results;
    }

    /**
     * Returns all property names as a Collection of String values.
     * 
     * @return all property names.
     */
    public Collection<String> getPropertyNames() {
	return properties.keySet();
    }

    @Override
    public String remove(Object key) {
	String value;
	synchronized (this) {
	    value = properties.remove(key);
	    // Also remove any children.
	    Collection<String> propNames = getPropertyNames();
	    for (String name : propNames) {
		if (name.startsWith((String) key)) {
		    properties.remove(name);
		}
	    }

	    deleteProperty((String) key);
	}

	// Generate event.
	// Map<String, Object> params = Collections.emptyMap();

	return value;
    }

    @Override
    public int size() {
	return properties.size();
    }

    @Override
    public Collection<String> values() {
	return Collections.unmodifiableCollection(properties.values());
    }

    // http://blog.csdn.net/yao_guet/article/details/6587399
    // http://www.sqlite.com.cn/MySqlite/6/566.Html
    public class DbHelper extends SQLiteOpenHelper {

	private final static String TAG = "SQLiteHelper";

	private static final int VERSION = 1;

	private static final String name = "device.db";

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

	    db.execSQL("CREATE TABLE properties(" + MAC
		    + " varchar(100) primary key, " + VALUE + " TEXT)");

	    Log.d(TAG, "create properties's db OK!");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int paramInt1, int paramInt2) {
	    // TODO Auto-generated method stub
	    Log.d(TAG, "properties's db onUpgrade!");
	}
    }
}
