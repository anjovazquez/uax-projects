package com.avv.atelesketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class TelesketchView extends View {

	final float METER_TO_PIXEL = 5.0f;

	private Path mPath;
	private Paint mPaint;

	float distanceToEdge = 15;
	float posX = this.distanceToEdge;
	float posY = this.distanceToEdge;

	float speedX = 0;
	float speedY = 0;

	long lastUpdateTime = 0;

	/*
	 * Inicializamos el pincel y el objeto que encapsula los datos del camino
	 * que sigue nuestra línea
	 */
	private void init() {
		this.mPath = new Path();
		this.mPaint = new Paint();

		this.mPaint.setAntiAlias(true);
		this.mPaint.setDither(true);
		this.mPaint.setColor(Color.GREEN);
		this.mPaint.setStyle(Paint.Style.STROKE);
		this.mPaint.setStrokeJoin(Paint.Join.ROUND);
		this.mPaint.setStrokeCap(Paint.Cap.ROUND);
		this.mPaint.setStrokeWidth(30);

	}

	public TelesketchView(Context context) {
		super(context);
		this.init();
	}

	public TelesketchView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init();
	}

	public TelesketchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}

	/*
	 * Pintamos lo que contenga el objeto mPath con el pincel mPaint
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawPath(this.mPath, this.mPaint);
	}

	/*
	 * Borramos lo pintado inicializando el objeto mPath y colocando en el
	 * centro de la imagen el punto
	 */
	public void clear() {
		this.posX = this.getWidth() / 2;
		this.posY = this.getHeight() / 2;
		this.mPath = new Path();
		this.mPath.moveTo(this.posX, this.posY);
		this.invalidate();
	}

	/*
	 * Actualizamos los puntos en el camino seguido
	 */
	public void setData(float x, float y) {

		if (this.lastUpdateTime == 0) {
			this.posX = this.getWidth() / 2;
			this.posY = this.getHeight() / 2;

			this.mPath.moveTo(this.posX, this.posY);

			this.lastUpdateTime = System.currentTimeMillis();
			return;
		}

		long now = System.currentTimeMillis();
		long ellapse = now - this.lastUpdateTime;
		if (ellapse > 100) {
			this.lastUpdateTime = now;

			/*
			 * Las coordenadas se nos proporcionan en m/s2 (unidad de medida de
			 * la aceleración) para conocer la velocidad mutiplicamos por el
			 * tiempo desde la última actualización obtenemos el resultado en
			 * m/s que convertimos a pixeles como tenemos la actividad en
			 * landscape es necesario intercambiar los valores
			 */
			this.speedX += ((y * ellapse) / 1000.0f) * this.METER_TO_PIXEL;
			this.speedY += ((x * ellapse) / 1000.0f) * this.METER_TO_PIXEL;

			/*
			 * Para ver el punto en el que se encuentra ahora dentro del dibujo
			 * multiplicamos por ellapse que es la diferencia con la última
			 * actualización y obtenemos la posición del pixel
			 */
			this.posX += ((this.speedX * ellapse) / 1000.0f);
			this.posY += ((this.speedY * ellapse) / 1000.0f);

			/*
			 * Comprobamos que en el mapeo que hemos hecho no se sale del marco
			 * de nuestra vista
			 */
			if (this.posX < this.distanceToEdge) {
				this.posX = this.distanceToEdge;
				this.speedX = 0;
			} else if (this.posX > (this.getWidth() - this.distanceToEdge)) {
				this.posX = this.getWidth() - this.distanceToEdge;
				this.speedX = 0;
			}

			if (this.posY < this.distanceToEdge) {
				this.posY = this.distanceToEdge;
				this.speedY = 0;
			} else if (this.posY > (this.getHeight() - this.distanceToEdge)) {
				this.posY = this.getHeight() - this.distanceToEdge;
				this.speedY = 0;
			}

			/*
			 * Añadimos un punto a nuestra línea
			 */
			this.mPath.lineTo(this.posX, this.posY);
			this.invalidate();
		}
	}

}
