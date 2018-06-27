package views.zeffect.cn.scrawlviewlib.panel.line;

import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import views.zeffect.cn.scrawlviewlib.panel.DrawConstant;

/**
 * Created by zeffect on 2016/12/26.
 *
 * @author zzx
 */

public class ViewPoint {
    /**
     * X坐标
     */
    private float x;
    /**
     * Y坐标
     */
    private float y;

    public ViewPoint(float x, float y) {
        if (x < 0) x = 0;
        this.x = x;
        if (y < 0) y = 0;
        this.y = y;
    }

    public float getX() {
        return x;
    }


    public float getY() {
        return y;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject dataJson = new JSONObject();
        dataJson.put(DrawConstant.X, x);
        dataJson.put(DrawConstant.Y, y);
        return dataJson;
    }


    public static ViewPoint toPoint(JSONObject dataJson) {
        if (dataJson == null) dataJson = new JSONObject();
        return new ViewPoint(dataJson.optInt(DrawConstant.X), dataJson.optInt(DrawConstant.Y));
    }

}
