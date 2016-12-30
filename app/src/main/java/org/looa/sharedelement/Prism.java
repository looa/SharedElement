package org.looa.sharedelement;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by ranxiangwei on 2016/12/30.
 */

public class Prism {

    private final static String SHARED_ELEMENT_DATA = "sharedElementData";

    private static class Holder {
        static Prism INSTANT = new Prism();
    }

    public static Prism getInstant() {
        return Holder.INSTANT;
    }


    public void startActivity(View view, Intent intent) {
        startActivity(view, intent, false, 0);
    }

    public void startActivity(View view, Intent intent, boolean justSharedImageView) {
        startActivity(view, intent, justSharedImageView, 0);
    }

    public void startActivity(View view, Intent intent, int sharedElementPosition) {
        startActivity(view, intent, true, sharedElementPosition);
    }

    public void startActivity(View view, Intent intent, boolean justSharedImageView, int sharedElementPosition) {
        int viewWidth = 0;
        int viewHeight = 0;
        int[] coordinate = new int[2];

        if (!justSharedImageView || !(view instanceof ViewGroup)) {
            viewWidth = view.getWidth();
            viewHeight = view.getHeight();
            view.getLocationInWindow(coordinate);
        } else {
            int count = 0;
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View targetView = ((ViewGroup) view).getChildAt(i);
                if (targetView instanceof ImageView) {
                    if (count == sharedElementPosition) {
                        viewWidth = targetView.getWidth();
                        viewHeight = targetView.getHeight();
                        targetView.getLocationInWindow(coordinate);
                        break;
                    }
                    count++;
                }
            }
            if (sharedElementPosition < 0 || sharedElementPosition >= ((ViewGroup) view).getChildCount()) {
                viewWidth = view.getWidth();
                viewHeight = view.getHeight();
                view.getLocationInWindow(coordinate);
            }
        }

        int coordinateX = coordinate[0];
        int coordinateY = coordinate[1];

        SharedElementData sourceData = new SharedElementData();
        sourceData.setWidth(viewWidth);
        sourceData.setHeight(viewHeight);
        sourceData.setCoordinateX(coordinateX);
        sourceData.setCoordinateY(coordinateY);

        intent.putExtra(SHARED_ELEMENT_DATA, sourceData);

        view.getContext().startActivity(intent);
        try {
            ((Activity) view.getContext()).overridePendingTransition(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSharedElement(final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeGlobalLayoutListener(view.getViewTreeObserver(), this);
                startWindowAnim(view);
                startViewAnim(view);
            }
        });
    }

    private void startViewAnim(View view) {
        SharedElementData data = (SharedElementData) ((Activity) view.getContext()).getIntent().getSerializableExtra(SHARED_ELEMENT_DATA);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "scaleX", 1f * data.getWidth() / view.getWidth(), 1);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleY", 1f * data.getHeight() / view.getHeight(), 1);
        AnimatorSet set = new AnimatorSet();
        set.play(animator).with(animator2);
        set.setDuration(300);
        set.setInterpolator(new BounceInterpolator());
        set.start();
    }

    private void startWindowAnim(final View view) {
        view.getContext().setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        Window window = ((Activity) view.getContext()).getWindow();
        View parent = window.getDecorView().findViewById(android.R.id.content);
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(300);
        parent.setAnimation(animation);
    }

    @TargetApi(16)
    private void removeGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener) {
        observer.removeOnGlobalLayoutListener(listener);
    }

    private static class SharedElementData implements Serializable {
        int width;
        int height;
        int coordinateX;
        int coordinateY;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getCoordinateX() {
            return coordinateX;
        }

        public void setCoordinateX(int coordinateX) {
            this.coordinateX = coordinateX;
        }

        public int getCoordinateY() {
            return coordinateY;
        }

        public void setCoordinateY(int coordinateY) {
            this.coordinateY = coordinateY;
        }
    }
}
