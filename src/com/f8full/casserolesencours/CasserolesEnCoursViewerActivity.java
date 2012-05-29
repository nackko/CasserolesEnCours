package com.f8full.casserolesencours;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
	Drawable redPot;
	Drawable redToBLue0Pot;
	Drawable redToBLue1Pot;
	Drawable redToBLue2Pot;
	Drawable bluePot;
	
	private CasserolesItemizedOverlay itemizedOverlayRed;
	private CasserolesItemizedOverlay itemizedOverlayRedToBlue0;
	private CasserolesItemizedOverlay itemizedOverlayRedToBlue1;
	private CasserolesItemizedOverlay itemizedOverlayRedToBlue2;
	private CasserolesItemizedOverlay itemizedOverlayBlue;
	
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
            redPot = this.getResources().getDrawable(R.drawable.ic_launcher);
            redToBLue0Pot = this.getResources().getDrawable(R.drawable.ic_redtoblue0);
            redToBLue1Pot = this.getResources().getDrawable(R.drawable.ic_redtoblue1);
            redToBLue2Pot = this.getResources().getDrawable(R.drawable.ic_redtoblue2);
            bluePot = this.getResources().getDrawable(R.drawable.ic_blue);           
                    
            
            
            itemizedOverlayRed = new CasserolesItemizedOverlay(redPot);
            itemizedOverlayRedToBlue0 = new CasserolesItemizedOverlay(redToBLue0Pot);
            itemizedOverlayRedToBlue1 = new CasserolesItemizedOverlay(redToBLue1Pot);
            itemizedOverlayRedToBlue2 = new CasserolesItemizedOverlay(redToBLue2Pot);
            itemizedOverlayBlue = new CasserolesItemizedOverlay(bluePot);
            
            
            
            Date newestTime = null;
            Date oldestTime = null;
            
			try {
				newestTime = DateFormat.getDateTimeInstance().parse(rowsExtra.get(0).split("\\|")[0]);
				oldestTime = DateFormat.getDateTimeInstance().parse(rowsExtra.get(rowsExtra.size()-1).split("\\|")[0]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
            
            
            for (String row : rowsExtra)
            {
            	String[] cells = row.split("\\|");
            	
            	//Location is in second element in the forme of 'Lat Long'
            	String[] latLong = cells[1].split(" ");        	
            	
            	
            	GeoPoint point = new GeoPoint((int)(Double.parseDouble(latLong[0])*1e6),(int)(Double.parseDouble(latLong[1])*1e6));
            	OverlayItem overlayitem = new OverlayItem(point, "", "");  
            	
            	Date rowDate = null;
				try {
					rowDate = DateFormat.getDateTimeInstance().parse(cells[0]);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	switch (mapValue(rowDate.getTime(), oldestTime.getTime(), newestTime.getTime(), 1L, 5L)) {
            	  
            	  case 1: 
            		  itemizedOverlayBlue.addOverlay(overlayitem);
            	    break;
            	  case 2: 
            		  itemizedOverlayRedToBlue2.addOverlay(overlayitem);
            	    break;
            	  case 3: 
            		  itemizedOverlayRedToBlue1.addOverlay(overlayitem);
            	    break;
            	  case 4: 
            		  itemizedOverlayRedToBlue0.addOverlay(overlayitem);
            	    break;
            	  case 5: 
            		  itemizedOverlayRed.addOverlay(overlayitem);
            	    break;
            	}
            	
            	
            	            
            }
            
            if(itemizedOverlayRed.size() !=0)
            	mapOverlays.add(itemizedOverlayRed);
            
            if(itemizedOverlayRedToBlue0.size() !=0)
            	mapOverlays.add(itemizedOverlayRedToBlue0);
            
            if(itemizedOverlayRedToBlue1.size() !=0)
            	mapOverlays.add(itemizedOverlayRedToBlue1);
            
            if(itemizedOverlayRedToBlue2.size() !=0)
            	mapOverlays.add(itemizedOverlayRedToBlue2);
            
            if(itemizedOverlayBlue.size() !=0)
            	mapOverlays.add(itemizedOverlayBlue);
        }      
    }
    
    private int mapValue(long value, long leftMin, long leftMax, long rightMin, long rightMax)
    {
    	long leftSpan = leftMax - leftMin;
    	long rightSpan = rightMax - rightMin;
    	
    	double valueScaled = (double)(value - leftMin) / (double)(leftSpan);
    	return (int)(rightMin + (valueScaled * rightSpan));    	
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
