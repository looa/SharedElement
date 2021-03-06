package org.looa.vision;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * PrismSE - Prism SharedElement
 * <p>
 * Created by ranxiangwei on 2016/12/30.
 */

public class PrismSE implements Animator.AnimatorListener {

    private final static String SHARED_ELEMENT_DATA = "sharedElementData";
    private final static int ANIM_DURATION = 250;

    private boolean isFinishAnim = false;
    private boolean isHasBrother = false;

    private int enterAnim = 0;
    private int exitAnim = 0;

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        isFinishAnim = true;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    private static class Holder {
        static PrismSE INSTANT = new PrismSE();
    }

    public static PrismSE getInstant() {
        return Holder.INSTANT;
    }


    public void startActivity(View sharedElement, Intent intent) {
        startActivity(sharedElement, intent, false, 0);
    }

    public void startActivity(View sharedElement, Intent intent, boolean justSharedImageView) {
        startActivity(sharedElement, intent, justSharedImageView, 0);
    }

    public void startActivity(View sharedElement, Intent intent, int sharedElementPosition) {
        startActivity(sharedElement, intent, true, sharedElementPosition);
    }

    public void startActivity(View sharedElement, Intent intent, boolean justSharedImageView, int sharedElementPosition) {
        initSharedElement(sharedElement, intent, justSharedImageView, sharedElementPosition);
        sharedElement.getContext().startActivity(intent);
        try {
            ((Activity) sharedElement.getContext()).overridePendingTransition(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SharedElementData initSharedElement(View sharedElement) {
        return initSharedElement(sharedElement, null, false, 0);
    }

    public SharedElementData initSharedElement(View sharedElement, boolean justSharedImageView) {
        return initSharedElement(sharedElement, null, true, 0);
    }

    public SharedElementData initSharedElement(View sharedElement, int sharedElementPosition) {
        return initSharedElement(sharedElement, null, false, sharedElementPosition);
    }

    public SharedElementData initSharedElement(View sharedElement, Intent intent) {
        return initSharedElement(sharedElement, intent, false, 0);
    }

    public SharedElementData initSharedElement(View sharedElement, Intent intent, boolean justSharedImageView) {
        return initSharedElement(sharedElement, intent, justSharedImageView, 0);
    }

    public SharedElementData initSharedElement(View sharedElement, Intent intent, int sharedElementPosition) {
        return initSharedElement(sharedElement, intent, true, sharedElementPosition);
    }

    public SharedElementData initSharedElement(View sharedElement, Intent intent, boolean justSharedImageView, int sharedElementPosition) {
        int viewWidth = 0;
        int viewHeight = 0;
        int[] coordinate = new int[2];

        if (!justSharedImageView || !(sharedElement instanceof ViewGroup)) {
            viewWidth = sharedElement.getWidth();
            viewHeight = sharedElement.getHeight();
            sharedElement.getLocationInWindow(coordinate);
        } else {
            int count = 0;
            for (int i = 0; i < ((ViewGroup) sharedElement).getChildCount(); i++) {
                View targetView = ((ViewGroup) sharedElement).getChildAt(i);
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
            if (sharedElementPosition < 0 || sharedElementPosition >= ((ViewGroup) sharedElement).getChildCount()) {
                viewWidth = sharedElement.getWidth();
                viewHeight = sharedElement.getHeight();
                sharedElement.getLocationInWindow(coordinate);
            }
        }

        float coordinateX = coordinate[0];
        float coordinateY = coordinate[1];

        SharedElementData sourceData = new SharedElementData();
        sourceData.setWidth(viewWidth);
        sourceData.setHeight(viewHeight);
        sourceData.setCoordinateX(coordinateX);
        sourceData.setCoordinateY(coordinateY);

        if (intent != null)
            intent.putExtra(SHARED_ELEMENT_DATA, sourceData);

        return sourceData;
    }

    /**
     * matching the shared element.
     *
     * @param view
     */
    public void matchSharedElement(final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeGlobalLayoutListener(view.getViewTreeObserver(), this);
                if (startViewAnim(view, true)) startWindowAnim(view, true);
            }
        });
    }

    public void finish(final View view) {
        if (!isFinishAnim) return;
        boolean isSharedElement = startViewAnim(view, false);
        if (isSharedElement) {
            startWindowAnim(view, false);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((Activity) view.getContext()).finish();
                        ((Activity) view.getContext()).overridePendingTransition(enterAnim, exitAnim);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, ANIM_DURATION);
        } else {
            ((Activity) view.getContext()).finish();
        }
    }

