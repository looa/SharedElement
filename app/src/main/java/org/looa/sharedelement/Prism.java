package org.looa.sharedelement;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by ranxiangwei on 2016/12/30.
 */

public class Prism {

    private final static String SHARED_ELEMENT_DATA = "sharedElementData";
    private final static int ANIM_DURATION = 250;

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
        float[] coordinate = new float[2];

        if (!justSharedImageView || !(view instanceof ViewGroup)) {
            viewWidth = view.getWidth();
            viewHeight = view.getHeight();
            coordinate[0] = view.getX();
            coordinate[1] = view.getY();
        } else {
            int count = 0;
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View targetView = ((ViewGroup) view).getChildAt(i);
                if (targetView instanceof ImageView) {
                    if (count == sharedElementPosition) {
                        viewWidth = targetView.getWidth();
                        viewHeight = targetView.getHeight();
                        coordinate[0] = targetView.getX();
                        coordinate[1] = targetView.getY();
                        break;
                    }
                    count++;
                }
            }
            if (sharedElementPosition < 0 || sharedElementPosition >= ((ViewGroup) view).getChildCount()) {
                viewWidth = view.getWidth();
                viewHeight = view.getHeight();
                coordinate[0] = view.getX();
                coordinate[1] = view.getY();
            }
        }

        float coordinateX = coordinate[0];
        float coordinateY = coordinate[1];

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
        if (data == null) return;
        int width = view.getWidth();
        int height = view.getHeight();
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.setClipChildren(false);
            parent.setClipToPadding(false);
        }
        float sourceX = data.getCoordinateX() + (data.getWidth() - width) / 2f - view.getX();
        float sourceY = data.getCoordinateY() + (data.getHeight() - height) / 2f - view.getY();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "scaleX", 1f * data.getWidth() / view.getWidth(), 1);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleY", 1f * data.getHeight() / view.getHeight(), 1);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "translationX", sourceX, 0);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(view, "translationY", sourceY, 0);
        Interpolator interpolator = new OvershootInterpolator(1.1f);
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(interpolator);
        set.play(animator3).with(animator4).with(animator1).with(animator2);
        set.setDuration(ANIM_DURATION);
        set.start();
    }

    /**
     * TODO 所有的非目标view渐变出现，从rootView遍历
     *
     * @param view targetView
     */
    private void startWindowAnim(final View view) {
        Window window = ((Activity) view.getContext()).getWindow();
        View parent = window.getDecorView().findViewById(android.R.id.content);
        if (parent instanceof ViewGroup) {
            if (((ViewGroup) parent).getChildCount() > 0) {
                View realParent = ((ViewGroup) parent).getChildAt(0);
                for (int i = 0; i < ((ViewGroup) realParent).getChildCount(); i++) {
                    View child = ((ViewGroup) realParent).getChildAt(i);
                    if (child != view) {
                        child.setAlpha(0);
                        ObjectAnimator animatorChild = ObjectAnimator.ofFloat(child, "alpha", 0, 1);
                        animatorChild.setDuration(ANIM_DURATION);
                        animatorChild.setStartDelay(ANIM_DURATION);
                        animatorChild.start();
                    }
                }
            }
        } else {
            Log.e(getClass().getName(), "" + parent.getClass().getName());
        }
    }

    @TargetApi(16)
    private void removeGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener) {
        observer.removeOnGlobalLayoutListener(listener);
    }

    private static class SharedElementData implements Serializable {
        int width;
        int height;
        float coordinateX;
        float coordinateY;

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

        public float getCoordinateX() {
            return coordinateX;
        }

        public void setCoordinateX(float coordinateX) {
            this.coordinateX = coordinateX;
        }

        public float getCoordinateY() {
            return coordinateY;
        }

        public void setCoordinateY(float coordinateY) {
            this.coordinateY = coordinateY;
        }
    }
}
