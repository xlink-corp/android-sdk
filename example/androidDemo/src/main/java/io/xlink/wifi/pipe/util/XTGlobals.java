package io.xlink.wifi.pipe.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * db 本地存储
 * 
 * @author Liuxy
 * @2015年1月22日上午11:07:16 </br>
 * @explain
 */
public class XTGlobals {

    private static XTProperties properties = null;

    /**
     * Returns a Doda property.
     * 
     * @param name
     *            the name of the property to return.
     * @return the property value specified by name.
     */
    public static String getProperty(String name) {
	if (properties == null) {
	    properties = XTProperties.getInstance();
	}
	return properties.get(name);
    }

    /**
     * Returns a Doda property. If the specified property doesn't exist, the
     * <tt>defaultValue</tt> will be returned.
     * 
     * @param name
     *            the name of the property to return.
     * @param defaultValue
     *            value returned if the property doesn't exist.
     * @return the property value specified by name.
     */
    public static String getProperty(String name, String defaultValue) {
	if (properties == null) {

	    properties = XTProperties.getInstance();
	}
	String value = properties.get(name);
	if (value != null) {
	    return value;
	} else {
	    return defaultValue;
	}
    }

    /**
     * Returns an integer value Doda property. If the specified property doesn't
     * exist, the <tt>defaultValue</tt> will be returned.
     * 
     * @param name
     *            the name of the property to return.
     * @param defaultValue
     *            value returned if the property doesn't exist or was not a
     *            number.
     * @return the property value specified by name or <tt>defaultValue</tt>.
     */
    public static int getIntProperty(String name, int defaultValue) {
	String value = getProperty(name);
	if (value != null) {
	    try {
		return Integer.parseInt(value);
	    } catch (NumberFormatException nfe) {
		// Ignore.
	    }
	}
	return defaultValue;
    }

    /**
     * Returns a long value Doda property. If the specified property doesn't
     * exist, the <tt>defaultValue</tt> will be returned.
     * 
     * @param name
     *            the name of the property to return.
     * @param defaultValue
     *            value returned if the property doesn't exist or was not a
     *            number.
     * @return the property value specified by name or <tt>defaultValue</tt>.
     */
    public static long getLongProperty(String name, long defaultValue) {
	String value = getProperty(name);
	if (value != null) {
	    try {
		return Long.parseLong(value);
	    } catch (NumberFormatException nfe) {
		// Ignore.
	    }
	}
	return defaultValue;
    }

    /**
     * Returns a boolean value Doda property.
     * 
     * @param name
     *            the name of the property to return.
     * @return true if the property value exists and is set to <tt>"true"</tt>
     *         (ignoring case). Otherwise <tt>false</tt> is returned.
     */
    public static boolean getBooleanProperty(String name) {
	return Boolean.valueOf(getProperty(name));
    }

    /**
     * Returns a boolean value Doda property. If the property doesn't exist, the
     * <tt>defaultValue</tt> will be returned.
     * 
     * If the specified property can't be found, or if the value is not a
     * number, the <tt>defaultValue</tt> will be returned.
     * 
     * @param name
     *            the name of the property to return.
     * @param defaultValue
     *            value returned if the property doesn't exist.
     * @return true if the property value exists and is set to <tt>"true"</tt>
     *         (ignoring case). Otherwise <tt>false</tt> is returned.
     */
    public static boolean getBooleanProperty(String name, boolean defaultValue) {
	String value = getProperty(name);
	if (value != null) {
	    return Boolean.valueOf(value);
	} else {
	    return defaultValue;
	}
    }

    /**
     * Return all immediate children property names of a parent Doda property as
     * a list of strings, or an empty list if there are no children. For
     * example, given the properties <tt>X.Y.A</tt>, <tt>X.Y.B</tt>,
     * <tt>X.Y.C</tt> and <tt>X.Y.C.D</tt>, then the immediate child properties
     * of <tt>X.Y</tt> are <tt>A</tt>, <tt>B</tt>, and <tt>C</tt> (<tt>C.D</tt>
     * would not be returned using this method).
     * <p>
     * 
     * @param parent
     *            Parent "node" to find the children of.
     * @return a List of all immediate children property names (Strings).
     */
    public static List<String> getPropertyNames(String parent) {
	if (properties == null) {
	    properties = XTProperties.getInstance();
	}
	return new ArrayList<String>(properties.getChildrenNames(parent));
    }

    /**
     * Return all immediate children property values of a parent Doda property
     * as a list of strings, or an empty list if there are no children. For
     * example, given the properties <tt>X.Y.A</tt>, <tt>X.Y.B</tt>,
     * <tt>X.Y.C</tt> and <tt>X.Y.C.D</tt>, then the immediate child properties
     * of <tt>X.Y</tt> are <tt>X.Y.A</tt>, <tt>X.Y.B</tt>, and <tt>X.Y.C</tt>
     * (the value of <tt>X.Y.C.D</tt> would not be returned using this method).
     * <p>
     * 
     * @param parent
     *            the name of the parent property to return the children for.
     * @return all child property values for the given parent.
     */
    public static List<String> getProperties(String parent) {
	if (properties == null) {
	    properties = XTProperties.getInstance();
	}

	Collection<String> propertyNames = properties.getChildrenNames(parent);
	List<String> values = new ArrayList<String>();
	for (String propertyName : propertyNames) {
	    String value = getProperty(propertyName);
	    if (value != null) {
		values.add(value);
	    }
	}

	return values;
    }

    public static Map<String, String> getAllProperty() {

	if (properties == null) {
	    properties = XTProperties.getInstance();
	}
	return properties.getProperties();
    }

    /**
     * Returns all Doda property names.
     * 
     * @return a List of all property names (Strings).
     */
    public static List<String> getPropertyNames() {
	if (properties == null) {
	    properties = XTProperties.getInstance();
	}
	return new ArrayList<String>(properties.getPropertyNames());
    }

    /**
     * Sets a Doda property. If the property doesn't already exists, a new one
     * will be created.
     * 
     * @param name
     *            the name of the property being set.
     * @param value
     *            the value of the property being set.
     */
    public static void setProperty(String name, String value) {
	if (properties == null) {
	    properties = XTProperties.getInstance();
	}
	properties.put(name, value);
    }

    /**
     * Sets multiple Doda properties at once. If a property doesn't already
     * exists, a new one will be created.
     * 
     * @param propertyMap
     *            a map of properties, keyed on property name.
     */
    public static void setProperties(Map<String, String> propertyMap) {
	if (properties == null) {
	    properties = XTProperties.getInstance();
	}

	properties.putAll(propertyMap);
    }

    /**
     * Deletes a Doda property. If the property doesn't exist, the method does
     * nothing. All children of the property will be deleted as well.
     * 
     * @param name
     *            the name of the property to delete.
     */
    public static void deleteProperty(String name) {
	if (properties == null) {
	    properties = XTProperties.getInstance();
	}
	properties.remove(name);
    }
}
