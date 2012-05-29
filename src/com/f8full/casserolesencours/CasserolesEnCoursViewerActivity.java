package com.f8full.casserolesencours;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import com.f8full.casserolesencours.R;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.widget.LinearLayout;

public class CasserolesEnCoursViewerActivity extends MapActivity  {
	
	LinearLayout linearLayout;
	MapView mapView;
	
	List<Overlay> mapOverlays;
	Drawable drawable;
	
	private CasserolesItemizedOverlay itemizedoverlay;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer);
        
        ArrayList<String> rowsExtra = getIntent().getStringArrayListExtra("rowsData");
        if(rowsExtra.size() == 0)
        {
        	finish();
        }
        else
        {
        	mapView = (MapView) findViewById(R.id.mapview);
            mapView.setBuiltInZoomControls(true);
            
            mapOverlays = mapView.getOverlays();
            drawable = this.getResources().getDrawable(R.drawable.ic_launcher);
            
                    
            
            
            itemizedoverlay = new CasserolesItemizedOverlay(drawable);        
            
            for (String row : rowsExtra)
            {
            	String[] cells = row.split("\\|");
            	
            	//Location is in second element in the forme of 'Lat Long'
            	String[] latLong = cells[1].split(" ");        	
            	
            	
            	GeoPoint point = new GeoPoint((int)(Double.parseDouble(latLong[0])*1e6),(int)(Double.parseDouble(latLong[1])*1e6));
            	OverlayItem overlayitem = new OverlayItem(point, "", "");      	
            	            
                itemizedoverlay.addOverlay(overlayitem);            
            }
            
            mapOverlays.add(itemizedoverlay);
        	
        }      
    }
	

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
