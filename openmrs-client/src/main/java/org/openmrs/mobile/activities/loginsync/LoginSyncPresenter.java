package org.openmrs.mobile.activities.loginsync;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.activities.patientlist.PatientListActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.event.SyncPullEvent;
import org.openmrs.mobile.event.SyncPushEvent;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class LoginSyncPresenter extends BasePresenter implements LoginSyncContract.Presenter {

	private LoginSyncContract.View view;
	private OpenMRS openMRS;

	private int totalSyncPushes;
	private int totalSyncPulls;
	private List<String> entitiesPushed;
	private List<String> entitiesPulled;
	private String currentDownloadingSubscription;
	private double averageNetworkSpeed;
	private Timer networkConnectivityCheckTimer;
	private int networkDurationMessageId = R.string.download_upload_speed_is_slow;
	private boolean arePushing = false;
	private boolean arePulling = false;

	private final int DELAY = 500;
	private final double SMOOTHING_FACTOR = 0.005;

	public LoginSyncPresenter(LoginSyncContract.View view, OpenMRS openMRS) {
		this.view = view;
		this.openMRS = openMRS;

		this.view.setPresenter(this);
		entitiesPulled = new ArrayList<>();
		entitiesPushed = new ArrayList<>();

		openMRS.requestDataSync();
	}

	@Override
	public void subscribe() {
		//intentionally left blank
	}

	public void onSyncPushEvent(SyncPushEvent syncPushEvent) {
		arePushing = true;
		String pullDisplayInformation = new String();
		double progress = 0;

		// Handle getting total amount for push
		// Handle getting incremental amount for push
		// Handle getting push complete
		arePushing = false;

		view.updateSyncPushProgress(progress, pullDisplayInformation, networkDurationMessageId);
	}

	public void onSyncPullEvent(SyncPullEvent syncPullEvent) {
		arePushing = true;
		String pullDisplayInformation = new String();
		double progress = 0;

		if (syncPullEvent.message.equals(ApplicationConstants.EventMessages.Sync.Pull.TOTAL_SUBSCRIPTIONS)) {
			totalSyncPushes = syncPullEvent.totalItems;
		} else if (syncPullEvent.message.equals(
				ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_STARTING)) {
			pullDisplayInformation = "Downloading " + syncPullEvent.entity + "...";
			currentDownloadingSubscription = syncPullEvent.entity;
		} else if (syncPullEvent.message.equals(
				ApplicationConstants.EventMessages.Sync.Pull.SUBSCRIPTION_REMOTE_PULL_COMPLETE)) {
			entitiesPulled.add(syncPullEvent.entity);
			pullDisplayInformation = "Downloading " + syncPullEvent.entity + " complete!";
		} else if (syncPullEvent.message.equals(
				ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_STARTING)) {
			pullDisplayInformation = "Downloading " + currentDownloadingSubscription + " - " + syncPullEvent.entity + "...";
		} else if (syncPullEvent.message.equals(
				ApplicationConstants.EventMessages.Sync.Pull.ENTITY_REMOTE_PULL_COMPLETE)) {
			pullDisplayInformation = "Downloading " + currentDownloadingSubscription + " - " + syncPullEvent.entity
					+ " complete!";
		}

		if (totalSyncPulls == entitiesPulled.size()) {
			progress = 100;
			pullDisplayInformation = "Download complete!";
			entitiesPulled = new ArrayList<>();
			arePulling = false;
		} else {
			progress = (double) entitiesPulled.size() / (double) totalSyncPushes;
		}

		view.updateSyncPullProgress(progress, pullDisplayInformation, networkDurationMessageId);

		if (progress == 100) {
			Intent intent = new Intent(openMRS.getApplicationContext(), PatientListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			openMRS.getApplicationContext().startActivity(intent);
			view.finish();
		}
	}

	public void startMeasuringConnectivity() {
		averageNetworkSpeed = 0;
		if (networkConnectivityCheckTimer == null) {
			networkConnectivityCheckTimer = new Timer();
		}
		networkConnectivityCheckTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				calculateNewAverageNetworkSpeed();
				int previousMessageId = networkDurationMessageId;
				updateDurationMessage();
				if (previousMessageId == networkDurationMessageId) {
					view.getParentActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							updateDurationDisplayText();
						}
					});
				}
			}
		}, DELAY, DELAY);
	}

	private void updateDurationMessage() {
		networkDurationMessageId = R.string.download_upload_speed_is_slow;
		// TODO: Implement logic to change message ID based on averageNetworkSpeed
	}

	private void updateDurationDisplayText() {
		if (arePushing) {
			view.updateSyncPushDuration(networkDurationMessageId);
		}
		if (arePulling) {
			view.updateSyncPullDuration(networkDurationMessageId);
		}
	}

	public void stopMeasuringConnectivity() {
		networkConnectivityCheckTimer.cancel();
	}

	private void calculateNewAverageNetworkSpeed() {
		double networkSpeed = (double) openMRS.getNetworkUtils().getCurrentConnectionSpeed();
		averageNetworkSpeed = SMOOTHING_FACTOR * networkSpeed + (1 - SMOOTHING_FACTOR) * averageNetworkSpeed;
	}
}
