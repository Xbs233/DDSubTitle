package top.bilibililike.subtitle.subtitle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import top.bilibililike.subtitle.utils.Utils;


/**
 * @author Xbs
 * @date 2020年1月15日19:15:14
 */
public class SubtitleView extends View {
    private static final String TAG = SubtitleView.class.getSimpleName();
    final private TextPaint firstTextPaint;
    final private TextPaint secondTextPaint;
    private long timeFlag;
    final private Paint backgroundPaint;
    final private StringBuilder newSubTitleStr;
    final private StringBuilder oldSubTitleStr;

    private int sumWidth;
    private int sumHeight;

    final private float firstTextSize = Utils.sp2px(24);
    final private float secondTextSize = Utils.sp2px(18);

    final private float alpha = 0.7f;

    final private int textMarging = 5;

    final RectF rectF;
    final RectF firstRectF;
    final RectF secondRectF;

    private Disposable disposable;

    public SubtitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        firstTextPaint = new TextPaint();
        firstTextPaint.setColor(Color.WHITE);
        firstTextPaint.setAntiAlias(true);
        firstTextPaint.setTextSize(firstTextSize);
        firstTextPaint.setTextAlign(Paint.Align.CENTER);
        secondTextPaint = new TextPaint();
        secondTextPaint.setColor(Color.WHITE);
        secondTextPaint.setAntiAlias(true);
        secondTextPaint.setTextSize(secondTextSize);
        secondTextPaint.setTextAlign(Paint.Align.CENTER);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#1F0F0F"));
        backgroundPaint.setAlpha((int) (alpha * 255));
        newSubTitleStr = new StringBuilder();
        oldSubTitleStr = new StringBuilder();
        rectF = new RectF();
        firstRectF = new RectF();
        secondRectF = new RectF();


    }

    private void initTimeTask() {
        timeFlag = System.currentTimeMillis();
        disposable = Observable
                .interval(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(s -> {
                    Log.d(TAG, "onSubscribe");
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aLong -> {
                    Log.d(TAG, "onNext timeFlag = " + timeFlag);
                    if (System.currentTimeMillis() - timeFlag > 20000) {
                        setVisibility(View.INVISIBLE);
                        disposable.dispose();
                    }
                })
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (newSubTitleStr.length() == 0 && oldSubTitleStr.length() == 0) {
            setVisibility(View.INVISIBLE);
            return;
        }

        //清空canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //矩形宽度计算
        final float firstRectWidth = newSubTitleStr.length() * firstTextSize;
        final float secondRectWidth = oldSubTitleStr.length() * secondTextSize;
        //字体起始x坐标计算
        final float firstTextWidth = (float) ((sumWidth - Math.pow(firstRectWidth, 0.5)) / 2);
        final float secondTextWidth = (float) ((sumWidth - Math.pow(secondRectWidth, 0.5)) / 2);

        //top默认为0
        firstRectF.left = (rectF.right - firstRectWidth) / 2 - textMarging * 4;
        firstRectF.right = firstRectF.left + firstRectWidth + textMarging * 4;
        firstRectF.bottom = firstTextSize + textMarging * 2;
        canvas.drawRect(firstRectF, backgroundPaint);

        secondRectF.top = firstRectF.bottom;
        secondRectF.left = (rectF.right - secondRectWidth) / 2 - textMarging * 4;
        secondRectF.right = secondRectF.left + secondRectWidth + textMarging * 4;
        secondRectF.bottom = secondRectF.top + secondTextSize + textMarging * 2;
        canvas.drawRect(secondRectF, backgroundPaint);

        //画文字
        canvas.drawText(newSubTitleStr.toString(), firstTextWidth, firstTextSize - textMarging, firstTextPaint);
        canvas.drawText(oldSubTitleStr.toString(), secondTextWidth, firstTextSize + secondTextSize + textMarging, secondTextPaint);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        rectF.bottom = bottom / 2;
        rectF.left = left;
        rectF.right = right;
        rectF.top = top;
        Log.d(TAG, "bottom = " + bottom + "\ntop = " + top + "\nleft = " + left + "\nright = " + right);
        firstRectF.right = right;
        secondRectF.right = right;
        super.onLayout(changed, left, top, right, bottom);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        sumWidth = MeasureSpec.getSize(widthMeasureSpec);
        sumHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(sumWidth, sumHeight);
    }

    public void addSubtitle(String subTitleStr) {
        timeFlag = System.currentTimeMillis();
        setVisibility(View.VISIBLE);
        if (disposable == null || disposable.isDisposed()) {
            disposable = null;
            initTimeTask();
        }
        if (oldSubTitleStr.length() != 0) {
            oldSubTitleStr.setLength(0);
        }
        oldSubTitleStr.append(" ");
        oldSubTitleStr.append(newSubTitleStr);
        if (newSubTitleStr.length() != 0) {
            newSubTitleStr.setLength(0);
        }
        newSubTitleStr.append(" ");
        newSubTitleStr.append(subTitleStr);

        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (!disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        if (disposable == null || disposable.isDisposed()) {
            disposable = null;
            initTimeTask();
        }
        super.onAttachedToWindow();
    }


}
