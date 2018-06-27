package views.zeffect.cn.scrawlview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;

import views.zeffect.cn.scrawlviewlib.panel.SketchPadView;

public class MainActivity extends AppCompatActivity {
    SketchPadView mSketchPadView;
    private JSONArray noteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSketchPadView = (SketchPadView) findViewById(R.id.scrawlview);
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
    }
}
