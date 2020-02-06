package top.bilibililike.subtitle.subtitle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Looper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import top.bilibililike.subtitle.utils.ToastUtil;
import top.bilibililike.subtitle.utils.Utils;


/**
 * @author Xbs
 * @date 2020年1月15日19:15:14
 */
public class SubTitleView extends View {
    private static final String TAG = SubTitleView.class.getSimpleName();
    final private TextPaint firstTextPaint;
    final private TextPaint secondTextPaint;
    private long timeFlag;
    final private Paint backgroundPaint;
    final private StringBuilder newSubTitleStr;
    final private StringBuilder oldSubTitleStr;

    private int sumWidth ;
    private int sumHeight;

    final private int firstTextSize = 24;
    final private int secondTextSize = 18;

    final private float alpha = 0.7f;

    final private int textMarging = 5;

    final RectF rectF;

    Disposable disposable;

    public SubTitleView(Context context, AttributeSet attrs) {
        super(context,attrs);
        firstTextPaint = new TextPaint();
        firstTextPaint.setColor(Color.WHITE);
        firstTextPaint.setAntiAlias(true);
        firstTextPaint.setTextSize(Utils.sp2px(firstTextSize));
        firstTextPaint.setTextAlign(Paint.Align.CENTER);
        secondTextPaint = new TextPaint();
        secondTextPaint.setColor(Color.WHITE);
        secondTextPaint.setAntiAlias(true);
        secondTextPaint.setTextSize(Utils.sp2px(secondTextSize));
        secondTextPaint.setTextAlign(Paint.Align.CENTER);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#1F0F0F"));
        backgroundPaint.setAlpha((int) (alpha*255));
        newSubTitleStr = new StringBuilder();
        oldSubTitleStr = new StringBuilder();
        rectF = new RectF();



    }

    private void initTimeTask(){
        timeFlag = System.currentTimeMillis();
        disposable = Observable
                .interval(1000,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(s -> {
                    Log.d(TAG,"onSubscribe");
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aLong -> {
                    Log.d(TAG,"onNext timeFlag = " + timeFlag);
                    if (System.currentTimeMillis() - timeFlag > 15000){
                        setVisibility(View.INVISIBLE);
                        ToastUtil.show("暂无同传消息，字幕自动隐藏");
                        disposable.dispose();
                    }
                })
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //清空canvas
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        final float subWidth = (float) ((sumWidth - Math.pow(newSubTitleStr.length() * Utils.sp2px(firstTextSize),0.5) )/2);
        final float sub2Width = (float) ((sumWidth - Math.pow(newSubTitleStr.length() * Utils.sp2px(secondTextSize),0.5) )/2);
        canvas.drawRect(rectF,backgroundPaint);
        //画字幕
        canvas.drawText(newSubTitleStr.toString(),subWidth,Utils.sp2px(firstTextSize),firstTextPaint);
        canvas.drawText(oldSubTitleStr.toString(),sub2Width,Utils.sp2px(firstTextSize+secondTextSize+textMarging),secondTextPaint);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        rectF.bottom = bottom;
        rectF.left = left;
        rectF.right = right;
        rectF.left = left;
        super.onLayout(changed, left, top, right, bottom);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        sumWidth = MeasureSpec.getSize(widthMeasureSpec);
        sumHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(sumWidth,sumHeight);
    }

    public void addSubtitle(String subTitleStr){
        timeFlag = System.currentTimeMillis();
        setVisibility(View.VISIBLE);
        if (disposable == null || disposable.isDisposed()){
            disposable = null;
            initTimeTask();
        }
        if (oldSubTitleStr.length() != 0){
            oldSubTitleStr.setLength(0);
        }
        oldSubTitleStr.append(newSubTitleStr);
        if (newSubTitleStr.length() != 0){
            newSubTitleStr.setLength(0);
        }
        newSubTitleStr.append(subTitleStr);
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (!disposable.isDisposed()){
            disposable.dispose();
            disposable = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        if (disposable == null || disposable.isDisposed()){
            disposable = null;
           initTimeTask();
        }
        super.onAttachedToWindow();
    }


}
