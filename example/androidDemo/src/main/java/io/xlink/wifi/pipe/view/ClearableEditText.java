package io.xlink.wifi.pipe.view;

import io.xlink.wifi.pipe.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class ClearableEditText extends EditText {

    public String defaultValue = "";
    Drawable leftDrawable;
    final Drawable rightDrawable = getResources().getDrawable(
	    R.drawable.clear_nor);

    public ClearableEditText(Context context) {
	super(context);

	init();
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);

	init();
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
	super(context, attrs);

	init();
    }

    void init() {

	// Set bounds of our X button
	rightDrawable.setBounds(5, 0, rightDrawable.getIntrinsicWidth() + 10,
		rightDrawable.getIntrinsicHeight() + 5);

	/*
	 * leftDrawable.setBounds(0,0,leftDrawable.getIntrinsicWidth()+10,
	 * leftDrawable.getIntrinsicHeight()+5);
	 */

	// There may be initial text in the field, so we may need to display the
	// button
	manageClearButton();

	this.setOnTouchListener(new OnTouchListener() {
	    @Override
	    public boolean onTouch(View v, MotionEvent event) {

		ClearableEditText et = ClearableEditText.this;

		// Is there an X showing?
		if (et.getCompoundDrawables() == null)
		    return false;
		// Only do this for up touches
		if (event.getAction() != MotionEvent.ACTION_UP)
		    return false;
		// Is touch on our clear button?
		if (event.getX() > et.getWidth() - et.getPaddingRight()
			- rightDrawable.getIntrinsicWidth()) {
		    et.setText("");
		    ClearableEditText.this.removeClearButton();
		}
		return false;
	    }
	});

	this.addTextChangedListener(new TextWatcher() {
	    @Override
	    public void onTextChanged(CharSequence s, int start, int before,
		    int count) {

		ClearableEditText.this.manageClearButton();
	    }

	    @Override
	    public void afterTextChanged(Editable arg0) {
	    }

	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count,
		    int after) {
	    }
	});

	this.setOnFocusChangeListener(new OnFocusChangeListener() {
	    @Override
	    public void onFocusChange(View v, boolean hasFocus) {
		ClearableEditText.this.manageClearButton();
	    }
	});
    }

    void manageClearButton() {
	if (this.getText().toString().equals("") || !this.isFocused())
	    removeClearButton();
	else
	    addClearButton();
    }

    void addClearButton() {
	/*
	 * this.setCompoundDrawables(this.getCompoundDrawables(),
	 * this.getCompoundDrawables(), imgX, this.getCompoundDrawables());
	 */

	this.setCompoundDrawables(leftDrawable, null, rightDrawable, null);
    }

    void removeClearButton() {
	/*
	 * this.setCompoundDrawables(this.getCompoundDrawables(),
	 * this.getCompoundDrawables(), null, this.getCompoundDrawables());
	 */

	this.setCompoundDrawables(leftDrawable, null, null, null);
    }

}