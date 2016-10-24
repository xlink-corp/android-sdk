package io.xlink.wifi.pipe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import io.xlink.wifi.pipe.R;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.bean.DataPoint;

/**
 * 设备列表适配器
 *
 * @author Liuxy
 * @2015年3月25日下午5:27:31 </br>
 * @explain
 */
public class DataPointListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DataPoint> dataPoints;

    public DataPointListAdapter(Context context) {

        this.mContext = context;
    }

    public void setData(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (dataPoints == null) {
            return 0;
        }
        return dataPoints.size();
    }

    @Override
    public Object getItem(int position) {
        return dataPoints.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int indexPos = -1;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.datapoint_item, parent, false);
        }

        final DataPoint dataPoint = dataPoints.get(position);
        if (dataPoint != null) {


            TextView index = (TextView) convertView.findViewById(R.id.index);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            LinearLayout layout_string = (LinearLayout) convertView.findViewById(R.id.layout_string);
            LinearLayout layout_int = (LinearLayout) convertView.findViewById(R.id.layout_int);
            LinearLayout layout_bool = (LinearLayout) convertView.findViewById(R.id.layout_bool);

            final EditText value = (EditText) convertView.findViewById(R.id.value);
            final TextView tv_text = (TextView) convertView.findViewById(R.id.tv_text);
            SeekBar seekbar = (SeekBar) convertView.findViewById(R.id.seekbar);
            RadioButton cb_true = (RadioButton) convertView.findViewById(R.id.cb_true);
            RadioButton cb_flase = (RadioButton) convertView.findViewById(R.id.cb_flase);
            RadioGroup radio_group = (RadioGroup) convertView.findViewById(R.id.radio_group);
            TextView btSetting = (TextView) convertView.findViewById(R.id.bt_setting);

            layout_bool.setVisibility(View.GONE);
            layout_int.setVisibility(View.GONE);
            layout_string.setVisibility(View.GONE);

            index.setText(dataPoint.getIndex() + "");
            name.setText(dataPoint.getName() + " (" + dataPoint.getSymbol() + ")");
            switch (dataPoint.getType()) {
                case XlinkCode.DP_TYPE_BOOL:
                    layout_bool.setVisibility(View.VISIBLE);
                    if ((Boolean) dataPoint.getValue() == true) {
                        cb_true.setChecked(true);
                    } else {
                        cb_flase.setChecked(true);
                    }
                    break;
                case XlinkCode.DP_TYPE_BYTE:
                    layout_int.setVisibility(View.VISIBLE);
                    if (dataPoint.getMax() == 0 && dataPoint.getMin() == 0) {
                        dataPoint.setMax(0xff);
                        dataPoint.setMin(0);
                    }
                    seekbar.setMax(dataPoint.getMax() - dataPoint.getMin());
                    seekbar.setProgress((int) dataPoint.getValueOfUnsigned() - dataPoint.getMin());
                    tv_text.setText(dataPoint.getValueOfUnsigned() + "");
                    break;
                case XlinkCode.DP_TYPE_SHORT:
                    layout_int.setVisibility(View.VISIBLE);
                    if (dataPoint.getMax() == 0 && dataPoint.getMin() == 0) {
                        dataPoint.setMax(0xffff);
                        dataPoint.setMin(0);
                    }
                    seekbar.setMax(dataPoint.getMax() - dataPoint.getMin());
                    seekbar.setProgress((int) dataPoint.getValueOfUnsigned() - dataPoint.getMin());
                    tv_text.setText(dataPoint.getValueOfUnsigned() + "");
                    break;
                case XlinkCode.DP_TYPE_INT:
                    layout_int.setVisibility(View.VISIBLE);
                    if (dataPoint.getMax() == 0 && dataPoint.getMin() == 0) {
                        dataPoint.setMax(Integer.MAX_VALUE);
                        dataPoint.setMin(0);
                    }
                    seekbar.setMax(dataPoint.getMax() - dataPoint.getMin());
                    //seekbar.setProgress(((int) dataPoint.getValueOfUnsigned() - dataPoint.getMin())/(dataPoint.getMax() - dataPoint.getMin())*100);
                    seekbar.setProgress((int) dataPoint.getValueOfUnsigned() - dataPoint.getMin());
                    tv_text.setText(dataPoint.getValueOfUnsigned() + "");
                    break;
//                case XlinkCode.DP_TYPE_FLOAT:
//                    layout_int.setVisibility(View.VISIBLE);
//                    break;
                case XlinkCode.DP_TYPE_STRING:
                    layout_string.setVisibility(View.VISIBLE);
                    value.setText(dataPoint.getValue() + "");
                    break;
            }

            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
//                        float setp=(dataPoint.getMax()-dataPoint.getMin())/(seekBar.getMax());
                        tv_text.setText(progress + dataPoint.getMin() + "");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (onItemClickListener != null) {
                        switch (dataPoint.getType()) {
                            case XlinkCode.DP_TYPE_INT:
                                dataPoint.setValueOfInt(seekBar.getProgress() + dataPoint.getMin());
                                break;
                            case XlinkCode.DP_TYPE_SHORT:
                                dataPoint.setValueOfShort((short) (seekBar.getProgress() + dataPoint.getMin()));
                                break;
                            case XlinkCode.DP_TYPE_BYTE:
                                dataPoint.setValueOfByte((byte) (seekBar.getProgress() + dataPoint.getMin()));
                                break;
                        }

                        onItemClickListener.onSettingClick(position, dataPoint);
                    }
                }
            });

            radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (onItemClickListener != null) {
                        switch (checkedId) {
                            case R.id.cb_true:
                                dataPoint.setValueOfBool(true);
                                break;
                            case R.id.cb_flase:
                                dataPoint.setValueOfBool(false);
                                break;
                        }

                        onItemClickListener.onSettingClick(position, dataPoint);
                    }
                }
            });
//            final EditText value = (EditText) convertView.findViewById(R.id.value);
//            TextView btSetting = (TextView) convertView.findViewById(R.id.bt_setting);
//
            btSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        String val = value.getText().toString();
                        switch (dataPoint.getType()) {
                            case XlinkCode.DP_TYPE_STRING:
                                dataPoint.setValueOfString(val);
                                break;
                        }
                        onItemClickListener.onSettingClick(position, dataPoint);
                    }
                }
            });


//            value.setOnTouchListener(new View.OnTouchListener() {
//
//                public boolean onTouch(View view, MotionEvent event) {
//
//                    // 在TOUCH的UP事件中，要保存当前的行下标，因为弹出软键盘后，整个画面会被重画
//
//                    // 在getView方法的最后，要根据index和当前的行下标手动为EditText设置焦点
//
//                    if(event.getAction() == MotionEvent.ACTION_UP) {
//
//                        indexPos= position;
//
//                    }
//
//                    return false;
//
//                }
//
//            });
//
//            if(indexPos!= -1 && indexPos == position) {
//
//                // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
//
//                value.requestFocus();
//
//            }
//
//            index.setText(dataPoint.getIndex() + "");
//            name.setText(dataPoint.getName() + " (" + dataPoint.getSymbol() + ")");
//            if (dataPoint.getValue() != null) {
//                value.setText(dataPoint.getValueOfUnsigned() + "");
//            } else {
//                value.setText("");
//            }
        }
        return convertView;

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    OnItemClickListener onItemClickListener = null;

    public interface OnItemClickListener {
        public void onSettingClick(int index, DataPoint dataPoint);
    }
}
