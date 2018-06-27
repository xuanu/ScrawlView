package views.zeffect.cn.scrawlview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;

import views.zeffect.cn.scrawlviewlib.panel.SketchPadView;

public class MainActivity extends Activity {
    SketchPadView mSketchPadView;
    private JSONArray noteArray;

    private View hideLeft, hideTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSketchPadView = (SketchPadView) findViewById(R.id.scrawlview);
        hideLeft = findViewById(R.id.hideLeft);
        hideTop = findViewById(R.id.hideTop);
        Button tButton1 = (Button) findViewById(R.id.pen);
        Button tButton2 = (Button) findViewById(R.id.eraser);
        tButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSketchPadView.setStrokeType(SketchPadView.PenType.Pen);
            }
        });
        tButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSketchPadView.setStrokeType(SketchPadView.PenType.Eraser);
            }
        });
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSketchPadView.clear();
            }
        });
        findViewById(R.id.saveNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    noteArray = mSketchPadView.line2String();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.drawNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSketchPadView.drawLine(SketchPadView.string2Line(noteArray), true);
            }
        });
        findViewById(R.id.changeSizeW).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLeft.setVisibility(hideLeft.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
        findViewById(R.id.changeSizeH).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTop.setVisibility(hideTop.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
    }
}
