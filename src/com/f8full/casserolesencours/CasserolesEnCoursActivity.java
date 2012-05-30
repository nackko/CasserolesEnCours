/*------------------------------------------------------------------------------
 **     Ident: Fabrice Veniard
 **    Author: Fabrice Veniard
 ** Copyright: (c) May 26, 2012 Fabrice Veniard All Rights Reserved.
 **------------------------------------------------------------------------------
 ** Fabrice Veniard                  |  No part of this file may be reproduced  
 ** @f8full                          |  or transmitted in any form or by any        
 **                                  |  means, electronic or mechanical, for the      
 ** H2J Montréal                     |  purpose, without the express written    
 ** Québec                           |  permission of the copyright holder.
 *------------------------------------------------------------------------------
 *
 *   This file is part of casserolesencours.
 *
 *   casserolesencours is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   casserolesencours is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with casserolesencours.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.f8full.casserolesencours;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import au.com.bytecode.opencsv.CSVReader;

import com.alohar.core.Alohar;
import com.alohar.user.callback.ALEventListener;
import com.alohar.user.callback.ALMotionListener;
import com.alohar.user.content.ALMotionManager;
import com.alohar.user.content.ALPlaceManager;
import com.alohar.user.content.data.ALEvents;
import com.alohar.user.content.data.MotionState;
//import com.alohar.user.content.data.UserStay;
import com.f8full.casserolesencours.R;
import com.google.android.maps.GeoPoint;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;
import com.google.api.client.googleapis.services.GoogleClient;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.http.xml.XmlHttpContent;
import com.google.api.client.http.xml.atom.AtomContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.xml.XmlNamespaceDictionary;

/*import com.google.api.client.googleapis.services.GoogleClient;
import com.google.api.services.docs.DocsClient;
import com.google.api.services.docs.DocsUrl;
import com.google.api.services.docs.model.DocumentListEntry;
import com.google.api.services.docs.model.DocumentListFeed;*/



//import 

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.livinglabmontreal.AtomCategory;
import org.livinglabmontreal.AtomRole;
import org.livinglabmontreal.AtomScope;
import org.livinglabmontreal.OAuth2AccessTokenActivity;
import org.livinglabmontreal.OAuth2ClientCredentials;
import com.google.api.client.googleapis.services.GoogleClient.Builder;

public class CasserolesEnCoursActivity extends Activity implements ALEventListener, ALMotionListener {
	private static final String PREF_NAME = "casserolesencours";
	private static final String PREF_KEY = "aloharuid";
	///////////////////////////////////////////////////////////////////////////////////
	public static final int APP_ID = 111; 

	////////////////////////////////////////////////////////////////////////////////////////
	//Account casserolesencours@gmail.com
	public static final String API_KEY = "47e49bd099908705ae5ec227de1bab7cdef20b45";
	private static final String PREF_REFRESH_TOKEN = "refreshToken";
	private static final int REQUEST_OAUTH2_AUTHENTICATE = 0;
	private static final String SERVICE_URL = "https://www.google.com/fusiontables/api/query/";
	///////////////////////////////////////////////////////////////////////////////////////

	private static final String PREF_TABLE_ENCID = "tableEncID";
	private static final String PREF_TABLE_STATUS = "tableStatus";

	public String mFusionTableEncID;

	private ALMotionManager mMotionManager;
	private boolean mIsStationary=true;
	private ScheduledThreadPoolExecutor mLocationPollThreadExecutor= new ScheduledThreadPoolExecutor(20);

	View mAloharAuthLayout, mMainLayout, mProgress;
	TextView mAccountView, mStatusView;
	/** The main handler. */
	Handler mMainHandler;



	/** The uid. */
	public String mAloharUid;
	private Future<?> mLocPollFrq0 = null;
	private ALPlaceManager mPlaceManager;
	private Future<?> mLocPollFrq1 = null;
	private Future<?> mLocPollFrq2 = null;
	private Future<?> mLocPollFrq3 = null;
	private Alohar mAlohar;
	private SharedPreferences mPrefs;
	private ToggleButton mServiceToggleButton;
	//private EventsManager mEventManager;
	private EditText mUIDView;


	GoogleCredential mGOOGCredential;// = new GoogleCredential();
	GoogleAccountManager mGOOGAccountManager;

	HttpTransport mNetHttpTransport = new NetHttpTransport();
	JsonFactory mJaksonJSONFactory = new JacksonFactory();

	GoogleClient mGOOGClient;// = new GoogleClient(mNetHttpTransport, mJaksonJSONFactory, SERVICE_URL);
	private TextView mPollFrequencyText;

