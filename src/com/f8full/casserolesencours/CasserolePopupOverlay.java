//From unlicensed code -- with modifications
//original : http://www.onthefencedevelopment.com/blog/using-google-maps-your-android-applications-%E2%80%93-part-4-displaying-information-popups

package com.f8full.casserolesencours;

import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class CasserolePopupOverlay extends Overlay {
	
	//private ArrayList<GeoPoint> mDisplayedMarkersList; 
	private LinearLayout mPopupLayout; 
	
	private GeoPoint mLastRecordPoint;
	private Date mLastRecordDate;
	
	private boolean mNavigateToMe;
	
	protected CasserolePopupOverlay(){}
	
	public CasserolePopupOverlay(boolean navigateToMe){
		mNavigateToMe = navigateToMe;
	}
	
	public void setPosPoint(GeoPoint pToSet){
		mLastRecordPoint = pToSet;
	}
	public void setLastRecordDate(Date pToSet){
		mLastRecordDate = pToSet;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
	}
	
	@Override
	public boolean onTap(GeoPoint pUnused, MapView mapView) {
	 
	    // If infopopup is currently displayed then clear it..
	    if (mPopupLayout != null) {
	        mapView.removeView(mPopupLayout);
	    }
	 
	        LayoutInflater inflater = (LayoutInflater) mapView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        mPopupLayout = (LinearLayout) inflater.inflate(R.layout.infopopup, mapView, false);
	 	       
	        MapView.LayoutParams params = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, mLastRecordPoint,
	                MapView.LayoutParams.BOTTOM_CENTER);
	 
	        mPopupLayout.setLayoutParams(params);
	 
	        TextView locationNameText = (TextView) mPopupLayout.findViewById(R.id.lastRecordTimePopupValue);
	         
	   
	        locationNameText.setText(DateFormat.getDateTimeInstance().format(mLastRecordDate));
	 
	        // Add the view to the Map
	        mapView.addView(mPopupLayout);
	         
	        if(mNavigateToMe)
	        	mapView.getController().animateTo(mLastRecordPoint);
	        
	   // }
	    return true;
	};

	/*private GeoPoint getSampleLocation() {

		// Create GeoPoint to secret location....
		GeoPoint sampleGeoPoint = new GeoPoint((int) (56.27058500725475 * 1E6), (int) (-2.6984095573425293 * 1E6));

		return sampleGeoPoint;
	}*/

}
