package com.bresan.glass.examples;

import com.bresan.glass.examples.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CardMenuActivity extends Activity {

	private final Handler mHandler = new Handler();

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		openOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.card_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.stop:
			post(new Runnable() {

				@Override
				public void run() {
					stopService(new Intent(CardMenuActivity.this, LiveCardService.class)); // stop the service
				}
			});
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		finish();
	}

	protected void post(Runnable runnable) {
		mHandler.post(runnable);
	}

}