	/** Logging level for HTTP requests/responses. */
	private static final Level LOGGING_LEVEL = Level.ALL;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);

		//That ease dev of technical stuff BUT is not wanted on a mid/longer term
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mAloharAuthLayout = findViewById(R.id.auth_layout);
		mMainLayout = findViewById(R.id.main_layout);
		mProgress = findViewById(R.id.progress_spin);

		mAccountView = (TextView)findViewById(R.id.account);

		mMainHandler = new Handler();

		mStatusView = (TextView)findViewById(R.id.service_status);
		mServiceToggleButton = (ToggleButton)findViewById(R.id.toggle);
		mUIDView = (EditText)findViewById(R.id.uid);

		mAlohar = Alohar.init(getApplication());


		mPlaceManager = mAlohar.getPlaceManager();
		mMotionManager = mAlohar.getMotionManager();

		//mEventManager = EventsManager.getInstance();
		//register listener
		//mPlaceManager.registerPlaceEventListener(mEventManager);
		mMotionManager.registerMotionListener(this);

		//Alohar original, I'm testing the other one
		//mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mPrefs = getSharedPreferences(PREF_NAME,MODE_PRIVATE);

		mAloharUid = mPrefs.getString(PREF_KEY, null);
		mFusionTableEncID = mPrefs.getString(PREF_TABLE_ENCID, null);
		
		
		
		String tableStatus = mPrefs.getString(PREF_TABLE_STATUS, null);
		if(tableStatus == null)
			tableStatus = getString(R.string.tableStatusPrivate);
		
		((TextView)findViewById(R.id.tableStatus)).setText(tableStatus);	
		
			

		if (mAloharUid != null) {
			mUIDView.setText(String.valueOf(mAloharUid));
			onAuthenClick(mUIDView);
		} else {
			mAloharAuthLayout.setVisibility(View.VISIBLE);
		}

		if (mFusionTableEncID != null) {

			((TextView)findViewById(R.id.tableInfo)).setText(mFusionTableEncID);            

		} else {
			((TextView)findViewById(R.id.tableInfo)).setText("Tap create");
		}



		mGOOGCredential = new GoogleCredential.Builder().setTransport(mNetHttpTransport)
				.setJsonFactory(mJaksonJSONFactory)//.build();
				.setClientSecrets(OAuth2ClientCredentials.CLIENT_ID, OAuth2ClientCredentials.CLIENT_SECRET)
				.build();

		mGOOGCredential.setAccessToken(null);
		mGOOGCredential.setRefreshToken(mPrefs.getString(PREF_REFRESH_TOKEN, null));

		//Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
		mGOOGAccountManager = new GoogleAccountManager(this);

		Builder truc = GoogleClient.builder(mNetHttpTransport, mJaksonJSONFactory, new GenericUrl(SERVICE_URL));
		truc.setHttpRequestInitializer(mGOOGCredential);

		mGOOGClient = truc.build();    

		mPollFrequencyText = (TextView) findViewById(R.id.pollFrequencyText);
		mPollFrequencyText.setText("N/A when stationary");
		//Something is wrong with the color, I'll see that cosmetic side later
		//mPollFrequencyText.setTextColor(R.color.text_violet);

		mLocationPollThreadExecutor.setKeepAliveTime(0, TimeUnit.SECONDS);

		RadioGroup pollFrequencyRadioGroup = (RadioGroup) findViewById(R.id.pollFrequencyRadioGroup);

		pollFrequencyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				startTaskForId(checkedId, true);    
			}
		});         

		findViewById(R.id.frequency0).setEnabled(false);
		findViewById(R.id.frequency1).setEnabled(false);
		findViewById(R.id.frequency2).setEnabled(false);
		findViewById(R.id.frequency3).setEnabled(false); 


	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		//update the title
		String title = String.format("%s v%s %d", getString(R.string.app_name), getString(R.string.version), mAlohar.version());
		setTitle(title);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateServiceStatus();
		//update current user stay
		//UserStay stay = mPlaceManager.getLastKnownStay();
		/*if (stay != null) {
            ((TextView)findViewById(R.id.current_stay)).setText(stay.toString());
        }*/
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_OAUTH2_AUTHENTICATE:
			if (resultCode == RESULT_OK) {
				//here I have authorization code
				final String code = data.getStringExtra("authcode");

				//This is not great, my thread pool is polluted
				mLocationPollThreadExecutor.execute(new Runnable() {
					public void run() {
						try {
							TokenResponse accessTokenResponse = new AuthorizationCodeTokenRequest(new NetHttpTransport(),
									new JacksonFactory(),
									new GenericUrl("https://accounts.google.com/o/oauth2/token"),
									code)
							.setRedirectUri(OAuth2ClientCredentials.REDIRECT_URI)
							.setScopes(OAuth2ClientCredentials.SCOPE)
							.setClientAuthentication(new ClientParametersAuthentication(OAuth2ClientCredentials.CLIENT_ID, OAuth2ClientCredentials.CLIENT_SECRET))
							.execute();						


							setRefreshToken(accessTokenResponse.getRefreshToken());
							mGOOGCredential.setFromTokenResponse(accessTokenResponse);						


						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});                


			} else {
				//Auth code grabing wnet wrong, relaunch activity (that could give a wonderfull infinite loop on no network access ?
				//For now on just do nothing
			}
			break;
		}
	}

	void setRefreshToken(String refreshToken) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(PREF_REFRESH_TOKEN, refreshToken);
		editor.commit();
	}

	void setTableEncID(String tableEncID) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(PREF_TABLE_ENCID, tableEncID);
		
		editor.commit();
	}
	
	void setTableStatus(String status) {
		SharedPreferences.Editor editor = mPrefs.edit();
		
		editor.putString(PREF_TABLE_STATUS, status);
		editor.commit();
	}


	/**
	 * Update service status.
	 */
	private void updateServiceStatus() {
		if (mAlohar.isServiceRunning()) {
			mServiceToggleButton.setChecked(true);
			mStatusView.setText("Service is running!");
		} else {
			mServiceToggleButton.setChecked(false);
			mStatusView.setText("Service stopped");
		}
	}

	/**
	 * On register click.
	 *
	 * @param v the view
	 */
	public void onRegisterClick(View v) {
		mAloharAuthLayout.setVisibility(View.GONE);
		mMainLayout.setVisibility(View.GONE);
		mProgress.setVisibility(View.VISIBLE);
		mAlohar.register(APP_ID, API_KEY, this);
	}

	/**
	 * On authen click.
	 *
	 * @param v the view
	 */
	public void onAuthenClick(View v) {
		String inputUID  = mUIDView.getText().toString();
		if (inputUID.trim().length() == 0) {
			toastError("Please give a valid UID");
		} else {
			mAloharUid = inputUID;
			try {
				mAlohar.authenticate(mAloharUid, APP_ID, API_KEY, this);
			} catch (Exception e) {
				//    			e.printStackTrace();
			}
		}
		mProgress.setVisibility(View.VISIBLE);
	}

	public void onViewerModeClick(View view)
	{
		//excellent, just do a request to grab the locations and pass them around
		final String SqlQuery = "SELECT Date, Location, Description FROM " + mFusionTableEncID + " ORDER BY Date DESC";

		new Thread((new Runnable() {

			public void run() {
				try {

					String encodedQuery = URLEncoder.encode(SqlQuery, "UTF-8");

					GoogleUrl GUrl = new GoogleUrl(SERVICE_URL + "?sql=" + encodedQuery + "&encid=true");



					HttpRequest request = mGOOGClient.getRequestFactory().buildGetRequest(GUrl);
					HttpHeaders headers = new HttpHeaders();

					headers.setContentLength("0");//Required so that Fusion Table API considers request
					request.setHeaders(headers);

					HttpResponse response = request.execute();

					if(response.getStatusCode() == 200)
					{
						//Here I have my data
						InputStreamReader inputStreamReader = new InputStreamReader(response.getContent());
						BufferedReader bufferedStreamReader = new BufferedReader(inputStreamReader);
						CSVReader reader = new CSVReader(bufferedStreamReader);
						// The first line is the column names, and the remaining lines are the rows.
						List<String[]> csvLines = reader.readAll();

						List<String> columns = Arrays.asList(csvLines.get(0));
						List<String[]> rows = csvLines.subList(1, csvLines.size());
						
						if(rows.size() == 0)
						{
							toastMessage("Table vide !");
							return;
						}


						ArrayList<String> rowData = new ArrayList<String>();

						for(String[] row : rows)
						{
							String toAdd = "";
							for(String cell : row)
							{
								//No , in data, or things are gonna go horribly wrong here
								toAdd += cell + "|";
							}

							//I have this last pesky separator ,it will give me an empty String on the other side
							rowData.add(toAdd);
						}

						Intent mapIntent = new Intent(getApplicationContext(), CasserolesEnCoursViewerActivity.class);
						mapIntent.putStringArrayListExtra("rowsData", rowData);
						mapIntent.putExtra("timeColored", ((CheckBox)findViewById(R.id.timeColoredCheckbox)).isChecked());
						mapIntent.putExtra("relativeTime", ((CheckBox)findViewById(R.id.relativeTimeCheckbox)).isChecked());
						startActivity(mapIntent);


					}


				} 
				catch (HttpResponseException e) 
				{
					if (e.getStatusCode() == 401) 
					{
						mGOOGAccountManager.invalidateAuthToken(mGOOGCredential.getAccessToken());
						mGOOGCredential.setAccessToken(null);

						SharedPreferences.Editor editor2 = mPrefs.edit();
						editor2.remove(PREF_REFRESH_TOKEN);
						editor2.commit();


						toastMessage("OAuth login required, redirecting...");

						//This last Constant is weird
						startActivityForResult(new Intent().setClass(getApplicationContext(),OAuth2AccessTokenActivity.class), REQUEST_OAUTH2_AUTHENTICATE);

					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		})).start();
	}

	/**
	 * On service click.
	 *
	 * @param view the view
	 */
	public void onServiceClick(View view) {
		boolean isChecked = ((ToggleButton)view).isChecked();
		if (isChecked) {
			//turn on
			mAlohar.startServices();
			mStatusView.setText("Service is running!");
		} else {
			//turn off
			mAlohar.stopServices();
			mStatusView.setText("Service stopped");
		}
	}

	public void onManifClick(View view) {
		boolean isChecked = ((ToggleButton)view).isChecked();
		final String description;
		if (isChecked)
		{
			description = "MANIF START";    		
		} 
		else 
		{
			description = "MANIF END";
		}        

		new Thread((new Runnable() {

			public void run() {

				//Threaded : httpTransport is Thread safe, hence concurrent access to the web should be handled
				writeToFusionTable(true, description);

			}
		})).start();
	}

	public void onAntiEmeuteClick(View view) {
		boolean isChecked = ((ToggleButton)view).isChecked();
		final String description;
		if (isChecked)
		{
			description = "Anti Emeute Start";    		
		} 
		else 
		{
			description = "Anti Emeute END";
		}        

		new Thread((new Runnable() {

			public void run() {

				//Threaded : httpTransport is Thread safe, hence concurrent access to the web should be handled
				writeToFusionTable(true, description);

			}
		})).start();         
	}

	public void onSpotDispersionClick(View view) {

		final String description = "Ordre de dispersion ! :S";

		new Thread((new Runnable() {

			public void run() {

				//Threaded : httpTransport is Thread safe, hence concurrent access to the web should be handled
				writeToFusionTable(true, description);

			}
		})).start();
	}

	public void onSpotFusionClick(View view) {

		final String description = "Fusion ! :)";

		new Thread((new Runnable() {

			public void run() {

				//Threaded : httpTransport is Thread safe, hence concurrent access to the web should be handled
				writeToFusionTable(true, description);

			}
		})).start();
	}

	public void onSpotReductionClick(View view) {

		final String description = "reduction ! :s";

		new Thread((new Runnable() {

			public void run() {

				//Threaded : httpTransport is Thread safe, hence concurrent access to the web should be handled
				writeToFusionTable(true, description);

			}
		})).start();
	}

	public void onClearTableClick(View view) {

		TextView textView = (TextView) findViewById(R.id.tableInfo);
		textView.setText("Tap create");

		mFusionTableEncID = null;
		
		((TextView)findViewById(R.id.tableStatus)).setText(R.string.tableStatusPrivate);

		SharedPreferences.Editor editor2 = mPrefs.edit();
		editor2.remove(PREF_TABLE_ENCID);
		editor2.remove(PREF_TABLE_STATUS);
		editor2.commit();

	}

	public void onCreateTableClick(View view) {

		//Create table with required columns and retrieve it's encID

		//final String description = "Fusion ! :)";

		new Thread((new Runnable() {

			public void run() {
				try {
					sendCreateQueryToFusionTable();

				} 
				catch (HttpResponseException e) 
				{
					if (e.getStatusCode() == 401) 
					{
						mGOOGAccountManager.invalidateAuthToken(mGOOGCredential.getAccessToken());
						mGOOGCredential.setAccessToken(null);

						SharedPreferences.Editor editor2 = mPrefs.edit();
						editor2.remove(PREF_REFRESH_TOKEN);
						editor2.commit();


						toastMessage("OAuth login required, redirecting...");

						//This last Constant is weird
						startActivityForResult(new Intent().setClass(getApplicationContext(),OAuth2AccessTokenActivity.class), REQUEST_OAUTH2_AUTHENTICATE);

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		})).start();
	}

	private void publicizeTable() throws IOException
	{



		try {
			
			XmlNamespaceDictionary docDic = new XmlNamespaceDictionary();
			docDic.set("", "http://www.w3.org/2005/Atom" );
			docDic.set("gAcl", "http://schemas.google.com/acl/2007");

			GenericData data = new GenericData();
			AtomCategory category = AtomCategory.newKind("accessRule");
			AtomRole role = AtomRole.newRole("reader");
			AtomScope scope = AtomScope.newScope("default");

			data.put("category", category);
			data.put("gAcl:role", role);
			data.put("gAcl:scope", scope);

			AtomContent content = AtomContent.forEntry(docDic,data);

			GoogleUrl GUrl = new GoogleUrl("https://docs.google.com/feeds/default/private/full/" + mFusionTableEncID + "/acl?v=3");
			HttpRequest request = mGOOGClient.getRequestFactory().buildPostRequest(GUrl, content);

			HttpResponse response = request.execute();

			if(response.getStatusCode() == 201)
			{
				toastMessage("Please NOT click drive");
				//Success !
				//Now send a mail at casserolesencours@gmail.com with table ID
				Intent it = new Intent(Intent.ACTION_SEND);
				String[] tos = { "casserolesencours@gmail.com" };
				it.putExtra(Intent.EXTRA_EMAIL, tos);
				it.putExtra(Intent.EXTRA_SUBJECT, "#casserolesencours register request for table ID:__" + mFusionTableEncID);
				it.putExtra(Intent.EXTRA_TEXT, getString(R.string.regmailcommunified) + "\r\n" + getString(R.string.regmailbody) + "\r\nLa compilation des résultats sera disponible ici : http://casserolesencours.blogspot.ca/" + getString(R.string.regmailsign));
				it.setType("message/rfc822");
				startActivity(it);

				//TODO: Replace by startActivityforResult to check if back was pressed
				mMainHandler.post(new Runnable() {

					public void run() {
						TextView textView = (TextView) findViewById(R.id.tableInfo);
						textView.setText(mFusionTableEncID);
						
						((TextView)findViewById(R.id.tableStatus)).setText(getString(R.string.tableStatusPublicPending));
						setTableStatus(getString(R.string.tableStatusPublicPending));
					}
				});	   


			}
		}
		catch (HttpResponseException e)
		{
			if(e.getStatusCode() == 409)	//Conflict
			{
				toastMessage("Please NOT click drive");
				Intent it = new Intent(Intent.ACTION_SEND);
				String[] tos = { "casserolesencours@gmail.com" };
				it.putExtra(Intent.EXTRA_EMAIL, tos);
				it.putExtra(Intent.EXTRA_SUBJECT, "#casserolesencours register request for table ID:__" + mFusionTableEncID);
				it.putExtra(Intent.EXTRA_TEXT, getString(R.string.regmailnotcommunified) + "\r\n" + getString(R.string.regmailbody) + "\r\nGlobal results will be posted here : http://casserolesencours.blogspot.ca/" + getString(R.string.regmailsign));
				it.setType("message/rfc822");
				startActivity(it);
				
				//TODO: Replace by startActivityforResult to check if back was pressed
				mMainHandler.post(new Runnable() {

					public void run() {
						TextView textView = (TextView) findViewById(R.id.tableInfo);
						textView.setText(mFusionTableEncID);
						
						((TextView)findViewById(R.id.tableStatus)).setText(getString(R.string.tableStatusPublicPending));
						setTableStatus(getString(R.string.tableStatusPublicPending));
					}
				});	   
				
				return;			
			}
			throw(e);
		}
		catch (IOException e)
		{
			throw(e);    		
		}

	}

	public void onRegisterTableClick(View view)
	{
		new Thread((new Runnable() {

			public void run() {
				try {
					publicizeTable();

				} 
				catch (HttpResponseException e) 
				{
					if (e.getStatusCode() == 401) 
					{
						mGOOGAccountManager.invalidateAuthToken(mGOOGCredential.getAccessToken());
						mGOOGCredential.setAccessToken(null);

						SharedPreferences.Editor editor2 = mPrefs.edit();
						editor2.remove(PREF_REFRESH_TOKEN);
						editor2.commit();


						toastMessage("OAuth login required, redirecting...");

						//This last Constant is weird
						startActivityForResult(new Intent().setClass(getApplicationContext(),OAuth2AccessTokenActivity.class), REQUEST_OAUTH2_AUTHENTICATE);

					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		})).start();    	
	}

	private void sendCreateQueryToFusionTable() throws JSONException, HttpResponseException, IOException
	{
		String today = DateFormat.getDateInstance().format(new Date());
		String todayFileFormatted = today.replace(" ", "_").replace(",", "_");


		String SqlQuery = "CREATE TABLE CasserolesEnCours" + todayFileFormatted + " (Date:DATETIME, Location:LOCATION, Manual:STRING, Description:STRING, IsStationary:STRING)";

		String encodedQuery = URLEncoder.encode(SqlQuery, "UTF-8");

		GoogleUrl GUrl = new GoogleUrl(SERVICE_URL + "?sql=" + encodedQuery + "&encid=true");

		try {

			HttpRequest request = mGOOGClient.getRequestFactory().buildPostRequest(GUrl, null);
			HttpHeaders headers = new HttpHeaders();

			headers.setContentLength("0");//Required so that Fusion Table API considers request
			request.setHeaders(headers);

			HttpResponse response = request.execute();

			if(response.getStatusCode() == 200)
			{
				String tableName="NONAME";

				InputStreamReader inputStreamReader = new InputStreamReader(response.getContent());
				BufferedReader bufferedStreamReader = new BufferedReader(inputStreamReader);
				CSVReader reader = new CSVReader(bufferedStreamReader);
				// The first line is the column names, and the remaining lines are the rows.
				List<String[]> csvLines = reader.readAll();
				List<String> columns = Arrays.asList(csvLines.get(0));
				List<String[]> rows = csvLines.subList(1, csvLines.size());

				//TextView textView = (TextView) findViewById(R.id.nameField);
				mFusionTableEncID = rows.get(0)[0];
				setTableEncID(mFusionTableEncID);
				setTableStatus(getString(R.string.tableStatusPrivate));

				//TODO : add request to retrieve NAME of table instead of ID

				mMainHandler.post(new Runnable() {

					public void run() {
						TextView textView = (TextView) findViewById(R.id.tableInfo);
						textView.setText(mFusionTableEncID);
						
						((TextView)findViewById(R.id.tableStatus)).setText(getString(R.string.tableStatusPrivate));
					}
				});	   

				toastMessage("Fusion table create request 200 OK :)--" + mFusionTableEncID);//dataList.get(0).getString("DESC"));
			}

		} catch(HttpResponseException e)
		{
			throw e;                                 

		} catch (IOException e) {

			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see com.alohar.user.callback.ALMotionListener#onMotionStateChanged(com.alohar.user.content.data.MotionState, com.alohar.user.content.data.MotionState)
	 */
	public void onMotionStateChanged(MotionState oldState, MotionState newState) {

		if( oldState == MotionState.STATIONARY || (newState != MotionState.BIGMOVEMENT && newState != MotionState.MICROMOVEMENT) )
		{
			//LOG IN TABLE
			final String motionStateSwitch = oldState.name() + "=>" + newState.name();



			final String userStateSwitch = "Stationary=" + mMotionManager.isStationary() + "|OnCommute=" + mMotionManager.isOnCommute();

			//final String motionStateToLog = DateFormat.getDateTimeInstance().format(new Date()) + " " + motionStateSwitch + "||" + userStateSwitch + "||Lat Long :" + getLatLongPosition() + "\r\n";

			//Not threaded : file write access don't play well in multithreaded environments
			//            try {
			//                writeToSD_CSV(false, motionStateSwitch);
			//            } catch (IOException e) {
			//                toastError("Can't write log to SD Card, IOException");
			//                //e.printStackTrace();
			//            }

			new Thread((new Runnable() {

				public void run() {

					//Threaded : httpTransport is Thread safe, hence concurrent access to the web should be handled
					writeToFusionTable(false, motionStateSwitch);

				}
			})).start();

			///////////////////////////////////////////////
			mMainHandler.post(new Runnable() {

				public void run() {
					((TextView)findViewById(R.id.motion_state)).setText(motionStateSwitch);
					((TextView)findViewById(R.id.user_state)).setText(userStateSwitch);
				}
			});
			////////////////////////////////////////////////////////
		}


		if(mIsStationary && mMotionManager.isStationary() == false)
			//We started to move, enable frequency controls
		{
			findViewById(R.id.frequency0).setEnabled(true);
			findViewById(R.id.frequency1).setEnabled(true);
			findViewById(R.id.frequency2).setEnabled(true);
			findViewById(R.id.frequency3).setEnabled(true);

			RadioGroup pollFrequencyRadioGroup = (RadioGroup) findViewById(R.id.pollFrequencyRadioGroup);
			if(pollFrequencyRadioGroup.getCheckedRadioButtonId() == -1)
			{
				//check(...) inderictly calls startTaskForId(...)
				((RadioGroup) findViewById(R.id.pollFrequencyRadioGroup)).check(R.id.frequency2);                
			}
			else
			{
				//START SCHEDULED THREADED ACTIVTY TO PUSH Location TO FUSION TABLE
				startTaskForId(pollFrequencyRadioGroup.getCheckedRadioButtonId(), false);
			}

			((TextView) findViewById(R.id.pollFrequencyText)).setText("{Polling frequency}:");
			//((TextView) findViewById(R.id.pollFrequencyText)).setTextColor(R.color.text_blue);
		}
		else if(mIsStationary == false && mMotionManager.isStationary())
			//We just stopped, disable 
		{
			findViewById(R.id.frequency0).setEnabled(false);
			findViewById(R.id.frequency1).setEnabled(false);
			findViewById(R.id.frequency2).setEnabled(false);
			findViewById(R.id.frequency3).setEnabled(false);

			//((RadioGroup) findViewById(R.id.pollFrequencyRadioGroup)).check(-1);
			cancelActiveTasks();
			mLocationPollThreadExecutor.purge();


			//Should I thread this as the Alohar's guy did by posting a new thread on the main handle ?
			((TextView) findViewById(R.id.pollFrequencyText)).setText("{N/A when stationary}");

			//((TextView) findViewById(R.id.pollFrequencyText)).setTextColor(R.color.text_violet);

			new Thread((new Runnable() {

				public void run() {

					//Threaded : httpTransport is Thread safe, hence concurrent access to the web should be handled
					writeToFusionTable(false, "Location poll STOP--Purged");

				}
			})).start();           

		}

		mIsStationary = mMotionManager.isStationary(); 
	}

	/////////////////////////////////////////////////////////////////////////////
	protected void writeToFusionTable(boolean manual, String desc) 
	{
		JSONObject newData = new JSONObject();

		try {
			newData.put("DESC", desc);
			newData.put("LOCATION", getLatLongPosition());
			newData.put("DATE", DateFormat.getDateTimeInstance().format(new Date()) );

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		writeToFusionTable(manual, newData);

	}

	protected void writeToFusionTable(boolean manual, JSONObject timeNDescNLoc) 
	{
		JSONObject newData = new JSONObject();

		String debugOrder = "";

		////////////////////////////
		//DEBUG : this is actually not working right now
		//        try {
		//            debugOrder = timeNDesc.getString("DESC");
		//            debugOrder += " DO:" + Integer.toString(mDebugRequestNumber.getAndIncrement());
		//            //timeNDesc.put("DESC", debugOrder);
		//        } catch (JSONException e1) {
		//            // TODO Auto-generated catch block
		//            e1.printStackTrace();
		//        }
		//////////////////////////////

		try {
			newData.put("DATE", timeNDescNLoc.get("DATE"));
			newData.put("LOCATION", timeNDescNLoc.get("LOCATION"));
			newData.put("DESC", timeNDescNLoc.get("DESC"));

			/////////////////////////////////////////////////////////////
			//Additional data

			newData.put("MANUAL", manual);            
			newData.put("STATIONARY", mMotionManager.isStationary());
			//newData.put("COMMUTE", mMotionManager.isOnCommute());	

			//TODO Second parameter (SD log on ERROR) should come from upper levels
			pushDataToFusionTable(newData, true);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			toastError("JSON error while formatting JSON data for query");
			return;
		}

	}
	///////////////////////////////////////////////////////

	private void pushDataToFusionTable(JSONObject data, boolean logOnError) throws JSONException
	{
		ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
		dataList.add(data);

		try {
			pushDataToFusionTable(dataList);

		} catch (HttpResponseException e) {
			if (e.getStatusCode() == 401) {
				mGOOGAccountManager.invalidateAuthToken(mGOOGCredential.getAccessToken());
				mGOOGCredential.setAccessToken(null);

				SharedPreferences.Editor editor2 = mPrefs.edit();
				editor2.remove(PREF_REFRESH_TOKEN);
				editor2.commit();


				toastMessage("OAuth login required, redirecting...");

				//This last Constant is weird
				startActivityForResult(new Intent().setClass(getApplicationContext(),OAuth2AccessTokenActivity.class), REQUEST_OAUTH2_AUTHENTICATE);

			}

			if(logOnError)
			{
				toastMessage("Fusion table INSERT failed...attempt to CSV log...");

				//Here I have to generate an error CSV file corresponding to the missed row of data
				//I add a fine timestamp on the data to avoid threads locking (unique filename), worse case two rows of data
				//will contains the exact same date string.
				try {
					data.put("ERROR_TIMESTAMP", new Date().getTime() );
				} catch (JSONException e1) {
					//Things went horribly horribly wrong, losing data for now
					toastError("Error timestamp JSON write failed...dropping row :(");
					return;
				}


				if(writeToSD_CSVError(data))
				{
					toastMessage("Happy dance ! -- Error CSV log succeded, remember to flush.");
					//HAPPY DANCE !!
				}
				else
				{
					//Things went horribly horribly wrong, losing data for now
					toastError("Error CSV writing failed...dropping row :(");
				}
			}
			else
			{
				toastMessage("Fusion table INSERT failed and logOnError==false");                
			}
		} catch (IOException e) {
			if(logOnError)
			{
				toastMessage("Fusion table INSERT failed...attempt to CSV log...");

				//Here I have to generate an error CSV file corresponding to the missed row of data
				//I add a fine timestamp on the data to avoid threads locking, worse case two rows of data
				//will contains the exact same date string.
				try {
					data.put("ERROR_TIMESTAMP", new Date().getTime() );
				} catch (JSONException e1) {
					//Things went horribly horribly wrong, losing data for now
					toastError("Error timestamp JSON write failed...dropping row :(");
					return;
				}


				if(writeToSD_CSVError(data))
				{
					toastMessage("Happy dance ! -- Error CSV log succeded, remember to flush.");
					//HAPPY DANCE !!
				}
				else
				{
					//Things went horribly horribly wrong, losing data for now
					toastError("Error CSV writing failed...dropping row :(");
				}
			}
			else
			{
				toastMessage("Fusion table INSERT failed and logOnError==false");                
			}
		}	
	}

	private void pushDataToFusionTable(ArrayList<JSONObject> dataList) throws JSONException, HttpResponseException, IOException
	{
		String SqlQuery = "";


		//Can I pass JSON around to input data into the table ? (Like in the request body or something)

		for(int i=0; i<dataList.size(); ++i)
		{
			SqlQuery += "INSERT INTO " + mFusionTableEncID + " (Date, Location, Manual, Description, IsStationary) VALUES ('"//fv#casseroles


					//Corresponds to following table https://www.google.com/fusiontables/DataSource?snapid=S512254UC6Z
					//SqlQuery += "INSERT INTO 1kOj-qW2ymCjwZ40HAFZ0HWwobZgCbEyOQVLqB5Q (Date, Location, Manual, Description, IsStationary) VALUES ('"//fv#casseroles
					//SqlQuery += "INSERT INTO 16ehK-lBkc8e_FTsKkizVteghlo6XSiHpLwBTSfo (Date, Location, Manual, Description, IsStationary, IsOnCommute) VALUES ('"//fv
					//SqlQuery += "INSERT INTO 1Ud9BqVMCWWDVmSGHUFGgmAnmxqhXiXTt_gllWxI (Date, Location, Manual, Description, IsStationary, IsOnCommute) VALUES ('"//sg
					+ dataList.get(i).getString("DATE")
					+ "', '"
					+ dataList.get(i).getString("LOCATION")
					+ "', '"
					+ dataList.get(i).getBoolean("MANUAL")
					+ "', '"
					+ dataList.get(i).getString("DESC")
					+ "', '"
					+ dataList.get(i).getBoolean("STATIONARY")
					/*+ "', '"
                    + dataList.get(i).getBoolean("COMMUTE")*/
					+ "')";
			if(dataList.size() != 1)
			{
				SqlQuery += ";";
			}

		}


		String encodedQuery = URLEncoder.encode(SqlQuery, "UTF-8");

		GoogleUrl GUrl = new GoogleUrl(SERVICE_URL + "?sql=" + encodedQuery);//&access_token=" + credential.getAccessToken());
		//GenericUrl  GUrl = new GoogleUrl(SERVICE_URL + "?sql=" + encodedQuery);//&access_token=" + credential.getAccessToken());
		//GUrl.get

		//UrlEncodedContent content = new UrlEncodedContent();

		try {

			HttpRequest request = mGOOGClient.getRequestFactory().buildPostRequest(GUrl, null);
			HttpHeaders headers = new HttpHeaders();

			headers.setContentLength("0");//Required so that Fusion Table API considers request
			request.setHeaders(headers);

			HttpResponse response = request.execute();

			if(response.getStatusCode() == 200)
			{
				toastMessage("Fusion table update request 200 OK :)--" + dataList.get(0).getString("DESC"));
				//Does nothing if nothing to flush
				flushErrorRows();
			}

		} catch(HttpResponseException e)
		{
			throw e;                                 

		} catch (IOException e) {

			throw e;
		}

	}

	private boolean writeToSD_CSVError(JSONObject data)
	{
		File sdCard = getSDCardFile(true);//getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

		if(sdCard==null)
			return false;

		File dir = new File (sdCard.getAbsolutePath() + "/" + getString(R.string.app_name) + "/OutputErrorLog");
		if(!dir.exists())
		{
			dir.mkdirs();
		}


		String today;

		try {
			today = data.getString("DATE") + "_" + data.getLong("ERROR_TIMESTAMP");
		} catch (JSONException e1) {
			//Data contains no date or error timestamp, abort
			return false;
		}//DateFormat.getDateTimeInstance().format(new Date());

		String todayFileFormatted = today.replace(" ", "_").replace(",", "_").replace(":", "_");		

		File fileCheck = getApplicationContext().getFileStreamPath(todayFileFormatted + "_ErrorRow.csv");
		if(fileCheck.exists())
		{
			//This is an error, the file should be unique, but somebody came before us
			//Let's just restamp the error and try again

			try {
				data.put("ERROR_TIMESTAMP", new Date().getTime());
			} catch (JSONException e) {
				return false;
			}

			return writeToSD_CSVError(data);

		}	
		//No else : all path lead to return

		File file = new File(dir, todayFileFormatted + "_ErrorRow.csv");
		//This creates the file if it doesn't already exists

		FileWriter writer=null;
		try {
			writer = new FileWriter(file, true);
		} catch (IOException e) {
			return false;
		}

		String toWrite = null;

		try {
			//////////////////////////////////////////////////////////////
			///DATA DEFINITION
			toWrite = "\"" + data.getString("DATE") + "\"," + data.getString("LOCATION") + "," + data.getBoolean("MANUAL") + "," + data.getString("DESC") 
					+ "," + data.getBoolean("STATIONARY") + /*"," + data.getBoolean("COMMUTE") +*/ "\r\n";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return false;
		}

		try {
			writer.write("Date,Location,Manual,Description,IsStationary\r\n");
			//writer.write("Date,Location,Manual,Description,IsStationary,IsOnCommute\r\n");
			writer.write(toWrite);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			return false;
		}

		return true;        	
	}

	private File getSDCardFile(boolean checkWriteAccess)
	{
		boolean externalStorageAvailable = false;
		boolean externalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			externalStorageAvailable = externalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			externalStorageAvailable = true;
			externalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			//  to know is we can neither read nor write
			externalStorageAvailable = externalStorageWriteable = false;
		}

		if(externalStorageAvailable == false)
			return null;
		else if(checkWriteAccess && !externalStorageWriteable)
			return null;        
		else
			return getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);    

	}

	/**
	 * Toast toastMessage.
	 *
	 * @param message the message
	 */
	public void toastMessage(final String message) {
		mMainHandler.post(new Runnable() {

			public void run() {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

			}
		});
	}

	private static final int MENU_FLUSH = 1;
	private static final int MENU_EXIT  = 2;
	private static final int MENU_FLUSHSQL  = 3;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_FLUSH, 0, "flush");
		menu.add(0, MENU_EXIT, 0, "Exit");
		menu.add(0, MENU_FLUSHSQL, 0, "Flush SQL");

		return super.onCreateOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_FLUSH:
			// try to trigger one post every 15 minutes
			mAlohar.flush();
			return true;
		case MENU_EXIT:
			mAlohar.stopServices();
			mAlohar.teardown();
			this.finish();
			return true;
		case MENU_FLUSHSQL:
			flushErrorRows();
			return true;   			

		}
		return super.onOptionsItemSelected(item);
	}



	private void flushErrorRows()
	{
		File sdCard = getSDCardFile(true);//getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
		String truc = sdCard.getAbsolutePath() + "/" + getString(R.string.app_name) + "/OutputErrorLog";
		File dir = new File (sdCard.getAbsolutePath() + "/" + getString(R.string.app_name) + "/OutputErrorLog");

		if(!dir.exists())
		{
			return;
		}        

		File[] errorFiles = dir.listFiles();

		if(errorFiles.length == 0)
		{
			//toastMessage("Nothing to flush");
			return;
		}

		final ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();

		for(int i=0; i<errorFiles.length; ++i)
			//I will create a big SQL query string containing every error rows separated by ;
		{
			FileInputStream fis;
			try {
				fis = new FileInputStream(errorFiles[i]);
				InputStreamReader inputStreamReader = new InputStreamReader(fis);
				BufferedReader bufferedStreamReader = new BufferedReader(inputStreamReader);
				CSVReader reader = new CSVReader(bufferedStreamReader);

				// The first line is the column names, and the remaining lines are the rows.
				List<String[]> csvLines;

				csvLines = reader.readAll();
				//List<String> columns = Arrays.asList(csvLines.get(0));
				List<String[]> rows = csvLines.subList(1, csvLines.size());
				//rows length should be 1 : 1 line of data

				//Let's construct a JSONObject from the second line
				JSONObject data = new JSONObject();

				/////////////////////////////////////////////
				//DATA Definition
				data.put("DATE", rows.get(0)[0]);
				data.put("LOCATION", rows.get(0)[1]);

				data.put("MANUAL", rows.get(0)[2]);
				data.put("DESC", rows.get(0)[3]);
				data.put("STATIONARY", rows.get(0)[4]);
				//data.put("COMMUTE", rows.get(0)[5]);

				dataList.add(data);                

			} catch (FileNotFoundException e1) {
				//That should never happen
				toastError("Can't flush SQL CSV error files");
				return;

			} catch (IOException e) {
				toastError("Can't flush SQL CSV error files");
				return;
			} catch (JSONException e) {
				toastError("Can't flush SQL CSV error files");
				return;
			}


		}

		new Thread((new Runnable() {

			public void run() {
				try {
					pushDataToFusionTable(dataList);
				} catch (HttpResponseException e) {
					if (e.getStatusCode() == 401) {
						mGOOGAccountManager.invalidateAuthToken(mGOOGCredential.getAccessToken());
						mGOOGCredential.setAccessToken(null);

						SharedPreferences.Editor editor2 = mPrefs.edit();
						editor2.remove(PREF_REFRESH_TOKEN);
						editor2.commit();
						//accountName = null;

						toastMessage("OAuth login required, please reflush afterwards... redirecting...");

						startActivityForResult(new Intent().setClass(getApplicationContext(),OAuth2AccessTokenActivity.class), REQUEST_OAUTH2_AUTHENTICATE);
						return; 
					}
				} catch (JSONException e) {
					toastError("Can't flush SQL CSV error files");
					return;
				} catch (IOException e) {
					toastError("Can't flush SQL CSV error files");
					return;
				}



				//if I get there everything went fine with my request, so I can delete files
				//NOT THREAD SAFE, multiple button clics could lead to nightmare
				//File sdCard = Environment.getExternalStorageDirectory();
				File sdCard = getSDCardFile(true);//getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

				File dir = new File (sdCard.getAbsolutePath() + "/" + getString(R.string.app_name) + "/OutputErrorLog");
				if(!dir.exists())
				{
					return;
				}




				File[] errorFiles = dir.listFiles();
				//Reenable button in interface to prevent multiple threads trying to delete files
				for(int i=0; i<errorFiles.length; ++i)
					//I will create a big string containing every error rows separated by ;
				{
					errorFiles[i].delete();
					//Could return false
				}                

				toastMessage("SQL flushing successfull " + errorFiles.length + " row(s) uploaded");
			}
		})).start();




	}

	private void cancelActiveTasks()
	{
		//mLocPollFrq2
		if(mLocPollFrq0 != null)
		{mLocPollFrq0.cancel(true);mLocPollFrq0=null;}
		else if(mLocPollFrq1 != null)
		{mLocPollFrq1.cancel(true);mLocPollFrq1=null;}
		else if(mLocPollFrq2 != null)
		{mLocPollFrq2.cancel(true);mLocPollFrq2=null;}
		else if(mLocPollFrq3 != null)
		{mLocPollFrq3.cancel(true);mLocPollFrq3=null;}
	}

	private void startTaskForId(int radioButtonID, final boolean manual)
	{

		String eventDescTemp = ""; 

		//Will do for now, insure only one task at a time, we could have overlap if wanted later on
		cancelActiveTasks();


		final JSONObject newData= new JSONObject(); //Just a convenient map ?

		try {

			newData.put("DESC", "Scheduled poll");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//That should be a switch
		if(radioButtonID == R.id.frequency0)
		{
			try {
				//Retrieve status message from ui in one function only
				newData.put("DESC", "Scheduled status update. UI says : " + ((EditText)findViewById(R.id.statusText)).getText().toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			eventDescTemp = "frequency -- now: 10min";

			//Threaded
			mLocPollFrq0 = mLocationPollThreadExecutor.scheduleAtFixedRate(new Runnable() {
				public void run() {
					try {
						newData.put("DATE", DateFormat.getDateTimeInstance().format(new Date()));
						newData.put("LOCATION", getLatLongPosition());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//Threaded : httpTransport is Thread safe, hence concurrent access to the web should be handled
					writeToFusionTable(false, newData);
				}
			}, 
			//60000, 60000, TimeUnit.MILLISECONDS);            
			10, 10, TimeUnit.MINUTES);
		}
		else if(radioButtonID == R.id.frequency1)
		{
			try {
				//Retrieve status message from ui in one function only
				newData.put("DESC", "Scheduled status update. UI says : " + ((EditText)findViewById(R.id.statusText)).getText().toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			eventDescTemp = "frequency -- now: 5min";

			//Threaded
			mLocPollFrq1 = mLocationPollThreadExecutor.scheduleAtFixedRate(new Runnable() {
				public void run() {
					try {
						newData.put("DATE", DateFormat.getDateTimeInstance().format(new Date()));
						newData.put("LOCATION", getLatLongPosition());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//Threaded : httpTransport is Thread safe, hence concurrent access to the web should be handled
					writeToFusionTable(false, newData);
				}
			},
			//30000, 30000, TimeUnit.MILLISECONDS);                                
			5, 5, TimeUnit.MINUTES);
		}
		else if(radioButtonID == R.id.frequency2)
		{
			try {
				//Retrieve status message from ui in one function only
				newData.put("DESC", "Scheduled status update. UI says : " + ((EditText)findViewById(R.id.statusText)).getText().toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			eventDescTemp = "frequency -- now: 3mins";

			//Threaded
			mLocPollFrq2 = mLocationPollThreadExecutor.scheduleAtFixedRate(new Runnable() {
				public void run() {
					try {
						newData.put("DATE", DateFormat.getDateTimeInstance().format(new Date()));
						newData.put("LOCATION", getLatLongPosition());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					//Threaded : httpTransport is Thread safe, hence concurrent access to the web should be handled
					writeToFusionTable(false, newData);
				}
			}, 
			//15000, 15000, TimeUnit.MILLISECONDS);                                
			3, 3, TimeUnit.MINUTES);
		}
		else if(radioButtonID == R.id.frequency3)
		{
			try {
				//Retrieve status message from ui in one function only
				newData.put("DESC", "Scheduled status update. UI says : " + ((EditText)findViewById(R.id.statusText)).getText().toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			eventDescTemp = "frequency -- now: 1min";

			//Threaded
			mLocPollFrq3 = mLocationPollThreadExecutor.scheduleAtFixedRate(new Runnable() {
				public void run() {
					try {
						newData.put("DATE", DateFormat.getDateTimeInstance().format(new Date()));
						newData.put("LOCATION", getLatLongPosition());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//Threaded : httpTransport is Thread safe, hence concurrent access to the web should be handled
					writeToFusionTable(false, newData);
				}
			},
			//5000, 5000, TimeUnit.MILLISECONDS);                                
			1, 1, TimeUnit.MINUTES);
		}

		final String eventDesc = eventDescTemp;

		//This is to thread the input of the changing frequency message, that's why tasks are delayed by their frequency
		new Thread((new Runnable() {

			public void run() {

				//Threaded : httpTransport is Thread safe, hence concurrent access to the web should be handled
				writeToFusionTable(manual, eventDesc);

			}
		})).start();



	}



	private String getLatLongPosition()
	{
		//Attempt to align values in CSV file but that failed
		//DecimalFormat df = new DecimalFormat("#.###############");

		//return df.format(mPlaceManager.getCurrentLocation().getLatitude()) + " " + df.format(mPlaceManager.getCurrentLocation().getLongitude());
		return mPlaceManager.getCurrentLocation().getLatitude() + " " + mPlaceManager.getCurrentLocation().getLongitude();
	}

	/**
	 * Toast error.
	 *
	 * @param message the message
	 */
	public void toastError(final String message) {
		mMainHandler.post(new Runnable() {

			public void run() {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				mAloharAuthLayout.setVisibility(View.VISIBLE);
				mMainLayout.setVisibility(View.GONE);
				mProgress.setVisibility(View.GONE);
			}
		});
	}

	//	private void writeToSD_CSV(boolean b, String motionStateSwitch) {
	//		// TODO Auto-generated method stub
	//		
	//	}

	/* (non-Javadoc)
	 * @see com.alohar.user.callback.ALEventListener#handleEvent(com.alohar.user.content.data.ALEvents, java.lang.Object)
	 */
	public void handleEvent(ALEvents event, Object data) {
		if (event == ALEvents.AUTHENTICATE_CALLBACK || event == ALEvents.REGISTRATION_CALLBACK) {
			if (data instanceof String) {
				mAloharUid = (String)data;
				Log.i("CobraDemo", "######UID=" + mAloharUid);
				//SharedPreferences prefs = getSharedPreferences(PREF_NAME,MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
				mPrefs.edit().putString(PREF_KEY, mAloharUid).commit();
			}

			//alohar service is ready to start
			mMainHandler.post(new Runnable() {

				public void run() {
					//switch to main layout
					mAloharAuthLayout.setVisibility(View.GONE);
					mMainLayout.setVisibility(View.VISIBLE);
					mProgress.setVisibility(View.GONE);
					mAccountView.setText(mAloharUid);
				}
			});
		} else if (event == ALEvents.GENERAL_ERROR_CALLBACK || event == ALEvents.SERVER_ERROR_CALLBACK) {
			toastError((String)data);
		}
	}
}