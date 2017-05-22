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

package org.openmrs.mobile.activities.visit.visitphoto;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.visit.VisitContract;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitPhoto;
import org.openmrs.mobile.utilities.DateUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitPhotoRecyclerViewAdapter
		extends RecyclerView.Adapter<VisitPhotoRecyclerViewAdapter.DownloadVisitPhotoViewHolder> {

	private Activity context;
	private VisitContract.VisitPhotoView view;
	private List<VisitPhoto> visitPhotos;
	private Map<ImageView, VisitPhoto> map = new HashMap<>();

	public VisitPhotoRecyclerViewAdapter(Activity context,
			List<VisitPhoto> visitPhotos, VisitContract.VisitPhotoView view) {
		this.context = context;
		this.visitPhotos = visitPhotos;
		this.view = view;
	}

	@Override
	public DownloadVisitPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.visit_photo_row, parent, false);
		return new DownloadVisitPhotoViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(DownloadVisitPhotoViewHolder holder, int position) {
		VisitPhoto visitPhoto = visitPhotos.get(position);
		if(visitPhoto == null){
			return;
		}

		view.downloadImage(visitPhoto.getObservation().getUuid(), new DataService.GetCallback<Bitmap>() {
			@Override
			public void onCompleted(Bitmap entity) {
				visitPhoto.setDownloadedImage(entity);
				holder.image.setImageBitmap(entity);
				holder.image.invalidate();
				map.put(holder.image, visitPhoto);
			}

			@Override
			public void onError(Throwable t) {
				holder.image.setVisibility(View.GONE);
			}
		});

		holder.image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View imageView) {
				if (map.containsKey(imageView)) {
					VisitPhoto visitPhoto = map.get(imageView);
					Dialog expandImageDialog = new Dialog(context);
					RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1000, 800);
					layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

					LinearLayout linearLayout = new LinearLayout(context);
					linearLayout.setOrientation(LinearLayout.VERTICAL);

					ImageView expandImage = new ImageView(context);
					expandImage.setLayoutParams(layoutParams);
					expandImage.setImageBitmap(visitPhoto.getDownloadedImage());

					TextView descriptionView = new TextView(context);
					descriptionView.setText(view.formatVisitImageDescription(visitPhoto.getFileCaption(),
							DateUtils.convertTime(visitPhoto.getDateCreated()
									.getTime()),
							visitPhoto.getCreator().getPerson().getDisplay()));
					descriptionView.setPadding(10, 10, 10, 10);

					linearLayout.addView(descriptionView);
					linearLayout.addView(expandImage);

					expandImageDialog.addContentView(linearLayout, layoutParams);
					expandImageDialog.show();
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return visitPhotos.size();
	}

	class DownloadVisitPhotoViewHolder extends RecyclerView.ViewHolder {
		private LinearLayout rowLayout;
		private ImageView image;

		public DownloadVisitPhotoViewHolder(View itemView) {
			super(itemView);
			rowLayout = (LinearLayout)itemView;
			image = (ImageView)itemView.findViewById(R.id.visitPhoto);
		}
	}
}