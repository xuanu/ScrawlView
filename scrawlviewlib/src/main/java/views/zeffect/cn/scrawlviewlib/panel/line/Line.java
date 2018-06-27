package views.zeffect.cn.scrawlviewlib.panel.line;

import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import views.zeffect.cn.scrawlviewlib.panel.DrawConstant;

/**
 * Created by zeffect on 2016/12/26.
 *
 * @author zzx
 */

public class Line {

    public static final int LINE_PEN = 0, LINE_EARSER = 1;

    /**
     * 点的集合
     */
    private List<ViewPoint> mPoints = new LinkedList<>();
    /**
     * 线的颜色
     */
    private int color;
    /**
     * 线的宽度
     */
    private int width;
    /**
     * 用来标记是笔还是橡皮
     * 0笔1橡皮
     */
    private int type;

    private ViewPoint viewSize;

    public Line(ViewPoint viewSize, int type, int width, int color) {
        this.viewSize = viewSize;
        this.type = type;
        this.width = width;
        this.color = color;
    }


    public List<ViewPoint> getPoints() {
        if (mPoints == null) mPoints = new LinkedList<>();
        return mPoints;
    }

    public void addPoint(ViewPoint point) {
        getPoints().add(point);
    }

    public int getColor() {
        return color;
    }


    public int getWidth() {
        return width;
    }


    public int getType() {
        return type;
    }

    public ViewPoint getViewSize() {
        if (viewSize == null) viewSize = new ViewPoint(640, 480);
        return viewSize;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject dataJson = new JSONObject();
        dataJson.put(DrawConstant.VIEW_SIZE, viewSize.toJson());
        dataJson.put(DrawConstant.TYPE, type);
        dataJson.put(DrawConstant.COLOR, color);
        dataJson.put(DrawConstant.WIDTH, width);
        JSONArray pointArray = new JSONArray();
        for (int i = 0; i < mPoints.size(); i++) {
            ViewPoint viewPoint = mPoints.get(i);
            pointArray.put(viewPoint.toJson());
        }
        dataJson.put(DrawConstant.POINTS, pointArray);
        return dataJson;
    }

    public static Line toLine(JSONObject dataJson) {
        if (dataJson == null) dataJson = new JSONObject();
        int type = dataJson.optInt(DrawConstant.TYPE);
        int width = dataJson.optInt(DrawConstant.WIDTH, 3);
        int color = dataJson.optInt(DrawConstant.COLOR, Color.BLUE);
        JSONObject viewPointJson = dataJson.optJSONObject(DrawConstant.VIEW_SIZE);
        Line line = new Line(ViewPoint.toPoint(viewPointJson), type, width, color);
        JSONArray pointArray = dataJson.optJSONArray(DrawConstant.POINTS);
        if (pointArray != null) {
            for (int i = 0; i < pointArray.length(); i++) {
                JSONObject pointJson = pointArray.optJSONObject(i);
                if (pointJson == null) continue;
                line.addPoint(ViewPoint.toPoint(pointJson));
            }
        }
        return line;
    }


}
