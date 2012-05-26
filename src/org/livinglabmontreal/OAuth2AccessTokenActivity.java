/*------------------------------------------------------------------------------
 **     Ident: Living Lab de Montréal
 **    Author: Fabrice Veniard
 ** Copyright: (c) May 26, 2012 Fabrice Veniard All Rights Reserved.
 **------------------------------------------------------------------------------
 ** Fabrice Veniard                  |  No part of this file may be reproduced  
 ** @f8full                          |  or transmitted in any form or by any        
 ** www.livinglabmontreal.org        |  means, electronic or mechanical, for the      
 ** Montréal                         |  purpose, without the express written    
 ** Québec                           |  permission of the copyright holder.
 *------------------------------------------------------------------------------
 *
 *   This file is part of casserolesencours.
 *   
 *   *   This file is a support class for OAuth Updated Google Fusion BellyDance on Android
 *   It is an copy of the version you can find here
 *   http://blog.doityourselfandroid.com/2011/08/06/oauth-2-0-flow-android/
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
package org.livinglabmontreal;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

//import com.alohar.demo.CobraDemoActivity;
import com.google.api.client.auth.oauth2.AuthorizationRequestUrl;

/**
 * Execute the OAuthRequestTokenTask to retrieve the request, and authorize the request.
 * After the request is authorized by the user, the callback URL will be intercepted here.
 * 
 */
public class OAuth2AccessTokenActivity extends Activity {

	final String TAG = getClass().getName();

	//private SharedPreferences prefs;
	//static final String PREF_AUTH_TOKEN = "authToken";

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//        Log.i(TAG, "Starting task to retrieve request token.");
//        this.prefs = getSharedPreferences(CobraDemoActivity.PREF_NAME,MODE_PRIVATE);
//        
//        
//        //new OAuthRequestTokenTask(this).execute();
//	}

	
	@Override
	protected void onResume() {
		super.onResume();
		WebView webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);  
        webview.setVisibility(View.VISIBLE);
        setContentView(webview);
        
        ArrayList<String> al = new ArrayList<String>();
        al.add("code");
        
        String authorizationUrl = new AuthorizationRequestUrl("https://accounts.google.com/o/oauth2/auth", OAuth2ClientCredentials.CLIENT_ID, al )
        .setRedirectUri(OAuth2ClientCredentials.REDIRECT_URI)
        .setScopes(OAuth2ClientCredentials.SCOPE)
        .build();      
        
        
        /* WebViewClient must be set BEFORE calling loadUrl! */  
        webview.setWebViewClient(new WebViewClient() {  

        	/*@Override  
            public void onPageStarted(WebView view, String url,Bitmap bitmap)  {  
        		System.out.println("onPageStarted : " + url);
            }*/
        	@Override  
            public void onPageFinished(WebView view, String url)  {  
            	
            	if (url.startsWith(OAuth2ClientCredentials.REDIRECT_URI)) 
            	{
            		int indexOfCode = url.indexOf("code="); 

					if (indexOfCode!=-1) {
					
						String code = extractCodeFromUrl(url);
						
						Intent codeBack = new Intent();
						codeBack.putExtra("authcode", code);
						
						setResult(RESULT_OK, codeBack);
						
						finish();
						
					} else if (url.indexOf("error=")!=-1) {
						view.setVisibility(View.INVISIBLE);
						//TODO: setResult(RESULT_USER+RESULT_NETWORKERROR) and handle in calling activity
						//TODO: The intent should be passed at the beginning of the activity so it know nothing about the return Intent to fire 
						//startActivity(new Intent(OAuth2AccessTokenActivity.this,CobraDemoActivity.class));
					}

            	}
                System.out.println("onPageFinished : " + url);
  		      
            }
			private String extractCodeFromUrl(String url) {
				return url.substring(OAuth2ClientCredentials.REDIRECT_URI.length()+7,url.length());
			}  
        });  
        
        webview.loadUrl(authorizationUrl);		
	}

}
