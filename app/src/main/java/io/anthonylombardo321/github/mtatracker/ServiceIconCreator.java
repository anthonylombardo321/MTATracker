package io.anthonylombardo321.github.mtatracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class ServiceIconCreator {
    private Context context;
    private String text;
    private String textColor;
    private String backgroundColor;
    private int dimension;
    private String size;

    public ServiceIconCreator(Context context){
        this.context = context;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Bitmap createServiceIcon() {
        Bitmap output = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paintCircle = new Paint();
        Paint paintText = new Paint();
        Rect rect = new Rect(0, 0, dimension, dimension);
        RectF rectF = new RectF(rect);
        float density = context.getResources().getDisplayMetrics().density;
        float roundPx = 100*density;

        if(backgroundColor.equalsIgnoreCase("Red")){
            paintCircle.setColor(Color.parseColor("#FF0000"));
        }
        if(backgroundColor.equalsIgnoreCase("Blue")){
            paintCircle.setColor(Color.parseColor("#2E78BF"));
        }
        if(backgroundColor.equalsIgnoreCase("Green")){
            paintCircle.setColor(Color.parseColor("#008000"));
        }
        if(backgroundColor.equalsIgnoreCase("Yellow")){
            paintCircle.setColor(Color.parseColor("#FFD700"));
        }
        if(backgroundColor.equalsIgnoreCase("GreenYellow")){
            paintCircle.setColor(Color.parseColor("#ADFF2F"));
        }
        if(backgroundColor.equalsIgnoreCase("Purple")){
            paintCircle.setColor(Color.parseColor("#800080"));
        }
        if(backgroundColor.equalsIgnoreCase("Orange")){
            paintCircle.setColor(Color.parseColor("#FFA500"));
        }
        if(backgroundColor.equalsIgnoreCase("Brown")){
            paintCircle.setColor(Color.parseColor("#70432A"));
        }
        if(backgroundColor.equalsIgnoreCase("Gray")){
            paintCircle.setColor(Color.parseColor("#C0C0C0"));
        }
        if(backgroundColor.equalsIgnoreCase("Black")){
            paintCircle.setColor(Color.parseColor("#000000"));
        }

        paintCircle.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        // Fills Background Color For Circle
        paintCircle.setStyle(Paint.Style.FILL);
        paintCircle.setStrokeWidth(4.0f);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paintCircle);
        if(textColor.equalsIgnoreCase("Black")){
            paintText.setColor(Color.parseColor("#000000"));
        }
        if(textColor.equalsIgnoreCase("White")){
            paintText.setColor(Color.parseColor("#FFFFFF"));
        }
        int textLength = text.length();
        if(size.equalsIgnoreCase("Small")) {
            paintText.setTextSize(60);
            if (textLength == 3) {
                paintText.setTextSize(40);
            }
            if (textLength == 4) {
                paintText.setTextSize(30);
            }
            if(textLength == 5){
                paintText.setTextSize(20);
            }
        }
        else{
            paintText.setTextSize(75);
            if (textLength == 3) {
                paintText.setTextSize(60);
            }
            if (textLength == 4) {
                paintText.setTextSize(45);
            }
            if (textLength == 5) {
                paintText.setTextSize(40);
            }
        }
        paintText.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, canvas.getWidth()/2, ((dimension / 2) - ((paintText.descent() + paintText.ascent()) / 2)), paintText);

        return output;
    }
}
