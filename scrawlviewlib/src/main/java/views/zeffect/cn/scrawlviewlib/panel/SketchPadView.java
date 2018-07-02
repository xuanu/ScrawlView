package views.zeffect.cn.scrawlviewlib.panel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import views.zeffect.cn.scrawlviewlib.panel.draw.ISketchPadTool;
import views.zeffect.cn.scrawlviewlib.panel.draw.SketchPadEraser;
import views.zeffect.cn.scrawlviewlib.panel.draw.SketchPadPen;
import views.zeffect.cn.scrawlviewlib.panel.line.Line;
import views.zeffect.cn.scrawlviewlib.panel.line.ViewPoint;


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
    private double HALF_STROKE_WIDTH = 0;
    private PenType mPenType = PenType.Pen;


    public enum PenType {
        Pen, Eraser
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

    protected void initialize() {
        m_canvas = new Canvas();
        m_bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        m_penSize = dp2px(3);
        m_eraserSize = dp2px(20);
        HALF_STROKE_WIDTH = m_penSize / 2;
        setStrokeType(PenType.Pen);
    }

    private int m_penSize;
    private int m_eraserSize;
    private int penColor = Color.BLUE;

    public void setStrokeType(PenType type) {
        if (type == PenType.Pen) {
            m_curTool = new SketchPadPen(m_penSize, penColor);
        } else if (type == PenType.Eraser) {
            m_curTool = new SketchPadEraser(m_eraserSize);
        }
        mPenType = type;
    }


    private List<Line> fingerLines = new LinkedList<>();

    private Line singleLine;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isTouchUp = false;
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //存线
                singleLine = new Line(new ViewPoint(getMeasuredWidth(), getMeasuredHeight()), m_curTool.getType(), m_curTool.getTextSize(), m_curTool.getColor());
                singleLine.getPoints().add(new ViewPoint(event.getX(), event.getY()));
                //
                lastTouchX = eventX;
                lastTouchY = eventY;
                resetDirtyRect(eventX, eventY);
                setStrokeType(mPenType);
                HALF_STROKE_WIDTH = Math.floor(m_curTool.getTextSize() * 1f / 2);
                m_curTool.touchDown(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {//取当前点和下一点作贝塞乐曲线
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    singleLine.getPoints().add(new ViewPoint(historicalX, historicalY));
                    expandDirtyRect(historicalX, historicalY);
                    m_curTool.touchMove(historicalX, historicalY);
                }
                m_curTool.touchMove(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                int upHistorySize = event.getHistorySize();
                for (int i = 0; i < upHistorySize; i++) {//取当前点和下一点作贝塞乐曲线
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    singleLine.getPoints().add(new ViewPoint(historicalX, historicalY));
                    expandDirtyRect(historicalX, historicalY);
                    m_curTool.touchMove(historicalX, historicalY);
                }
                //存线
                singleLine.getPoints().add(new ViewPoint(event.getX(), event.getY()));
                fingerLines.add(singleLine);
                //
                isTouchUp = true;
                m_curTool.touchUp(event.getX(), event.getY());
                m_curTool.draw(m_canvas);
                break;
        }
        invalidate(
                (int) (dirtyRect.left - HALF_STROKE_WIDTH),
                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
//        invalidate();
        lastTouchX = eventX;
        lastTouchY = eventY;
        return true;
    }

    private boolean isTouchUp = false;

    @Override
    protected void onDraw(Canvas canvas) {
        if (null != m_foreBitmap) {
            canvas.drawBitmap(m_foreBitmap, 0, 0, m_bitmapPaint);
        }
        if (mPenType == PenType.Eraser) {
            if (!isTouchUp) {
                m_curTool.drawToastCircle(canvas, lastTouchX, lastTouchY);
                m_curTool.draw(m_canvas);
            }
        } else {
            if (!isTouchUp) m_curTool.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) createStrokeBitmap(w, h);
    }


    protected void createStrokeBitmap(int w, int h) {
        Bitmap bitmap = Bitmap.createBitmap(w, h,
                Bitmap.Config.ARGB_8888);
        if (null != bitmap) {
            if (m_canvas != null) {
                Bitmap mFreeBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
                m_canvas.setBitmap(mFreeBitmap);
                m_canvas = null;
                mFreeBitmap.recycle();
                mFreeBitmap = null;
            }
            if (m_foreBitmap != null) {
                if (!m_foreBitmap.isRecycled()) m_foreBitmap.recycle();
                m_foreBitmap = null;
            }
            m_foreBitmap = bitmap;
            if (m_canvas == null) m_canvas = new Canvas();
            m_canvas.setBitmap(m_foreBitmap);
            drawLine(fingerLines);
        }
    }


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


    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }


    /**
     * 清空
     */
    public void clear() {
        fingerLines.clear();
        m_curTool.clear();
        SketchPadEraser tempEraser = new SketchPadEraser(m_eraserSize);
        tempEraser.drawRect(m_canvas, new Rect(0, 0, getWidth(), getHeight()));
        invalidate();
    }


    public void drawLine(List<Line> lines) {
        drawLine(lines, false);
    }

    public void drawLine(List<Line> lines, boolean append) {
        if (lines == null) return;
        if (append) fingerLines.addAll(lines);
        for (Line pLine : lines) {
            ISketchPadTool tempTool;
            ViewPoint viewSizePoint = pLine.getViewSize();
            int size = (int) ((getMeasuredHeight() * pLine.getWidth() * 1f) / viewSizePoint.getY());
            int color = pLine.getColor();
            if (pLine.getType() == Line.LINE_PEN) {
                tempTool = new SketchPadPen(size, color);
            } else if (pLine.getType() == Line.LINE_EARSER) {
                tempTool = new SketchPadEraser(size);
            } else {
                tempTool = new SketchPadPen(size, color);
            }
            Path tempPath = new Path();
            float mX = 0;
            float mY = 0;
            for (int i = 0; i < pLine.getPoints().size() - 1; i++) {
                ViewPoint tempPoint = pLine.getPoints().get(i);
                if (i == 0) {
                    mX = toViewAxisX(viewSizePoint, tempPoint.getX());
                    mY = toViewAxisY(viewSizePoint, tempPoint.getY());
                    tempPath.moveTo(mX, mY);
                } else {
                    float previousX = mX;
                    float previousY = mY;
                    ViewPoint nextPoint = pLine.getPoints().get(i + 1);
                    //设置贝塞尔曲线的操作点为起点和终点的一半
                    float cX = (mX + toViewAxisX(viewSizePoint, nextPoint.getX())) / 2;
                    float cY = (mY + toViewAxisY(viewSizePoint, nextPoint.getY())) / 2;
                    tempPath.quadTo(previousX, previousY, cX, cY);
                    mX = toViewAxisX(viewSizePoint, nextPoint.getX());
                    mY = toViewAxisY(viewSizePoint, nextPoint.getY());
                }
            }
            tempTool.drawPath(m_canvas, tempPath);
            invalidate();
        }
    }

    public float toViewAxisX(ViewPoint viewPoint, float pX) {
        return (pX * getMeasuredWidth() * 1f) / viewPoint.getX();
    }

    public float toViewAxisY(ViewPoint viewPoint, float pY) {
        return (pY * getMeasuredHeight() * 1f) / viewPoint.getY();
    }

    public JSONArray line2String() throws JSONException {
        return line2String(fingerLines);
    }


    public static JSONArray line2String(List<Line> lines) throws JSONException {
        if (lines == null) lines = new LinkedList<>();
        JSONArray dataArray = new JSONArray();
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            dataArray.put(line.toJson());
        }
        return dataArray;
    }


    public static List<Line> string2Line(JSONArray dataArray) {
        if (dataArray == null) return Collections.emptyList();
        List<Line> lines = new LinkedList<>();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject lineJson = dataArray.optJSONObject(i);
            if (lineJson == null) continue;
            lines.add(Line.toLine(lineJson));
        }
        return lines;
    }

}
