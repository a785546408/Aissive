package CustomView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.color;
import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

public class MyView extends View {  
	private final static String X_KEY = "Xpos";  
	private final static String Y_KEY = "Ypos"; 
	private int count;
	public float XX;    //原点的X坐标
	public float YY;     //原点的Y坐标	
	public int XScale=55;     //X的刻度长庿
	public int YScale=40;     //Y的刻度长庿
	public int XLength=380;       //X轴的长度
	public int YLength=240;       //Y轴的长度

	public int Max=0;
	public int Min=0;
	
	public Path pathMax;
	public Path pathMin;
	

	WindowManager wm = (WindowManager) getContext()
			.getSystemService(Context.WINDOW_SERVICE);

	int width = wm.getDefaultDisplay().getWidth();
	int height = wm.getDefaultDisplay().getHeight();

	public List Xlist=new ArrayList();

	public int mX;

	private List<Map<String, Integer>> mListPoint = new ArrayList<Map<String,Integer>>();  

	Paint mPaint = new Paint();  
	Paint nPaint = new Paint();
	Paint LPaint = new Paint();


	public MyView(Context context, AttributeSet attrs, int defStyle) {  
		super(context, attrs, defStyle);  
		// TODO Auto-generated constructor stub  
	}  


	public MyView(Context context, AttributeSet attrs) {  
		super(context, attrs);  
		// TODO Auto-generated constructor stub  
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					if(mListPoint.size()>count-1){
						mListPoint.remove(0);
					}
				}
			}
		});
	}  

	public MyView(Context context) {  
		super(context);  
		// TODO Auto-generated constructor stub  
	}  

	@Override  
	protected void onDraw(Canvas canvas) {  
		// TODO Auto-generated method stub  
		super.onDraw(canvas);  

		//确定三种画笔，一种画直线，一种画圆，丿种画虚线
		mPaint.setColor(Color.BLACK); 
		nPaint.setColor(Color.WHITE);
		LPaint.setColor(Color.BLACK);

		mPaint.setAntiAlias(true); 
		nPaint.setAntiAlias(true);
		LPaint.setAntiAlias(true);

		LPaint.setTextSize(25);
		mPaint.setTextSize(15);

		mPaint.setStrokeWidth((float) 1.5);
		nPaint.setStrokeWidth((float) 1.5);
		LPaint.setStyle(Paint.Style.STROKE);

		canvas.drawLine(XX+50, YY+480, XX+50, YY+30, mPaint);
		canvas.drawLine(XX+50, YY+480, XX+width, YY+480, mPaint);
		canvas.drawText("Tm", XX+width-100, YY+515, mPaint);
		canvas.drawText("P", XX+25, YY+15, mPaint);



		if (mListPoint.size()>0) {


			Max = mListPoint.get(0).get(Y_KEY);
			Min = mListPoint.get(0).get(Y_KEY);
		}

		for (int index = 0; index<mListPoint.size(); index++)  

		{  //只能记录当前20分钟内的朿大最小忿
			if (mListPoint.get(index).get(Y_KEY) < Max) {
				Max = mListPoint.get(index).get(Y_KEY);
			}
			if (mListPoint.get(index).get(Y_KEY) > Min) {
				Min = mListPoint.get(index).get(Y_KEY);
			}

			if(index<count+1){
				Xlist.add(new Integer(index*60));
			}
			if (index>0)  
			{  



				canvas.drawLine(Float.parseFloat(Xlist.get(index-1)+"")+50, mListPoint.get(index-1).get(Y_KEY),  
						Float.parseFloat(Xlist.get(index)+"")+50, mListPoint.get(index).get(Y_KEY), mPaint);  
				canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
				if (index > 1) {



					canvas.drawCircle(Float.parseFloat(Xlist.get(index-1)+"")+50, mListPoint.get(index-1).get(Y_KEY), (float)4.5, mPaint);
					canvas.drawCircle(Float.parseFloat(Xlist.get(index-1)+"")+50, mListPoint.get(index-1).get(Y_KEY), (float)3, nPaint);



				}
				canvas.drawCircle(Float.parseFloat(Xlist.get(index)+"")+50, mListPoint.get(index).get(Y_KEY), (float)4.5, mPaint);
				canvas.drawCircle(Float.parseFloat(Xlist.get(index)+"")+50, mListPoint.get(index).get(Y_KEY), (float)3, nPaint);
			}
			if(index>=0)
			{
				canvas.drawText((int)Float.parseFloat(mListPoint.get(index).get(X_KEY)+"")/120+"",Float.parseFloat(Xlist.get(index)+"")+45, 515,LPaint);
			}

		}



		pathMax = new Path();
		pathMax.moveTo(XX+50, Max);
		pathMax.lineTo(XX+width, Max);

		pathMin = new Path();
		pathMin.moveTo(XX+50, Min);
		pathMin.lineTo(XX+width, Min);

		PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
		LPaint.setPathEffect(effects);

		canvas.drawPath(pathMax, LPaint);
		canvas.drawPath(pathMin, LPaint);
		canvas.drawText((480-Max)/10+"", XX+25, Max, LPaint);
		canvas.drawText((480-Min)/10+"", XX+25, Min, LPaint);

		if(Xlist != null){
			Xlist.clear();
		}
	}  
	/** 
	 * @param curX  which x position you want to draw. 
	 * @param curY  which y position you want to draw. 
	 * @see all you put x-y position will connect to a line. 
	 */  
	public void setLinePoint(List<Map<String, Integer>> mListPoint, float XX, float YY,int count)  
	{  
		this.XX = XX;
		this.YY = YY;
		this.mListPoint = mListPoint;
		this.count = count;
		invalidate();  
	}  
	
	public void clearLine(){
		Xlist.clear();
		mListPoint.clear();
		pathMax.reset();
		pathMin.reset();
		invalidate();  
		
		
	}
}  