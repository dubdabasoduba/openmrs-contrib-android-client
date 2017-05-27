/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.auditdata;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.patientheader.PatientHeaderContract;
import org.openmrs.mobile.activities.patientheader.PatientHeaderFragment;
import org.openmrs.mobile.activities.patientheader.PatientHeaderPresenter;
import org.openmrs.mobile.activities.visit.VisitActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.TabUtil;

public class AuditDataActivity extends ACBaseActivity {

	public AuditDataContract.Presenter mPresenter;
	private PatientHeaderContract.Presenter patientHeaderPresenter;
	private String patientUuid;
	private String visitUuid;
	private String providerUuid, visitStopDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLayoutInflater().inflate(R.layout.activity_audit_data, frameLayout);

		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_audit_data_form);
		toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setElevation(0);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}

		// Create fragment
		AuditDataFragment auditDataFragment =
				(AuditDataFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
		if (auditDataFragment == null) {
			auditDataFragment = AuditDataFragment.newInstance();
		}
		if (!auditDataFragment.isActive()) {
			addFragmentToActivity(getSupportFragmentManager(), auditDataFragment, R.id.contentFrame);
		}

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			this.patientUuid = extras.getString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE);
			this.visitUuid = extras.getString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE);
			this.providerUuid = extras.getString(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE);
			this.visitStopDate = extras.getString(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE);

			// patient header
			if (patientHeaderPresenter == null) {
				PatientHeaderFragment headerFragment = (PatientHeaderFragment)getSupportFragmentManager()
						.findFragmentById(R.id.patientHeader);
				if (headerFragment == null) {
					headerFragment = PatientHeaderFragment.newInstance();
				}

				if (!headerFragment.isActive()) {
					addFragmentToActivity(getSupportFragmentManager(), headerFragment, R.id.patientHeader);
				}

				patientHeaderPresenter = new PatientHeaderPresenter(headerFragment, patientUuid);
			}

		}
		mPresenter = new AuditDataPresenter(auditDataFragment);

	}

	@Override
	public void onConfigurationChanged(final Configuration config) {
		super.onConfigurationChanged(config);
		TabUtil.setHasEmbeddedTabs(getSupportActionBar(), getWindowManager(),
				TabUtil.MIN_SCREEN_WIDTH_FOR_VISITDETAILSACTIVITY);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
		outState.putString(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
		outState.putString(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
		outState.putString(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitStopDate);
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				Intent intent = new Intent(getApplicationContext(), VisitActivity.class);
				intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, patientUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_UUID_BUNDLE, visitUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_UUID_BUNDLE, providerUuid);
				intent.putExtra(ApplicationConstants.BundleKeys.VISIT_CLOSED_DATE, visitStopDate);

				//fix for now
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				getApplicationContext().startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
