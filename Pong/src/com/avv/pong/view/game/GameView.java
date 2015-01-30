package com.avv.pong.view.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.avv.pong.R;

/**
 * Clase que representa el terreno de juego
 * 
 * @author angelvazquez
 * 
 */
public class GameView extends View implements OnTouchListener,
		OnCompletionListener {

	private static final int PADDLE_THICKNESS = 25;
	private static final int PADDLE_WIDTH = 80;
	private static final int PADDING = 60;
	private static final int BALL_RADIUS = 12;
	private Point mBallPosition;

	private State mCurrentState = State.Starting;
	private State mLastState = State.Paused;

	public static enum State {
		Running, Paused, Score, Finished, Starting
	}

	private final Paint mPaint = new Paint();

	private final RefreshHandler mRedrawHandler = new RefreshHandler();

	private double mBallAngle = 135;
	private final Rect mPlayer1 = new Rect();
	private final Rect mPlayer2 = new Rect();

	// Superficies que reciben eventos tactiles
	private Rect mPlayer1Box;
	private Rect mPlayer2Box;
	private Rect mPauseTouchBox;

	private int mPlayer1LastTouch;
	private int mPlayer2LastTouch;

	private int mFramesPerSecond = 30;
	private int mFrameSkips = 10;
	private boolean onePlayerGame = true;
	private boolean twoPlayerGame = false;

	// Controles posibles
	public static final int TOUCH_MODE = 0;
	public static final int SENSOR_MODE = 1;

	// Parametros defecto
	private int mBallSpeed = 8;
	private int mPaddleSpeed = this.mBallSpeed - 2;
	private int scorePlayer1 = 3;
	private int scorePlayer2 = 3;
	private int control = TOUCH_MODE;
	public boolean nextRound;
	private long lastUpdateTime;

	// Los sonidos
	private MediaPlayer mPaddleHit;
	private MediaPlayer mMissedBallTone;
	private MediaPlayer mWallHit;
	private MediaPlayer mEndGameTone;

	/**
	 * Handler que envina mensajes constantemente y cada cierto tiempo y que
	 * hace que se refresque el pintado
	 * 
	 */
	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			GameView.this.updateSituation();
			GameView.this.invalidate();
		}

		public void sleep(long delay) {
			this.removeMessages(0);
			this.sendMessageDelayed(this.obtainMessage(0), delay);
		}
	}

	public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.initGameView();
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.initGameView();
	}

	public GameView(Context context) {
		super(context);
		this.initGameView();
	}

	/**
	 * Cuando cambia el tamaño y se estabiliza ya tenemos la medida real del
	 * ancho y alto del campo de juego
	 * 
	 * @param w
	 * @param h
	 * @param oldw
	 * @param oldh
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.initPaddles();
		this.updateSituation();
	}

	/**
	 * Llamado por el constructor se registra como listener de eventos táctiles
	 * e inicializa los sonidos del juego
	 */
	private void initGameView() {
		this.setOnTouchListener(this);
		this.mWallHit = this.loadSound(R.raw.wall);
		this.mPaddleHit = this.loadSound(R.raw.paddle);
		this.mMissedBallTone = this.loadSound(R.raw.ballmiss);
		this.mEndGameTone = this.loadSound(R.raw.endgametone);
	}

	/**
	 * Se construyen los rectángulos que son los mandos de cada jugador y las
	 * superficies que reciben los eventos táctiles
	 */
	private void initPaddles() {
		this.mPlayer1.top = PADDING;
		this.mPlayer1.bottom = PADDING + PADDLE_THICKNESS;

		this.mPlayer2.top = this.getHeight() - PADDING - PADDLE_THICKNESS;
		this.mPlayer2.bottom = this.getHeight() - PADDING;

		this.mPlayer2.left = this.mPlayer1.left = (this.getWidth() / 2)
				- PADDLE_WIDTH;
		this.mPlayer2.right = this.mPlayer1.right = (this.getWidth() / 2)
				+ PADDLE_WIDTH;

		this.mPlayer1Box = new Rect(0, 0, this.getWidth(), this.getHeight() / 8);
		this.mPlayer2Box = new Rect(0, (7 * this.getHeight()) / 8,
				this.getWidth(), this.getHeight());

		int min = Math.min(this.getWidth() / 4, this.getHeight() / 4);
		int xmid = this.getWidth() / 2;
		int ymid = this.getHeight() / 2;
		this.mPauseTouchBox = new Rect(xmid - min, ymid - min, xmid + min, ymid
				+ min);

		this.mPlayer1LastTouch = (this.getWidth() / 2);
		this.mPlayer2LastTouch = (this.getWidth() / 2);
	}

	/**
	 * Comprueba si la bola está en los límites de las x
	 * 
	 * @return
	 */
	private boolean isInLimitX() {
		return ((this.mBallPosition.getX() - BALL_RADIUS) >= 0)
				&& ((this.mBallPosition.getX() + BALL_RADIUS) <= this
						.getWidth());
	}

	/**
	 * Comprueba si la bola está en los límites de las y
	 * 
	 * @return
	 */
	private boolean isInLimitY() {
		boolean limitY = ((this.mBallPosition.getY() - BALL_RADIUS) >= 0)
				&& ((this.mBallPosition.getY() + BALL_RADIUS) <= this
						.getHeight());
		if (!limitY) {
			if (this.onePlayerGame) {
				if (!((this.mBallPosition.getY() - BALL_RADIUS) >= 0)) {
					this.mLastState = this.mCurrentState;
					this.mCurrentState = State.Score;
					this.scorePlayer1 = this.scorePlayer1 - 1;
					this.playSound(this.mMissedBallTone);
				}
			} else {
				if (this.twoPlayerGame) {
					this.mLastState = this.mCurrentState;
					this.mCurrentState = State.Score;
					if (!((this.mBallPosition.getY() - BALL_RADIUS) >= 0)) {
						if (this.scorePlayer1 > 0) {
							this.scorePlayer1 = this.scorePlayer1 - 1;
							this.playSound(this.mMissedBallTone);
						}
					}
					if (!((this.mBallPosition.getY() + BALL_RADIUS) <= this
							.getHeight())) {
						if (this.scorePlayer2 > 0) {
							this.scorePlayer2 = this.scorePlayer2 - 1;
							this.playSound(this.mMissedBallTone);
						}
					}
				}
			}
		}
		return limitY;
	}

	/**
	 * Calcula la posición de la bola y si rebota contra las paredes, los
	 * rectángulos
	 */
	private void calculateBallPosition() {
		if (this.mBallPosition == null) {
			this.mBallPosition = new Point(this.getWidth() / 2,
					this.getHeight() / 2);
		}

		double posX = this.mBallPosition.getX();
		double posY = this.mBallPosition.getY();

		if (!this.isInLimitX()) {
			this.bounceBallVertical();
		}

		this.mBallPosition.set(
				posX
						+ (this.mBallSpeed * Math
								.cos((this.mBallAngle * Math.PI) / 180.)),
				posY
						+ (this.mBallSpeed * Math
								.sin((this.mBallAngle * Math.PI) / 180.)));

		if (!this.isInLimitY()) {
			this.bounceBallHorizontal();
		}

		if (this.isGameFinished()) {
			this.playSound(this.mEndGameTone);
		}
	}

	/**
	 * Rebota contra los muros
	 */
	private void bounceBallVertical() {
		this.mBallAngle = (540 - this.mBallAngle) % 360;
		this.playSound(this.mWallHit);
	}

	/**
	 * Rebota contra los rectángulos
	 */
	private void bounceBallHorizontal() {
		this.mBallAngle = (360 - this.mBallAngle) % 360;
		this.playSound(this.mPaddleHit);
	}

	/**
	 * Conocer si rebotará
	 * 
	 * @param r
	 *            el rectángulo
	 * @return si choca contra el rectángulo
	 */
	private boolean ballCollides(Rect r) {
		int x = this.mBallPosition.getX();
		int y = this.mBallPosition.getY();
		return (x >= r.left) && (x <= r.right) && (y >= (r.top - BALL_RADIUS))
				&& (y <= (r.bottom + BALL_RADIUS));
	}

	/**
	 * Cuando llega el mensaje al handler se llama a este método para actualizar
	 * la situación del juego
	 */
	public void updateSituation() {

		if (this.mCurrentState == State.Running) {

			long now = System.currentTimeMillis();
			this.calculateBallPosition();
			if (this.mPlayer1LastTouch > 0) {
				this.movePaddleTowards(this.mPlayer1, 8 * this.mPaddleSpeed,
						this.mPlayer1LastTouch);
			}
			if (this.mPlayer2LastTouch > 0) {
				this.movePaddleTowards(this.mPlayer2, 8 * this.mPaddleSpeed,
						this.mPlayer2LastTouch);
			}
			if ((this.mBallAngle >= 180) && this.ballCollides(this.mPlayer1)) {
				this.bounceBallHorizontal();
				this.increaseDifficulty();
			} else if ((this.mBallAngle < 180)
					&& this.ballCollides(this.mPlayer2)) {
				this.bounceBallHorizontal();
				this.increaseDifficulty();
			}
			long diff = System.currentTimeMillis() - now;
			long inter = (1000 / this.mFramesPerSecond) - diff;
			long delay = Math.max(0, inter);
			Log.d("diff", diff + "");
			Log.d("inter", inter + "");
			Log.d("refresh delay", delay + "");
			this.mRedrawHandler.sleep(delay);
		} else {
			this.mRedrawHandler.sleep((1000 / this.mFramesPerSecond));
		}
	}

	/**
	 * Se actualizará más frecuentemente cada vez que rebote contra los
	 * rectángulos haciendolo más difíciles
	 */
	private void increaseDifficulty() {
		if (this.mFramesPerSecond < 150) {
			this.mFramesPerSecond += this.mFrameSkips;
			this.mFrameSkips++;
		}
	}

	/**
	 * Desplaza el rectángulo hacia una coordenada x
	 * 
	 * @param r
	 *            el rectángulo a mover
	 * @param speed
	 *            velocidad de desplazamiento
	 * @param x
	 *            hacia donde se mueve (horizontalmente)
	 */
	private void movePaddleTowards(Rect r, int speed, float x) {
		int dx = (int) Math.abs(r.centerX() - x);

		if (x < r.centerX()) {
			r.offset((dx > speed) ? -speed : -dx, 0);
		} else if (x > r.centerX()) {
			r.offset((dx > speed) ? speed : dx, 0);
		}
	}

	/*
	 * La principal función de la vista, dibuja todos los elementos según la
	 * situación del juego
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (this.mCurrentState == State.Starting) {
			return;
		}
		this.updateScoreBoard(canvas);

		if (this.isGameFinished()) {
			this.announceWinner(canvas);
			this.mLastState = this.mCurrentState;
			this.mCurrentState = State.Finished;
			return;
		}

		if (this.nextRound) {
			this.restartNextRound();
			return;
		}

		this.drawBallInPosition(canvas);
		this.drawPaddles(canvas);

		if (this.mCurrentState == State.Paused) {
			this.announcePause(canvas);
		} else {
			if (this.mCurrentState == State.Score) {
				this.announceNumericScoreBoard(canvas);
			}
		}
	}

	/**
	 * Se muestra el marcador
	 * 
	 * @param canvas
	 */
	private void announceNumericScoreBoard(Canvas canvas) {
		if (this.twoPlayerGame) {
			this.mPaint.setColor(Color.RED);
			this.mPaint.setStyle(Style.STROKE);
			float strokeWidth = this.mPaint.getStrokeWidth();
			this.mPaint.setStrokeWidth(14);
			canvas.drawRect(this.mPauseTouchBox, this.mPaint);

			this.mPaint.setStrokeWidth(strokeWidth);
			this.mPaint.setTextSize(100);

			float mSeparator = this.mPaint.measureText("-");
			float xCoord = (this.getWidth() / 2) - (mSeparator / 2);
			canvas.drawText("-", xCoord, (this.getHeight() / 2), this.mPaint);

			this.mPaint.setColor(Color.GREEN);
			float mScore1 = this.mPaint.measureText(String
					.valueOf(this.scorePlayer1));
			canvas.drawText(String.valueOf(this.scorePlayer1), xCoord
					- (mScore1 + mSeparator), (this.getHeight() / 2),
					this.mPaint);

			this.mPaint.setColor(Color.RED);
			float mScore2 = this.mPaint.measureText(String
					.valueOf(this.scorePlayer2));
			canvas.drawText(String.valueOf(this.scorePlayer2),
					xCoord + mScore2, (this.getHeight() / 2), this.mPaint);
		} else {
			this.mPaint.setColor(Color.GREEN);
			float mScore1 = this.mPaint.measureText(String
					.valueOf(this.scorePlayer1));
			float xCoord = (this.getWidth() / 2) - (mScore1 / 2);
			canvas.drawText(String.valueOf(this.scorePlayer1), xCoord,
					(this.getHeight() / 2), this.mPaint);
		}
	}

	/**
	 * Se muestra el mensaje de pausa en el juego
	 * 
	 * @param canvas
	 */
	private void announcePause(Canvas canvas) {
		String pause = "Pausa";
		int pausew = (int) this.mPaint.measureText(pause);

		this.mPaint.setColor(Color.GREEN);
		this.mPaint.setStyle(Style.STROKE);
		canvas.drawRect(this.mPauseTouchBox, this.mPaint);
		canvas.drawText(pause, (this.getWidth() / 2) - (pausew / 2),
				this.getHeight() / 2, this.mPaint);
	}

	/**
	 * Dibuja los rectángulos
	 * 
	 * @param canvas
	 */
	private void drawPaddles(Canvas canvas) {
		this.mPaint.setStyle(Style.FILL);
		this.mPaint.setColor(Color.GREEN);
		canvas.drawRect(this.mPlayer1, this.mPaint);

		if (this.twoPlayerGame) {
			this.mPaint.setStyle(Style.FILL);
			this.mPaint.setColor(Color.RED);
			canvas.drawRect(this.mPlayer2, this.mPaint);
		}
	}

	/**
	 * Dibuja las vidas restantes de cada jugador
	 * 
	 * @param canvas
	 */
	private void updateScoreBoard(Canvas canvas) {

		this.mPaint.setColor(Color.WHITE);
		this.mPaint.setTextSize(100);
		for (int i = 0; i < this.scorePlayer1; i++) {
			canvas.drawCircle(this.getWidth() - (30 * (i + 1)), 20, 12,
					this.mPaint);
		}
		if (this.twoPlayerGame) {
			this.mPaint.setColor(Color.WHITE);
			this.mPaint.setTextSize(100);
			for (int i = 0; i < this.scorePlayer2; i++) {
				this.mPaint.setColor(Color.WHITE);
				this.mPaint.setTextSize(100);
				canvas.drawCircle(this.getWidth() - (30 * (i + 1)),
						this.getHeight() - 20, 12, this.mPaint);
			}
		}
	}

	/**
	 * Dibuja la bola en la posición dentro del terreno de juego
	 * 
	 * @param canvas
	 */
	private void drawBallInPosition(Canvas canvas) {
		this.mPaint.setStyle(Style.FILL);
		this.mPaint.setColor(Color.BLUE);
		canvas.drawCircle(this.mBallPosition.getX(), this.mBallPosition.getY(),
				BALL_RADIUS, this.mPaint);
	}

	/**
	 * Nueva ronda
	 */
	private void restartNextRound() {
		this.initPaddles();
		this.mBallPosition = new Point(this.getWidth() / 2,
				this.getHeight() / 2);
		this.nextRound = false;
	}

	/**
	 * Anuncia el ganador
	 * 
	 * @param canvas
	 */
	private void announceWinner(Canvas canvas) {
		this.mPaint.setColor(Color.GREEN);
		String winner = "Green";
		if (this.scorePlayer1 == 0) {
			this.mPaint.setColor(Color.RED);
			winner = "Red";
		}
		int playerTextMs = (int) this.mPaint.measureText(winner);
		this.mPaint.setStyle(Style.STROKE);
		canvas.drawText(winner, (this.getWidth() / 2) - (playerTextMs / 2),
				this.getHeight() / 2, this.mPaint);
		int winnerTextMs = (int) this.mPaint.measureText("wins");
		canvas.drawText("wins", (this.getWidth() / 2) - (winnerTextMs / 2),
				(this.getHeight() / 2) + 100, this.mPaint);

		this.mPaint.setStrokeWidth(10);
		canvas.drawRect(this.mPauseTouchBox, this.mPaint);
	}

	/**
	 * Comprueba si el juego ha terminado, si las vidas han llegado a 0
	 * 
	 * @return
	 */
	private boolean isGameFinished() {
		return (this.scorePlayer1 == 0) || (this.scorePlayer2 == 0);
	}

	/**
	 * Clase para representar los puntos dentro del campo de juego donde puede
	 * estar la bola
	 * 
	 */
	class Point {
		private int x, y;

		Point() {
			this.x = 0;
			this.y = 0;
		}

		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public void set(double d, double e) {
			this.x = (int) d;
			this.y = (int) e;
		}

		public void translate(int i, int j) {
			this.x += i;
			this.y += j;
		}

		@Override
		public String toString() {
			return "Point: (" + this.x + ", " + this.y + ")";
		}
	}

	/**
	 * Los eventos táctiles se leen aquí
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (this.mCurrentState == State.Starting) {
			return true;
		}
		Log.d(this.getClass().getName(), "Touch events");
		for (int i = 0; i < event.getPointerCount(); i++) {
			int tx = (int) event.getX(i);
			int ty = (int) event.getY(i);

			if (this.mPlayer1Box.contains(tx, ty)) {
				this.mPlayer1LastTouch = tx;
			} else if (this.mPlayer2Box.contains(tx, ty)) {
				this.mPlayer2LastTouch = tx;
			} else if ((event.getAction() == MotionEvent.ACTION_DOWN)
					&& this.mPauseTouchBox.contains(tx, ty)) {
				if (this.mCurrentState == State.Running) {
					this.mLastState = this.mCurrentState;
					this.mCurrentState = State.Paused;
				} else {
					if (this.mCurrentState == State.Score) {
						this.mLastState = State.Score;
						this.mCurrentState = State.Running;
						this.nextRound = true;
					} else {
						if (this.mCurrentState == State.Finished) {
							this.scorePlayer1 = 3;
							this.scorePlayer2 = 3;
							this.nextRound = true;
							this.mCurrentState = State.Running;
							this.mLastState = State.Finished;
						} else {
							this.mCurrentState = this.mLastState;
							this.mLastState = State.Paused;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Se inicia un nuevo juego
	 * 
	 * @param nPlayer
	 *            numero de jugadores
	 * @param lifes
	 *            numero de vidas
	 * @param difficulty
	 *            dificultad
	 * @param control
	 *            control tactil o sensor
	 */
	public void startNewGame(int nPlayer, int lifes, int difficulty, int control) {
		this.onePlayerGame = true;
		this.twoPlayerGame = false;
		if (nPlayer > 1) {
			this.onePlayerGame = false;
			this.twoPlayerGame = true;
		}
		this.nextRound = true;
		this.mCurrentState = State.Running;

		this.mBallSpeed = difficulty;
		this.mPaddleSpeed = this.mBallSpeed - 2;
		this.scorePlayer1 = lifes;
		this.scorePlayer2 = lifes;
		this.control = control;

		this.initPaddles();
		this.updateSituation();
	}

	/**
	 * Mueve un porcentaje del ancho en función de las lecturas del sensor
	 * 
	 * @param movX
	 *            lectura del sensor
	 */
	public void sensorMovement(float movX) {
		if ((this.mCurrentState == State.Starting)
				|| (this.control == TOUCH_MODE)) {
			return;
		}
		if (this.lastUpdateTime > 0) {
			long now = System.currentTimeMillis();
			long ellapse = now - this.lastUpdateTime;
			if (ellapse > 100) {
				if ((movX < this.getWidth()) && (movX > 0)) {
					double m = movX * this.getWidth() * 0.1;
					Log.d("sensor touch simulated", String.valueOf(m));
					this.mPlayer1LastTouch = new Double(m).intValue();
				}
				this.lastUpdateTime = now;
			}
		} else {
			this.lastUpdateTime = System.currentTimeMillis();
		}
	}

	/**
	 * Se liberan recursos
	 */
	public void releaseResources() {
		this.mPaddleHit.release();
		this.mMissedBallTone.release();
		this.mWallHit.release();
		this.mEndGameTone.release();
	}

	/**
	 * Se cargan los sonidos a utilizar
	 * 
	 * @param rid
	 * @return
	 */
	private MediaPlayer loadSound(int rid) {
		MediaPlayer mp = MediaPlayer.create(this.getContext(), rid);
		mp.setOnCompletionListener(this);
		return mp;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.seekTo(0);
	}

	/**
	 * Se reproduce el sonido elegido
	 * 
	 * @param mp
	 */
	private void playSound(MediaPlayer mp) {

		if (!mp.isPlaying()) {
			mp.setVolume(0.2f, 0.2f);
			mp.start();
		}
	}

	public void onResume() {
		// this.mCurrentState = State.Paused;
	}

	public void onPause() {
		if (this.mCurrentState != State.Starting) {
			this.mCurrentState = State.Paused;
		}
	}
}
