package com.gp.draw;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by guoping on 2017/5/8.
 */

public class AnimationButton extends View{

    private int width;

    private int height;

    //圆角半径
    private int circleAngle;

    private RectF rectf = new RectF();

    private int two_circle_distance;

    private Paint paint;

    private Paint textPaint;

    private int bg_color = 0xffbc7d53;

    private ValueAnimator animator_rect_to_angle;

    private int duration = 1000;

    private int default_two_circle_distance;

    private ValueAnimator animator_rect_to_circle;

    private ObjectAnimator animator_move_to_up;

    private int move_distance = 300;

    private Rect textRect = new Rect();

    private String buttonString = "确认完成";

    private AnimatorSet animatorSet = new AnimatorSet();

    private ValueAnimator animator_draw_ok;

    /**
     * 是否开始绘制对勾
     */
    private boolean startDrawOk = false;

    /**
     * 取路径的长度
     */
    private PathMeasure pathMeasure;

    /**
     * 对路径处理实现绘制动画效果
     */
    private PathEffect effect;

    /**
     * 对勾（√）画笔
     */
    private Paint okPaint;

    /**
     * 矩形到正方形过度的动画
     */
    private ValueAnimator animator_rect_to_square;

    /**
     * 路径--用来获取对勾的路径
     */
    private Path path = new Path();

    private AnimationButtonListener animationButtonListener;

    public void setAnimationButtonListener(AnimationButtonListener listener) {
        animationButtonListener = listener;
    }


    public AnimationButton(Context context) {
        this(context, null);
    }

    public AnimationButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (animationButtonListener != null) {
                    animationButtonListener.onClickListener();
                }
            }
        });

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animationButtonListener != null) {
                    animationButtonListener.animationFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public AnimationButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        this(context, attrs, defStyleAttr);
//    }

    private void initPaint(){
        paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(bg_color);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(40);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        okPaint = new Paint();
        okPaint.setStrokeWidth(10);
        okPaint.setStyle(Paint.Style.STROKE);
        okPaint.setAntiAlias(true);
        okPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        default_two_circle_distance = (w - h) / 2;

        initOk();
        initAnimation();
    }

    private void initAnimation(){
        set_rect_to_angle_animation();
        set_rect_to_circle_animation();
        set_move_to_up_animation();
        set_draw_ok_animation();

        animatorSet
                .play(animator_move_to_up)
                .before(animator_draw_ok)
                .after(animator_rect_to_square)
                .after(animator_rect_to_angle);
    }

    private void draw_oval_to_circle(Canvas canvas){
        rectf.left = two_circle_distance;
        rectf.top = 0;
        rectf.right = width - two_circle_distance;
        rectf.bottom = height;
        canvas.drawRoundRect(rectf,circleAngle,circleAngle,paint);
    }

    /**
     * 绘制文字
     *
     * @param canvas 画布
     */
    private void drawText(Canvas canvas) {
        textRect.left = 0;
        textRect.top = 0;
        textRect.right = width;
        textRect.bottom = height;
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (textRect.bottom + textRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        //文字绘制到整个布局的中心位置
        canvas.drawText(buttonString, textRect.centerX(), baseline, textPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draw_oval_to_circle(canvas);
        drawText(canvas);

        if (startDrawOk) {
            canvas.drawPath(path, okPaint);
        }
    }

    /**
     * 设置矩形过度到圆角矩形的动画
     */
    private void set_rect_to_angle_animation() {
        animator_rect_to_angle = ValueAnimator.ofInt(0, height / 2);
        animator_rect_to_angle.setDuration(duration);
        animator_rect_to_angle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleAngle = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    /**
     * 设置圆角矩形过度到圆的动画
     */
    private void set_rect_to_circle_animation() {
        animator_rect_to_circle = ValueAnimator.ofInt(0, default_two_circle_distance);
        animator_rect_to_circle.setDuration(duration);
        animator_rect_to_circle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                two_circle_distance = (int) animation.getAnimatedValue();

                int alpha = 255 - (two_circle_distance * 255) / default_two_circle_distance;

                textPaint.setAlpha(alpha);

                invalidate();
            }
        });
    }

    /**
     * 设置view上移的动画
     */
    private void set_move_to_up_animation() {
        final float curTranslationY = this.getTranslationY();
        animator_move_to_up = ObjectAnimator.ofFloat(this, "translationY", curTranslationY, curTranslationY - move_distance);
        animator_move_to_up.setDuration(duration);
        animator_move_to_up.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    /**
     * 绘制对勾的动画
     */
    private void set_draw_ok_animation() {
        animator_draw_ok = ValueAnimator.ofFloat(1, 0);
        animator_draw_ok.setDuration(duration);
        animator_draw_ok.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startDrawOk = true;
                float value = (Float) animation.getAnimatedValue();

                effect = new DashPathEffect(new float[]{pathMeasure.getLength(), pathMeasure.getLength()}, value * pathMeasure.getLength());
                okPaint.setPathEffect(effect);
                invalidate();
            }
        });
    }

    /**
     * 绘制对勾
     */
    private void initOk() {
        //对勾的路径
        path.moveTo(default_two_circle_distance + height / 8 * 3, height / 2);
        path.lineTo(default_two_circle_distance + height / 2, height / 5 * 3);
        path.lineTo(default_two_circle_distance + height / 3 * 2, height / 5 * 2);

        pathMeasure = new PathMeasure(path, true);
    }

    /**
     * 启动动画
     */
    public void start() {
        animatorSet.start();
    }


    /**
     * 动画还原
     */
    public void reset() {
        startDrawOk = false;
        circleAngle = 0;
        two_circle_distance = 0;
        default_two_circle_distance = (width - height) / 2;
        textPaint.setAlpha(255);
        setTranslationY(getTranslationY() + move_distance);
        invalidate();
    }

    public interface AnimationButtonListener {
        /**
         * 按钮点击事件
         */
        void onClickListener();

        /**
         * 动画完成回调
         */
        void animationFinish();
    }
}