    public void overridePendingTransition(int enterAnim, int exitAnim) {
        this.exitAnim = exitAnim;
        this.enterAnim = enterAnim;
    }

    private boolean startViewAnim(View view, boolean enter) {
        SharedElementData data = (SharedElementData) ((Activity) view.getContext()).getIntent().getSerializableExtra(SHARED_ELEMENT_DATA);
        if (data == null) return false;
        isFinishAnim = false;
        int width = view.getWidth();
        int height = view.getHeight();
        int[] viewCoordinate = new int[2];
        view.getLocationInWindow(viewCoordinate);
        float sourceX = data.getCoordinateX() + (data.getWidth() - width) / 2f - viewCoordinate[0];
        float sourceY = data.getCoordinateY() + (data.getHeight() - height) / 2f - viewCoordinate[1];
        float scaleX = 1f * data.getWidth() / view.getWidth();
        float scaleY = 1f * data.getHeight() / view.getHeight();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "scaleX", enter ? scaleX : 1, enter ? 1 : scaleX);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleY", enter ? scaleY : 1, enter ? 1 : scaleY);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "translationX", enter ? sourceX : 0, enter ? 0 : sourceX);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(view, "translationY", enter ? sourceY : 0, enter ? 0 : sourceY);
        Interpolator interpolator;
        if (enter) interpolator = new OvershootInterpolator(1.1f);
        else interpolator = new AccelerateDecelerateInterpolator();
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(interpolator);
        set.play(animator3).with(animator4).with(animator1).with(animator2);
        set.setDuration(ANIM_DURATION);
        set.start();
        set.addListener(this);
        return true;
    }

    /**
     * @param view targetView
     */
    private void startWindowAnim(final View view, boolean enter) {
        Window window = ((Activity) view.getContext()).getWindow();
        final View parent = window.getDecorView().findViewById(android.R.id.content);
        if (parent instanceof ViewGroup) {
            if (((ViewGroup) parent).getChildCount() > 0) {
                View realParent = ((ViewGroup) parent).getChildAt(0);
                ((ViewGroup) realParent).setClipChildren(false);
                if (!enter) {
                    ObjectAnimator animator = ObjectAnimator.ofInt(realParent, "backgroundColor", Color.parseColor("#ffffff"),
                            Color.parseColor("#00a0a0a0"));
                    animator.setDuration(ANIM_DURATION);
                    animator.setEvaluator(new ArgbEvaluator());
                    animator.start();
                }
                View temp = view;
                isHasBrother = false;
                while (temp.getParent() != null && temp.getParent() instanceof ViewGroup && temp.getParent() != parent) {
                    for (int i = 0; i < ((ViewGroup) temp.getParent()).getChildCount(); i++) {
                        View child = ((ViewGroup) temp.getParent()).getChildAt(i);
                        if (child != temp) {
                            isHasBrother = true;
                            child.setAlpha(enter ? 0 : 1);
                            ObjectAnimator animatorChild = ObjectAnimator.ofFloat(child, "alpha", enter ? 0 : 1, enter ? 1 : 0, enter ? 1 : 0, enter ? 1 : 0);
                            animatorChild.setDuration(ANIM_DURATION);
                            animatorChild.start();
                        }
                    }
                    temp = (ViewGroup) temp.getParent();
                }
                if (!isHasBrother) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isFinishAnim = true;
                        }
                    }, ANIM_DURATION);
                }
            }
        }
    }

    @TargetApi(16)
    private void removeGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener) {
        observer.removeOnGlobalLayoutListener(listener);
    }

    public static class SharedElementData implements Serializable {
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
