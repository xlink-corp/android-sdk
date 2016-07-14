package io.xlink.wifi.pipe.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import io.xlink.wifi.sdk.util.MyLog;

public class BaseFragment extends Fragment {
    private boolean isRun = false;

    public void Log(String msg) {
	MyLog.e(this.getClass().getSimpleName(), msg);
    }

    @Override
    public void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
//	Log("onPause");
	isRun = false;
    }

    @Override
    public void onDetach() {
	// TODO Auto-generated method stub
	super.onDetach();
	Field childFragmentManager;
	try {
	    childFragmentManager = Fragment.class
		    .getDeclaredField("mChildFragmentManager");
	    childFragmentManager.setAccessible(true);
	    childFragmentManager.set(this, null);
	} catch (NoSuchFieldException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	Log("onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
	// TODO Auto-generated method stub
	super.onDestroyView();
//	Log("onDestroyView");
    }

    @Override
    public void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
//	Log("onDestroy");
    }

    @Override
    public void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	isRun = true;
//	Log("onResume");
    }

    public final boolean isRun() {
	return isRun;
    }

}
