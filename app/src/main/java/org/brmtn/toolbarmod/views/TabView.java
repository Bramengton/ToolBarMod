package org.brmtn.toolbarmod.views;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.brmtn.toolbarmod.R;

public class TabView extends LinearLayout {
    private static final int ESTIMATED_TOAST_HEIGHT_DIPS = 48;
    private ImageView imageView;

    public TabView(Context context) {
        this(context, null);
    }

    public TabView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.button);
    }

    public TabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.tab_view_merge, this);
        imageView = (ImageView) findViewById(R.id.image);
        setAttributes(context, attrs);
        setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.app_bar_height));
        setGravity(Gravity.CENTER);
        int eightDp = getResources().getDimensionPixelSize(R.dimen.eight_dp);
        setPadding(eightDp, 0, eightDp, 0);
    }

    private void setAttributes(Context context, AttributeSet attrs){
        Resources.Theme theme = context.getTheme();
        if (theme != null) {
            TypedArray typedArray = theme.obtainStyledAttributes(attrs,R.styleable.TabView, 0, 0);
            if (typedArray != null) {

                int n = typedArray.getIndexCount();

                for (int i = 0; i < n; i++){
                    int attr = typedArray.getIndex(i);

                    switch (attr){
                        case R.styleable.TabView_icon:
                            int src = typedArray.getResourceId(i, 0);
                            setIcon(src);
                            break;
                        case R.styleable.TabView_description:
                            CharSequence text = typedArray.getText(attr);
                            setIconDescription(text);
                            break;
                    }
                }
                typedArray.recycle();
            }
        }
    }

    public void setIcon(int resId) {
        setIcon(getContext().getResources().getDrawable(resId));
    }

    public void setIcon(Drawable icon) {
        if (icon != null) imageView.setImageDrawable(icon);
    }

    public void setIconDescription(@StringRes int resId) {
        setIconDescription(getContext().getString(resId));
    }

    public void setIconDescription(CharSequence description) {
        if (!TextUtils.isEmpty(description)) setContentDescription(description);
        updateHint();
    }

    public ImageView getImageView(){
        return imageView;
    }

    @Override
    public void setContentDescription(CharSequence contentDescription) {
        super.setContentDescription(contentDescription);
        updateHint();
    }

    private void updateHint() {
        boolean needHint = !TextUtils.isEmpty(getContentDescription());
        if (needHint) {
            setOnLongClickListener(mOnLongClickListener);
        } else {
            setOnLongClickListener(null);
            setLongClickable(false);
        }
    }

    private OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showCheatSheet(getContentDescription());
            return true;
        }
    };

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Internal helper method to show the cheat setTabs toast.
     */
    private void showCheatSheet(CharSequence text) {
        final int[] screenPos = new int[2]; // origin is device display
        final Rect displayFrame = new Rect(); // includes decorations (e.g. status bar)
        getLocationOnScreen(screenPos);
        getWindowVisibleDisplayFrame(displayFrame);

        final int viewWidth = getWidth();
        final int viewHeight = getHeight();
        final int viewCenterX = screenPos[0] + viewWidth / 2;
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final int estimatedToastHeight = (int) (ESTIMATED_TOAST_HEIGHT_DIPS * getResources().getDisplayMetrics().density);

        Toast cheatSheet = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        boolean showBelow = screenPos[1] < estimatedToastHeight;
        if (showBelow) {
            // Show below
            // Offsets are after decorations (e.g. status bar) are factored in
            cheatSheet.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                    viewCenterX - screenWidth / 2,
                    screenPos[1] - displayFrame.top + viewHeight);
        } else {
            // Show above
            // Offsets are after decorations (e.g. status bar) are factored in
            // NOTE: We can't use Gravity.BOTTOM because when the keyboard is up
            // its height isn't factored in.
            cheatSheet.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                    viewCenterX - screenWidth / 2,
                    screenPos[1] - displayFrame.top - estimatedToastHeight);
        }

        cheatSheet.show();
    }
}
