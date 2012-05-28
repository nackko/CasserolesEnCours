package com.f8full.casserolesencours;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import com.f8full.casserolesencours.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class CasserolesEnCoursViewerActivity extends MapActivity  {
	
	LinearLayout linearLayout;
	MapView mapView;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
    }
	

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
