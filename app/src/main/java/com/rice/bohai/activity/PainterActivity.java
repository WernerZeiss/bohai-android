package com.rice.bohai.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.rice.bohai.MyApplication;
import com.rice.bohai.R;
import com.rice.bohai.custom.PaintView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * Created by Developer on 2017/6/27.
 */

public class PainterActivity extends Activity implements OnClickListener, CancelAdapt {

    private Intent intent;
    private String fileName;
    private SharedPreferences sp;
    private List<String> paintColorList;
    private final static int REQUEST_CODE = 1;
    private int select_paint_style_index = 1;//1笔，0橡皮

    //    注册控件
    private TextView btnRevokePaint;
    private TextView btnRedoPaint;
    private TextView btnCleanPaint;
    private ImageView btnPenStylePaint;
    private TextView btnPenColorPaint;
    private TextView btnBackPaint;
    private TextView btnSavePaint;
    private TextView text_pen_size;
    private PaintView paintViewPad;
    private ConstraintLayout constraintLayout;
    private LinearLayout paint_linear;
    private SeekBar seekBar_pen_size;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint_layout);

        initView();
//        AppData.setFinalPage(2);

        //        获取intent
        intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("fileName")) {
//            获取intent中的值
                fileName = intent.getStringExtra("fileName");
//            为控件赋值
                initData(fileName);
            } else {
                initData(null);
                //获得系统当前时间，并以该时间作为文件名
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                fileName = MyApplication.instance.imageFilePath + "paint" + formatter.format(curDate) + ".png";
            }
        }

//        //        沉浸式状态栏
//        // 4.4以上版本开启
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//            setTranslucentStatus(true);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setNavigationBarTintEnabled(true);
//
//            // 状态栏背景色
//            if (Build.VERSION.SDK_INT < 23) {
//                tintManager.setStatusBarTintResource(R.color.colorAccent);
//            } else {
//                tintManager.setTintColor(getColor(R.color.colorAccent));
//            }
//        }
    }

    @Override
    public void onBackPressed() {
        paintViewPad.saveToSDCard(fileName);
        finish();
    }

    private void initData(String fileName) {
        //获取的是屏幕宽高，通过控制freamlayout来控制涂鸦板大小
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        int screenWidth = defaultDisplay.getWidth();
        int screenHeight = defaultDisplay.getHeight() - 110;
        paintViewPad = new PaintView(this, screenWidth, screenHeight, fileName);
        paint_linear.addView(paintViewPad);
        paintViewPad.requestFocus();
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
//        AppData.setPenColor(sp.getInt("pencolor", -65536));
//        AppData.setPenSize(sp.getInt("pensize", 9));
//        paintColorList = Arrays.asList(getResources().getStringArray(R.array.paintcolor));
        paintViewPad.selectPaintColor(Color.BLACK);
        paintViewPad.selectPaintStyle(select_paint_style_index);
//        seekBar_pen_size.setProgress(9);
        paintViewPad.selectPaintSize(9);

    }

    public void initView() {
//        实例化控件
//        btnSavePaint = findViewById(R.id.btn_save_paint);
//        btnRevokePaint = findViewById(R.id.btn_revoke_paint);
//        btnRedoPaint = findViewById(R.id.btn_redo_paint);
        btnCleanPaint = findViewById(R.id.btn_clean_paint);
//        btnPenStylePaint = findViewById(R.id.btn_pen_style_paint);
//        btnPenColorPaint = findViewById(R.id.btn_pen_color_paint);
//        btnBackPaint = findViewById(R.id.btn_back_paint);
//        text_pen_size = findViewById(R.id.text_pen_size);
        constraintLayout = findViewById(R.id.framelayout_paint);
//        seekBar_pen_size = findViewById(R.id.seekbar_pen_size);
        paint_linear = findViewById(R.id.paint_linear);

//        btnSavePaint.setOnClickListener(this);
//        btnRevokePaint.setOnClickListener(this);
//        btnRedoPaint.setOnClickListener(this);
        btnCleanPaint.setOnClickListener(this);
//        btnPenStylePaint.setOnClickListener(this);
//        btnPenColorPaint.setOnClickListener(this);
//        btnBackPaint.setOnClickListener(this);
//        seekBar_pen_size.setOnSeekBarChangeListener(new MySeekChangeListener());

//        btnPenStylePaint.getBackground().setLevel(0);
//        btnRevokePaint.setEnabled(false);
//        btnRedoPaint.setEnabled(false);

//        paintViewPad.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                btnRevokePaint.setEnabled(paintViewPad.haveSavePath());
//                btnRedoPaint.setEnabled(paintViewPad.haveDeletePath());
//            }
//        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn_revoke_paint:
//                //撤销
//                paintViewPad.undo();
//                break;
//            case R.id.btn_redo_paint:
//                //重做
//                paintViewPad.recover();
//                break;
            case R.id.btn_clean_paint:
                //清空
                paintViewPad.clean();
                break;
//            case R.id.btn_pen_style_paint:
//                //设置画笔样式
//                changePenStyle();
//                break;
//            case R.id.btn_pen_color_paint:
//                //选择画笔颜色
//                intent = new Intent(this, ColorSelectActivity.class);
//                startActivityForResult(intent, REQUEST_CODE);
//                break;
//            case R.id.btn_back_paint:
//                //返回
//                paintViewPad.saveToSDCard(fileName);
//                finish();
//                break;
//            case R.id.btn_save_paint:
//                //保存
//                paintViewPad.saveToSDCard(fileName);
//                finish();
//                break;
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        AppData.setPenColor(data.getIntExtra("color", 0xff0000));
//        paintViewPad.selectPaintColor(AppData.getPenColor());
//    }

    /**
     * 切换画笔和橡皮
     */
    public void changePenStyle() {
        btnPenStylePaint.getBackground().setLevel(select_paint_style_index);
        switch (select_paint_style_index) {
            case 0:
                select_paint_style_index = 1;
                Toast.makeText(this, "切换到画笔", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                select_paint_style_index = 0;
                Toast.makeText(this, "切换到橡皮", Toast.LENGTH_SHORT).show();
                break;
        }
        paintViewPad.selectPaintStyle(select_paint_style_index);
    }


    /**
     * 沉浸式状态栏
     */
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

//    @Override
//    protected void onDestroy() {
//        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putInt("pencolor", AppData.getPenColor());
//        editor.putInt("pensize", AppData.getPenSize());
//        editor.apply();
//        super.onDestroy();
//    }

    @Override
    protected void onPause() {
        //避免style定义的转场退出时候出现2次
        this.overridePendingTransition(0, 0);
        super.onPause();
    }

    /**
     * 画笔尺寸选择监听
     */
    private class MySeekChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            AppData.setPenSize(seekBar.getProgress());
//            paintViewPad.selectPaintSize(AppData.getPenSize());
//            text_pen_size.setText("画笔尺寸：" + Integer.toString(AppData.getPenSize() + 1));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
//            AppData.setPenSize(seekBar.getProgress());
//            paintViewPad.selectPaintSize(AppData.getPenSize());
//            text_pen_size.setText("画笔尺寸：" + Integer.toString(AppData.getPenSize() + 1));
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }
}
