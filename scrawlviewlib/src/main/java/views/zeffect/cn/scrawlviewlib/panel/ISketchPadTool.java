package views.zeffect.cn.scrawlviewlib.panel;


import android.graphics.Canvas;

public interface ISketchPadTool
{
    public void draw(Canvas canvas);
    public void cleanAll();
    public void touchDown(float x, float y);
    public void touchMove(float x, float y);
    public void touchUp(float x, float y);
}
