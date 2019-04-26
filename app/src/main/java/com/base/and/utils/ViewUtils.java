package com.base.and.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ViewUtils {

    private static final String CLASS_NAME_GRID_VIEW = "android.widget.GridView";
    private static final String FIELD_NAME_VERTICAL_SPACING = "mVerticalSpacing";
    public static long lastClickTime = 0;
    public static View view;

    /**
     * get ListView height according to every children
     *
     * @param view
     * @return
     */
    public static int getListViewHeightBasedOnChildren(ListView view) {
        int height = getAbsListViewHeightBasedOnChildren(view);
        ListAdapter adapter;
        int adapterCount;
        if (view != null && (adapter = view.getAdapter()) != null
                && (adapterCount = adapter.getCount()) > 0) {
            height += view.getDividerHeight() * (adapterCount - 1);
        }
        return height;
    }

    // /**
    // * get GridView height according to every children
    // *
    // * @param view
    // * @return
    // */
    // public static int getGridViewHeightBasedOnChildren(GridView view) {
    // int height = getAbsListViewHeightBasedOnChildren(view);
    // ListAdapter adapter;
    // int adapterCount, numColumns = getGridViewNumColumns(view);
    // if (view != null && (adapter = view.getAdapter()) != null && (adapterCount = adapter.getCount()) > 0
    // && numColumns > 0) {
    // int rowCount = (int)Math.ceil(adapterCount / (double)numColumns);
    // height = rowCount * (height / adapterCount + getGridViewVerticalSpacing(view));
    // }
    // return height;
    // }
    //
    // /**
    // * get GridView columns number
    // *
    // * @param view
    // * @return
    // */
    // public static int getGridViewNumColumns(GridView view) {
    // if (view == null || view.getChildCount() <= 0) {
    // return 0;
    // }
    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
    // return getNumColumnsCompat11(view);
    //
    // } else {
    // int columns = 0;
    // int children = view.getChildCount();
    // if (children > 0) {
    // int width = view.getChildAt(0).getMeasuredWidth();
    // if (width > 0) {
    // columns = view.getWidth() / width;
    // }
    // }
    // return columns;
    // }
    // }
    //
    // @TargetApi(11)
    // private static int getNumColumnsCompat11(GridView view) {
    // return view.getNumColumns();
    // }

    public static StateListDrawable setbg(Context context, int normalId, int pressId) {
        StateListDrawable bg = new StateListDrawable();
        Drawable idNormal = context.getResources().getDrawable(normalId);
        Drawable idPressed = context.getResources().getDrawable(pressId);
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled,
                android.R.attr.selectable, android.R.attr.focusable}, idPressed);
        bg.addState(new int[]{android.R.attr.state_enabled}, idNormal);
        bg.addState(new int[]{}, idNormal);
        return bg;
    }

    public static boolean isFastDoubleClick(View _view) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        if (view != null) {
            if (view.equals(_view)) {
                if (0 < timeD && timeD < 800) {
                    return true;
                }
            } else {
                view = _view;
                return false;
            }
        } else {
            view = _view;
            return false;
        }
        return false;
    }

    /**
     * get GridView vertical spacing
     *
     * @param view
     * @return
     */
    public static int getGridViewVerticalSpacing(GridView view) {
        // get mVerticalSpacing by android.widget.GridView
        Class<?> demo = null;
        int verticalSpacing = 0;
        try {
            demo = Class.forName(CLASS_NAME_GRID_VIEW);
            Field field = demo.getDeclaredField(FIELD_NAME_VERTICAL_SPACING);
            field.setAccessible(true);
            verticalSpacing = (Integer) field.get(view);
            return verticalSpacing;
        } catch (Exception e) {
            /**
             * accept all exception, include ClassNotFoundException, NoSuchFieldException, InstantiationException,
             * IllegalArgumentException, IllegalAccessException, NullPointException
             */
            e.printStackTrace();
        }
        return verticalSpacing;
    }

    /**
     * get AbsListView height according to every children
     *
     * @param view
     * @return
     */
    public static int getAbsListViewHeightBasedOnChildren(AbsListView view) {
        ListAdapter adapter;
        if (view == null || (adapter = view.getAdapter()) == null) {
            return 0;
        }

        int height = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, view);
            if (item instanceof ViewGroup) {
                item.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
            }
            item.measure(0, 0);
            height += item.getMeasuredHeight();
        }
        height += view.getPaddingTop() + view.getPaddingBottom();
        return height;
    }

    /**
     * set view height
     *
     * @param view
     * @param height
     */
    public static void setViewHeight(View view, int height) {
        if (view == null) {
            return;
        }

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
    }

    // /**
    // * set GistView height which is calculated by {@link # getGridViewHeightBasedOnChildren(GridView)}
    // *
    // * @param view
    // * @return
    // */
    // public static void setGridViewHeightBasedOnChildren(GridView view) {
    // setViewHeight(view, getGridViewHeightBasedOnChildren(view));
    // }

    /**
     * set ListView height which is calculated by {@link # getListViewHeightBasedOnChildren(ListView)}
     *
     * @param view
     * @return
     */
    public static void setListViewHeightBasedOnChildren(ListView view) {
        setViewHeight(view, getListViewHeightBasedOnChildren(view));
    }

    /**
     * set AbsListView height which is calculated by {@link # getAbsListViewHeightBasedOnChildren(AbsListView)}
     *
     * @param view
     * @return
     */
    public static void setAbsListViewHeightBasedOnChildren(AbsListView view) {
        setViewHeight(view, getAbsListViewHeightBasedOnChildren(view));
    }

    /**
     * set SearchView OnClickListener
     *
     * @param v
     * @param listener
     */
    public static void setSearchViewOnClickListener(View v, OnClickListener listener) {
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = group.getChildAt(i);
                if (child instanceof LinearLayout || child instanceof RelativeLayout) {
                    setSearchViewOnClickListener(child, listener);
                }

                if (child instanceof TextView) {
                    TextView text = (TextView) child;
                    text.setFocusable(false);
                }
                child.setOnClickListener(listener);
            }
        }
    }

    /**
     * get descended views from parent.
     *
     * @param parent
     * @param filter          Type of views which will be returned.
     * @param includeSubClass Whether returned list will include views which are subclass of filter or not.
     * @return
     */
    public static <T extends View> List<T> getDescendants(ViewGroup parent, Class<T> filter,
                                                          boolean includeSubClass) {
        List<T> descendedViewList = new ArrayList<T>();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            Class<? extends View> childsClass = child.getClass();
            if ((includeSubClass && filter.isAssignableFrom(childsClass))
                    || (!includeSubClass && childsClass == filter)) {
                descendedViewList.add(filter.cast(child));
            }
            if (child instanceof ViewGroup) {
                descendedViewList
                        .addAll(getDescendants((ViewGroup) child, filter, includeSubClass));
            }
        }
        return descendedViewList;
    }

    public static void onCheckedChanged(Context context, RadioGroup group, int checkedId) {
//        int n = group.getChildCount();
//        for (int i = 0; i < n; i++) {
//            ((RadioButton) (group.getChildAt(i))).setTextColor(context.getResources().getColor(
//                    R.color.orange));
//        }
//        ((RadioButton) (group.findViewById(checkedId))).setTextColor(context.getResources()
//                .getColor(R.color.white));
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

}
