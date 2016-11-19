package views.zeffect.cn.scrawlviewlib.panel;

import java.io.Console;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * The SketchPadView class provides method to draw strokes on it like as a
 * canvas or sketch pad. We use touch event to draw strokes, when user touch
 * down and touch move, we will remember these point of touch move and set them
 * to a Path object, then draw the Path object, so that user can see the strokes
 * are drawing real time. When touch up event is occurring, we draw the path to
 * a bitmap which is hold by a canvas, and then draw the bitmap to canvas to
 * display these strokes to user.
 *
 * @author Li Hong
 * @date 2010/07/30
 */
public class SketchPadView extends View {
    /**
     * Need to track this so the dirty region can accommodate the stroke.
     **/
    private float HALF_STROKE_WIDTH = 0;
    private int m_strokeColor = Color.BLACK;
    private int m_penSize = 2;
    private int m_eraserSize = 10;
    private PenType mPenType = PenType.Pen;


    public enum PenType {
        Pen, Eraser;
    }

    private Paint m_bitmapPaint = null;
    private Bitmap m_foreBitmap = null;
    private Canvas m_canvas = null;
    private ISketchPadTool m_curTool = null;

    public SketchPadView(Context context) {
        this(context, null);
    }

    public SketchPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SketchPadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public void setStrokeType(PenType type) {
        if (type == PenType.Pen) {
            m_curTool = new SketchPadPen(m_penSize, m_strokeColor);
        } else if (type == PenType.Eraser) {
            m_curTool = new SketchPadEraser(m_eraserSize);
        }
        mPenType = type;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = eventX;
                lastTouchY = eventY;
                setStrokeType(mPenType);
                m_curTool.touchDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {//取当前点和下一点作贝塞乐曲线
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    m_curTool.touchMove(historicalX, historicalY);
                    if (mPenType == PenType.Eraser) {
                        m_curTool.draw(m_canvas);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                m_curTool.touchUp(event.getX(), event.getY());
                m_curTool.draw(m_canvas);
                break;
        }
        invalidate(
                (int) (dirtyRect.left - HALF_STROKE_WIDTH),
                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

        lastTouchX = eventX;
        lastTouchY = eventY;
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null != m_foreBitmap) {
            canvas.drawBitmap(m_foreBitmap, 0, 0, m_bitmapPaint);
        }
        if (mPenType != PenType.Eraser) {
            m_curTool.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createStrokeBitmap(w, h);
    }

    protected void initialize() {
        m_canvas = new Canvas();
        m_bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setStrokeType(PenType.Pen);
        HALF_STROKE_WIDTH = m_penSize / 2;
    }

    protected void createStrokeBitmap(int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h,
                Bitmap.Config.ARGB_8888);
        if (null != bitmap) {
            m_foreBitmap = bitmap;
            m_canvas.setBitmap(m_foreBitmap);
        }
    }

    /**
     * Optimizes painting by invalidating the smallest possible area.
     */
    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF();

    /**
     * Called when replaying history to ensure the dirty region includes all
     * points.
     */
    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }
        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    /**
     * Resets the dirty region when the motion event occurs.
     */
    private void resetDirtyRect(float eventX, float eventY) {
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }
}
