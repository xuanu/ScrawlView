package views.zeffect.cn.scrawlviewlib.panel.draw;


import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

public interface ISketchPadTool {
    public void draw(Canvas canvas);

    public void touchDown(float x, float y);

    public void touchMove(float x, float y);

    public void touchUp(float x, float y);

    public void drawToastCircle(Canvas pCanvas, float x, float y);

    public int getColor();

    public int getTextSize();

    public int getType();

    public void drawPath(Canvas pCanvas, Path pPath);

    public void drawRect(Canvas pCanvas, Rect pRect);

    public void clear();
}
