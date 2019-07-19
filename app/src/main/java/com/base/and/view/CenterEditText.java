package com.base.and.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import com.base.and.R;
import com.base.and.utils.SoftKeyBoardListener;

@SuppressLint("AppCompatCustomView")
public class CenterEditText extends EditText {

    private Context context;
    private int drawableIcon; // icon图标
    private boolean isShowCenter;//是否居中显示icon，默认为不居中
    private boolean isShowLeft;//键盘打开后icon是否显示在左边，默认为不显示icon
    private boolean isShowHint;//键盘打开后是否显示提示文字,默认为显示
    private boolean isOpen = true;//是否开启使用,默认为true

    private boolean isDraw = true;//是否绘制,配合居中显示使用
    private String hintText;

    public CenterEditText(Context context) {
        super(context);
        this.context = context;
        initView(null);
    }

    public CenterEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(attrs);
    }

    public CenterEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CenterEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CenterEditText);
            isShowCenter = array.getBoolean(R.styleable.CenterEditText_isCenter, false);
            isShowLeft = array.getBoolean(R.styleable.CenterEditText_isShowLeft, false);
            isShowHint = array.getBoolean(R.styleable.CenterEditText_isShowHint, true);
            isOpen = array.getBoolean(R.styleable.CenterEditText_isOpen, true);
            drawableIcon = array.getResourceId(R.styleable.CenterEditText_drawableIcon, R.drawable.icon_search);
            array.recycle();
        }
        if (context instanceof Activity && isOpen) {
            hintText = getHint().toString();
            SoftKeyBoardListener.setListener((Activity) context, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
                @Override
                public void keyBoardShow(int height) {
                    //键盘处于打开状态
                    setCursorVisible(true);// 显示光标
                    isDraw = false;//重新绘制(icon居左或者不显示)
                    if (!isShowHint)
                        setHint("");
                    else {
                        if (!TextUtils.isEmpty(hintText))
                            setHint(hintText);
                    }
                }

                @Override
                public void keyBoardHide(int height) {
                    //键盘处于关闭状态
                    setCursorVisible(false);// 隐藏光标
                    isDraw = TextUtils.isEmpty(getText().toString());
                    if (!TextUtils.isEmpty(hintText))
                        setHint(hintText);
                }
            });
        }
    }

    /**
     * 解决直接setText 文字位置不对的问题
     * 跟键盘监听操作同步即可
     *
     * @param text
     * @param type
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (!TextUtils.isEmpty(text)) {
            //键盘处于打开状态
            setCursorVisible(true);// 显示光标
            isDraw = false;//重新绘制(icon居左或者不显示)
            if (!isShowHint)
                setHint("");
            else {
                if (!TextUtils.isEmpty(hintText))
                    setHint(hintText);
            }
        } else {
            //键盘处于关闭状态
            setCursorVisible(false);// 隐藏光标
            isDraw = TextUtils.isEmpty(getText().toString());
            if (!TextUtils.isEmpty(hintText))
                setHint(hintText);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        if (!isOpen) {
            super.onDraw(canvas);
            return;
        }
        if (isShowCenter && isDraw) {// 将icon绘制在中间
            setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(drawableIcon), null, null, null);//绘制图片
            float textWidth = getPaint().measureText(getHint().toString());//得到文字宽度
            int drawablePadding = getCompoundDrawablePadding();//得到drawablePadding宽度
            int drawableWidth = context.getDrawable(drawableIcon).getIntrinsicWidth();//得到图片宽度
            float bodyWidth = textWidth + drawableWidth + drawablePadding;//计算距离
            canvas.translate((getWidth() - bodyWidth - getPaddingLeft() - getPaddingRight()) / 2, 0);//最终绘制位置
            super.onDraw(canvas);
        } else {
            if (isShowLeft) {
                setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(drawableIcon), null, null, null);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
            super.onDraw(canvas);
        }
    }
}
