package com.bresan.glass.examples;

import java.util.Calendar;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.bresan.glass.examples.R;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

public class LiveCardService extends Service {

	private static final String LIVE_CARD_TAG = "LiveCardExample";

	private LiveCard currentLiveCard;
	private RemoteViews liveCardRemote; // Control LiveCard's components

	private final Handler handler = new Handler();
	private final LiveCardUpdater liveCardUpdater = new LiveCardUpdater(); // Runnable to refresh LiveCard
	private static final long REFRESH_CARD_DELAY = 1000; // Delay to refresh LiveCard

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (currentLiveCard == null) {

			currentLiveCard = new LiveCard(this, LIVE_CARD_TAG); // new instance of LiveCard

			liveCardRemote = new RemoteViews(getPackageName(), R.layout.card_layout); // start a RemoteView to control LiveCard components

			Intent menuIntent = new Intent(this, CardMenuActivity.class);
			menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			currentLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

			// Publish the card into user's timeline.
			// Here we use PublishMode.REVEAL, it means the user will be sent to the new card.
			// If you want to add the card without redirecting the user, PublishMode.SILENT should be used.
			currentLiveCard.publish(PublishMode.REVEAL);

			handler.post(liveCardUpdater); // Post into user's timeline
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (currentLiveCard != null && currentLiveCard.isPublished()) {
			liveCardUpdater.setStop(true); // Stop the live card updater

			currentLiveCard.unpublish(); // Remove live card from user's timeline
			currentLiveCard = null;
		}
		super.onDestroy();
	}

	private class LiveCardUpdater implements Runnable {

		private boolean mIsStopped = false;

		public void run() {
			if (!isStopped()) {
				String myDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()); // get current time
				liveCardRemote.setTextViewText(R.id.footer_text, myDate);

				currentLiveCard.setViews(liveCardRemote); // we need to call setViews() to update the live card

				handler.postDelayed(liveCardUpdater, REFRESH_CARD_DELAY); // post after delay
			}
		}

		public boolean isStopped() {
			return mIsStopped;
		}

		public void setStop(boolean isStopped) {
			this.mIsStopped = isStopped;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// If need to interprocess data between service and activity, return a binder object to receive and modify it later
		return null;
	}
}
