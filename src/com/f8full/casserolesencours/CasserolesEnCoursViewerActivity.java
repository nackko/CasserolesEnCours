package com.f8full.casserolesencours;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import com.f8full.casserolesencours.R;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class CasserolesEnCoursViewerActivity extends MapActivity  {

	
	MapView mMapView;
	
	private MapController mMapController;

	List<Overlay> mapOverlays;
	
	private MyLocationOverlay mMyLocationOverlay;
	private CasserolePopupOverlay mLastPopupOverlay;
	
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
	
	private boolean mShowMyLocation;

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
			mMapView = (MapView) findViewById(R.id.mapview);
			mMapView.setBuiltInZoomControls(true);
			
			mMapController = mMapView.getController();
			
			mShowMyLocation = getIntent().getBooleanExtra("myLocation", false); 
			
			if( mShowMyLocation == true)
			{
				//Add the MyLocationOverlay
				mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
				mMapController.setZoom(14);
			}
			else	//wolrmap, let zoom out
			{
				mMapController.setZoom(2); 
			}

			mapOverlays = mMapView.getOverlays();
			redPot = this.getResources().getDrawable(R.drawable.ic_launcher);
			itemizedOverlayRed = new CasserolesItemizedOverlay(redPot);


			redToBLue0Pot = this.getResources().getDrawable(R.drawable.ic_redtoblue0);
			redToBLue1Pot = this.getResources().getDrawable(R.drawable.ic_redtoblue1);
			redToBLue2Pot = this.getResources().getDrawable(R.drawable.ic_redtoblue2);
			bluePot = this.getResources().getDrawable(R.drawable.ic_blue);           




			itemizedOverlayRedToBlue0 = new CasserolesItemizedOverlay(redToBLue0Pot);
			itemizedOverlayRedToBlue1 = new CasserolesItemizedOverlay(redToBLue1Pot);
			itemizedOverlayRedToBlue2 = new CasserolesItemizedOverlay(redToBLue2Pot);
			itemizedOverlayBlue = new CasserolesItemizedOverlay(bluePot);



			Date newestTime = null;
			Date oldestTime = null;

			try {
				//relative time means real time now : pot will turn blue after 30 minutes
				//DEACTIVATED
				/*if(getIntent().getBooleanExtra("relativeTime", false) == true)
				{
					newestTime = new Date(); //now

					Calendar cl = Calendar.getInstance();
					cl.setTime(newestTime);
					cl.add(Calendar.MINUTE, -30);
					
					oldestTime = cl.getTime();
				}
				else
				{*/
					newestTime = DateFormat.getDateTimeInstance().parse(rowsExtra.get(0).split("\\|")[0]);
					oldestTime = DateFormat.getDateTimeInstance().parse(rowsExtra.get(rowsExtra.size()-1).split("\\|")[0]);
				//} DEACTIVATED
				
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

				if(getIntent().getBooleanExtra("timeColored", false) == false)
				{
					itemizedOverlayRed.addOverlay(overlayitem);					
				}
				else
				{
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
			}
			
			
			if(itemizedOverlayBlue.size() !=0)
				mapOverlays.add(itemizedOverlayBlue);
			
			if(itemizedOverlayRedToBlue2.size() !=0)
				mapOverlays.add(itemizedOverlayRedToBlue2);
			
			if(itemizedOverlayRedToBlue1.size() !=0)
				mapOverlays.add(itemizedOverlayRedToBlue1);
			
			if(itemizedOverlayRedToBlue0.size() !=0)
				mapOverlays.add(itemizedOverlayRedToBlue0);

			if(itemizedOverlayRed.size() !=0)
				mapOverlays.add(itemizedOverlayRed);
			
			//Newest location extraction;
			String newestLocRow = rowsExtra.get(0);
			String[] newestLocCells = newestLocRow.split("\\|");

			//Location is in second element in the forme of 'Lat Long'
			String[] newestlatLong = newestLocCells[1].split(" ");   
			
			GeoPoint LastestLocpoint = new GeoPoint((int)(Double.parseDouble(newestlatLong[0])*1e6),(int)(Double.parseDouble(newestlatLong[1])*1e6));
			
			mLastPopupOverlay = new CasserolePopupOverlay(mShowMyLocation);
			
			mLastPopupOverlay.setPosPoint(LastestLocpoint);
			mLastPopupOverlay.setLastRecordDate(newestTime);
			
			mapOverlays.add(mLastPopupOverlay);
			
			if( mShowMyLocation == true)
			{			
				mapOverlays.add(mMyLocationOverlay);
				
				mMyLocationOverlay.enableMyLocation();
				
				mMyLocationOverlay.runOnFirstFix(new Runnable() {
				       public void run() {
				    	   mMapController.animateTo(mMyLocationOverlay.getMyLocation());
				       }
				    });
			}
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
	protected void onResume() {
	    super.onResume();
	    if( mShowMyLocation == true)
		{
	    	mMyLocationOverlay.enableMyLocation();
		}
	}
	 
	@Override
	protected void onPause() {
	    super.onPause();
	    if( mShowMyLocation == true)
		{
	    	mMyLocationOverlay.disableMyLocation();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
