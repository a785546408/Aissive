package CustomView;
 
import android.content.Context;  
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.graphics.Rect;  
import android.graphics.Paint.Align;  
import android.graphics.Paint.FontMetrics;  
import android.util.AttributeSet;  
import android.view.View;  

public class HorizontalBarView extends View {  

	public HorizontalBarView(Context context) {  
		super(context);  
		// TODO Auto-generated constructor stub  

		init(context, null);  
	}  

	public HorizontalBarView(Context context, AttributeSet attrs) {  
		super(context, attrs);  
		// TODO Auto-generated constructor stub  
		init(context, attrs);  
	}  

	//  ������ ���� ���ʣ�  
	private Paint axisLinePaint;  
	//  �����ı��Ļ���  
	private Paint titlePaint;  
	//  ���λ��� ��״ͼ����ʽ��Ϣ  
	private Paint recPaint;  

	private int count = 1;
	private void init(Context context, AttributeSet attrs)  
	{  

		axisLinePaint = new Paint();    
		titlePaint = new Paint();  
		recPaint = new Paint();  


		axisLinePaint.setColor(Color.DKGRAY);  
		titlePaint.setColor(Color.BLACK);  
		titlePaint.setTextSize(30);

	}  

	//7 ��  
	private int[] data1;  
	private int[] data2;  
	private String[] describe;


	/** 
	 * ������������� ��ҪView�����ػ档 
	 *  
	 * ���߳� ˢ�¿ؼ���ʱ����ã� 
	 * this.invalidate();  ʧЧ����˼�� 
	 * this.postInvalidate();  �������߳� ������ͼ�ķ������á� 
	 *  
	 * */  
	//updata this year data  
	public void updateData1(int[] thisData)  
	{  
		data1 = thisData;  
		  
		this.postInvalidate();  //�������߳� ������ͼ�ķ������á�  
		
	}  


	public void updateData2(int[] lastData)  
	{  
		data2 = lastData;  
		//      this.invalidate(); //ʧЧ����˼��  
		this.postInvalidate();  //�������߳� ������ͼ�ķ������á�  
	}  
	//updata last year data   
	public void updateDescribe(String[] describe)  
	{  
		this.describe = describe;  
		//      this.invalidate(); //ʧЧ����˼��  
		this.postInvalidate();  //�������߳� ������ͼ�ķ������á�  
	}  
	
	public int getMax(int[] data)  
	{  
			int max = 0;
			 for(int i=0;i<data.length;i++)
		        {
		           if(max<data[i])
		              max=data[i];
		            
		        }  
			return max;
	}  
	
	public int compareMax(int max1,int max2){
		if(max1>max2){
			return max1;
		}
		else{
			return max2;
		}
		
	}
	
	public int getMin(int[] data)  
	{  
			int min = 0;
			 for(int i=0;i<=data.length;i++)
		        {
		           if(min<data[i])
		              min=data[i];
		            
		        }
			this.postInvalidate();  //�������߳� ������ͼ�ķ������á�  
			return min;
	} 
	
	public boolean IsEmpty(int[] data){
		if(data == null){
			return true;
		}
		else{
			return false;
		}

	}
	public void setXtitle(String xTitles) {
		this.xTitles = xTitles;  
  
		this.postInvalidate();
		
	}
	public void setYtitle(String yTitles) {
		this.yTitlesStrings = yTitles;  
		
		this.postInvalidate();
		
	}
	private String yTitlesStrings = "Ty";  

	private String xTitles = "Tm"; 
	
	private String  Origin = "0";
	
	private float x;
	private float y;

	@Override  
	protected void onDraw(Canvas canvas) {  
		// TODO Auto-generated method stub  
		super.onDraw(canvas);  
		
		int width = getWidth();  
		int height = getHeight();  

		// 1 ���������ߣ�  
		canvas.drawLine(50, 10,50, 530, axisLinePaint);  

		canvas.drawLine(50, 530, width-30 , 530, axisLinePaint);  

		// 3 ���� Y ������  

		titlePaint.setTextAlign(Align.RIGHT);  

		canvas.drawText(yTitlesStrings, 40, 10, titlePaint);  


		// 4  ���� X �� ������  

		canvas.drawText(xTitles, width-20, 535 , titlePaint);  
		
		// 5  ���� ԭ�� ������  
		
		canvas.drawText(Origin,40,540, titlePaint);  

		// 6 ���ƾ���  

		if(data1 != null && data1.length >0)  
		{  
			int thisCount = data1.length;  
			
			for(int i=0;i<thisCount;i++)  
			{  
				int value = data1[i];
				titlePaint.setColor(Color.BLACK);
				recPaint.setColor(Color.GREEN);  

				Rect rect = new Rect();  
	
				int max1 = getMax(data1);
				
				if(!IsEmpty(data2)){
					int max2 = getMax(data2);
					int max = compareMax(max1,max2);
					int rh = 95; //������Ը߶�
					rect.top = rh*i + 50;  
					rect.bottom =rh*i + 80;//���þ��ο�10  
					rect.left  = 52 ;
					rect.right = (int) (82 + value*width*0.6/max);  
				}
				else {
					int rh = 95; //������Ը߶�
					rect.top = rh*i + 50;  
					rect.bottom =rh*i + 80;//���þ��ο�10  
					rect.left  = 52 ;
					rect.right = (int) (82 + value*width*0.6/max1);  
				}
				

				canvas.drawRect(rect, recPaint);  
				
				canvas.drawText(data1[i]+"ms", rect.right+115, rect.bottom, titlePaint);
				if(IsEmpty(data2)){
					canvas.drawText(describe[i], rect.right+260, rect.bottom, titlePaint);
				}
				else if(data1[i]>data2[i] || data1[i]==data2[i] ){
					canvas.drawText(describe[i], rect.right+260, rect.bottom+5, titlePaint);
				}


			}  
		}  

		if(data2 != null && data2.length >0)  
		{  
			int thisCount = data2.length;  

			for(int i=0;i<thisCount;i++)  
			{   
				int value = data2[i];
				recPaint.setColor(Color.BLUE);  
				titlePaint.setColor(Color.BLACK);

				Rect rect = new Rect();  
				

				int rh = 95; //������Ը߶�

				rect.top = rh*i +82;  
				rect.bottom =rh*i + 112;//���þ��ο�10  
				int max1 = getMax(data2);
				int max2 = getMax(data1);
				int max = compareMax(max1,max2);
				

				rect.left  = 52 ;
				rect.right = (int) (82 + value*width*0.6/max);  
				

				canvas.drawRect(rect, recPaint);  
				canvas.drawText(data2[i]+"ms", rect.right+115, rect.bottom, titlePaint);  
				if(data1[i]<data2[i]){
					canvas.drawText(describe[i], rect.right+260, rect.bottom-5, titlePaint);
				}


			}  
		}  
		


	}  
	public void initial(){
		data1 = null;
		data2 = null;
		describe = null;
	invalidate();  
	}



}  
