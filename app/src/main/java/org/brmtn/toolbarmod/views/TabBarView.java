package org.brmtn.toolbarmod.views;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import org.brmtn.toolbarmod.R;

public class TabBarView extends LinearLayout {
    private ActionMenu mActionMenu;

    private static final int STRIP_HEIGHT = 6;

    private final Paint paint;

    private int stripHeight;
    private float offset;
    private int selectedTab = -1;

    private OnTabClickedListener onTabClickedListener;

    public interface OnTabClickedListener{
        void onTabClicked(int index);
    }

    public TabBarView(Context context) {
        this(context, null);
    }

    public TabBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
        mActionMenu = new ActionMenu(context);
        paint.setColor(Color.WHITE);
        stripHeight = (int) (STRIP_HEIGHT * getResources().getDisplayMetrics().density + .5f);
        setAttributes(context, attrs);
    }

    private void setAttributes(Context context, AttributeSet attrs){
        Resources.Theme theme = context.getTheme();
        if (theme != null) {
            TypedArray typedArray = theme.obtainStyledAttributes(attrs, R.styleable.TabBarView, 0, 0);
            if (typedArray != null) {

                int n = typedArray.getIndexCount();

                for (int i = 0; i < n; i++){
                    int attr = typedArray.getIndex(i);

                    switch (attr){
                        case R.styleable.TabBarView_tabBarColor:
                            int color = typedArray.getColor(i, Color.WHITE);
                            paint.setColor(color);
                            break;
                        case R.styleable.TabBarView_tabBarSize:
                            stripHeight = typedArray.getDimensionPixelSize(i,
                                    (int) (STRIP_HEIGHT * getResources().getDisplayMetrics().density + .5f));
                            break;
                        case R.styleable.TabBarView_tabActions:
                            int xmlRes = typedArray.getResourceId(i, 0);
                            setTabs(xmlRes);
                            break;
                    }
                }
                typedArray.recycle();
            }
        }
    }



    private void updateTabs(){
        if (getChildCount() > 0) removeAllViews();
        if(mActionMenu!=null && mActionMenu.size()>0){
            for(int i=0; i<mActionMenu.size(); i++){
                MenuItem item = mActionMenu.getItem(i);
                TabView tabView = new TabView(getContext());
                tabView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                tabView.setIcon(item.getIcon());
                tabView.setIconDescription(item.getTitle());
                tabView.setBackgroundResource(R.drawable.default_selector);
                addView(tabView, i);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setupTabViews();
    }

    private void setupTabViews() {
        if (getChildCount() > 0){
            for (int i = 0; i < getChildCount(); i++){
                TabView child = (TabView) getChildAt(i);
                if (child != null) {
                    child.setTag(i);
                    child.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = (Integer) v.getTag();
                            if (onTabClickedListener != null) {
                                onTabClickedListener.onTabClicked(index);
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // Draw the strip manually
        final TabView child = (TabView) getChildAt(selectedTab);
        if (child != null) {
            int left = child.getLeft();
            int right = child.getRight();
            if (offset > 0) {
                View view = getChildAt(selectedTab + 1);
                if(view!=null && view instanceof TabView) {
                    final TabView nextChild = (TabView) view;
                    left = (int) (child.getLeft() + offset * (nextChild.getLeft() - child.getLeft()));
                    right = (int) (child.getRight() + offset * (nextChild.getRight() - child.getRight()));
                }
            }
            canvas.drawRect(left, getHeight() - stripHeight, right, getHeight(), paint);
        }
    }

    public void setOnTabClickedListener(OnTabClickedListener onTabClickedListener) {
        this.onTabClickedListener = onTabClickedListener;
    }

    /**
     * Set Menu resources as list item to display in TabBarView
     *
     * @param xmlRes mActionMenu resource id
     */
    public void setTabs(@MenuRes int xmlRes) {
        new MenuInflater(getContext()).inflate(xmlRes, mActionMenu);
        updateTabs();
    }

    /**
     * Add one item into TabBarView
     *
     * @param id      ID of item
     * @param iconRes mIcon resource
     * @param textRes text resource
     */
    public void setTabs(int id, @DrawableRes int iconRes, @StringRes int textRes) {
        setTabs(id, ContextCompat.getDrawable(getContext(), iconRes), getContext().getText(textRes));
    }

    /**
     * Add one item into TabBarView
     *
     * @param id   ID of item
     * @param icon mIcon
     * @param text text
     */
    public void setTabs(int id, @NonNull Drawable icon, @NonNull CharSequence text) {
        ActionMenuItem item = new ActionMenuItem(getContext(), 0, id, 0, 0, text);
        item.setIcon(icon);
        mActionMenu.add(item);
        updateTabs();
    }

    public void setStripColor(int color) {
        if (paint.getColor() != color) {
            paint.setColor(color);
            invalidate();
        }
    }

    public void setStripHeight(int height) {
        if (stripHeight != height) {
            stripHeight = height;
            invalidate();
        }
    }

    public void setSelectedTab(int tabIndex) {
        if (tabIndex < 0) {
            tabIndex = 0;
        }
        final int childCount = getChildCount();
        if (tabIndex >= childCount) {
            tabIndex = childCount - 1;
        }
        if (selectedTab != tabIndex) {
            selectedTab = tabIndex;
            invalidate();
        }
    }

    public void setOffset(float offset) {
        if (this.offset != offset) {
            this.offset = offset;
            invalidate();
        }
    }

    public TabView getTab(int position) {
        return (TabView) getChildAt(position);
    }
}
