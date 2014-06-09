package edu.dhbw.andobjviewer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import edu.dhbw.andar.ARToolkit;
import edu.dhbw.andar.AndARActivity;
import edu.dhbw.andar.exceptions.AndARException;
import edu.dhbw.andarmodelviewer.R;
import edu.dhbw.andobjviewer.graphics.LightingRenderer;
import edu.dhbw.andobjviewer.graphics.Model3D;
import edu.dhbw.andobjviewer.models.ChessBoard;

public class AugmentedModelViewerActivity extends AndARActivity implements
		SurfaceHolder.Callback {

	public static final boolean DEBUG = false;

	/* Menu Options: */
	private final int MENU_SCALE = 0;
	private final int MENU_ROTATE = 1;
	private final int MENU_TRANSLATE = 2;
	private final int MENU_SCREENSHOT = 3;

	private int mode = MENU_SCALE;

	private Model3D model;
	private ProgressDialog waitDialog;
	private Resources res;

	ARToolkit artoolkit;
	ChessBoard chessBoard;


	public AugmentedModelViewerActivity() {
		super(false);
	}

	/*
	 * (non-Javadoc)
	 * @see edu.dhbw.andar.AndARActivity#onCreate(android.os.Bundle)
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setNonARRenderer(new LightingRenderer());// or might be omited
		res = getResources();
		artoolkit = getArtoolkit();
		getSurfaceView().setOnTouchListener(new TouchEventHandler());
		getSurfaceView().getHolder().addCallback(this);
		chessBoard = new ChessBoard(res);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		System.out.println("");
	}

	/*
	 * create the menu
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_TRANSLATE, 0, res.getText(R.string.translate))
				.setIcon(R.drawable.translate);
		menu.add(0, MENU_ROTATE, 0, res.getText(R.string.rotate)).setIcon(
				R.drawable.rotate);
		menu.add(0, MENU_SCALE, 0, res.getText(R.string.scale)).setIcon(
				R.drawable.scale);
		menu.add(0, MENU_SCREENSHOT, 0, res.getText(R.string.take_screenshot))
				.setIcon(R.drawable.screenshoticon);
		return true;
	}

	/* Handles item selections */
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 * Menu de ferramentas - transalada, rotaciona, aumenta escala, print screen
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SCALE:
			mode = MENU_SCALE;
			return true;
		case MENU_ROTATE:
			mode = MENU_ROTATE;
			return true;
		case MENU_TRANSLATE:
			mode = MENU_TRANSLATE;
			return true;
		case MENU_SCREENSHOT:
			new TakeAsyncScreenshot().execute();
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.dhbw.andar.AndARActivity#surfaceCreated(android.view.SurfaceHolder)
	 * Cria Surface
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		/*
		 * carregamento dos models é feito aqui,
		 * para garantir que a Surface ja foi criada,
		 * então a visualização ja pode ser iniciada.
		 */
		if (model == null) {
			waitDialog = ProgressDialog.show(this, "",
					getResources().getText(R.string.loading), true);
			waitDialog.show();
			new ModelLoader().execute();
		}
	}

	/**
	 * Handles touch events.
	 * 
	 * @author Tobias Domhan
	 * 
	 */
	class TouchEventHandler implements OnTouchListener {

		private float lastX = 0;
		private float lastY = 0;

		/*
		 * Trata de eventos de toque, objetos a serem
		 * rotacionados, alargados. Conforme a opção selecionada.
		 * 
		 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
		 * android.view.MotionEvent)
		 */
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (model != null) {
				switch (event.getAction()) {
				// Action started
				default:
				case MotionEvent.ACTION_DOWN:
					lastX = event.getX();
					lastY = event.getY();
					break;
				// Action ongoing
				case MotionEvent.ACTION_MOVE:
					float dX = lastX - event.getX();
					float dY = lastY - event.getY();
					lastX = event.getX();
					lastY = event.getY();
					if (model != null) {
						switch (mode) {
						case MENU_SCALE:
							model.setScale(dY / 100.0f);
							break;
						case MENU_ROTATE:
							model.setXrot(-1 * dX);// dY-> Rotation um die
													// X-Achse
							model.setYrot(-1 * dY);// dX-> Rotation um die
													// Y-Achse
							break;
						case MENU_TRANSLATE:
							model.setXpos(dY / 10f);
							model.setYpos(dX / 10f);
							break;
						}
					}
					break;
				// Action ended
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					lastX = event.getX();
					lastY = event.getY();
					break;
				}
			}
			return true;
		}

	}

	private class ModelLoader extends AsyncTask<Void, Void, Void> {
		
		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 * Chama método de popular tabuleiro
		 */
		@Override
		protected Void doInBackground(Void... params) {
			chessBoard.populate();
			return null;
		}

		/*
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 * Registra os objetos carregados.
		 */
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			waitDialog.dismiss();

			// register model
			try {
				artoolkit.registerARObject(chessBoard);
				artoolkit.registerARObject(chessBoard.getPieceMarker());
				
			} catch (AndARException e) {
				e.printStackTrace();
			}
			startPreview();
		}
	}

	/*
	 * Em caso de screen shot
	 */
	class TakeAsyncScreenshot extends AsyncTask<Void, Void, Void> {

		private String errorMsg = null;

		@Override
		protected Void doInBackground(Void... params) {
			Bitmap bm = takeScreenshot();
			FileOutputStream fos;
			try {
				fos = new FileOutputStream("/sdcard/AndARScreenshot"
						+ new Date().getTime() + ".png");
				bm.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				errorMsg = e.getMessage();
				e.printStackTrace();
			} catch (IOException e) {
				errorMsg = e.getMessage();
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (errorMsg == null)
				Toast.makeText(AugmentedModelViewerActivity.this,
						getResources().getText(R.string.screenshotsaved),
						Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(
						AugmentedModelViewerActivity.this,
						getResources().getText(R.string.screenshotfailed)
								+ errorMsg, Toast.LENGTH_SHORT).show();
		};

	}

}
