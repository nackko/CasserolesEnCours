This is ground base for citizen enabled strikes real time positioning update.
This is free software, published under GNU license.

It requires some manual setup to be used as of now
- Authorize Installed app (API not in LIST, click on button directly) in you Google API console 
https://code.google.com/apis/console/
- Copy relevant data to org.livinglabmontreal.OAuth2ClientCredentials.java
(.default file is a template)
 
 - Note : Alohar technology uses account casserolesencours@gmail.com, you can register your own key if you want
 and input it in main activity
 public static final int APP_ID = 111; 
	////////////////////////////////////////////////////////////////////////////////////////
    //Account casserolesencours@gmail.com
    public static final String API_KEY = "47e49bd099908705ae5ec227de1bab7cdef20b45";
 
Compile and run.
 
 Use Instructions :
 - On first launch, choose to register new user. It will create a new private dataset on Alohar cloud.
 It is important to note that no connection with device or device carrier will ever be made.
 At any point in time, you can register a new user by tapping menu->Exit 
 and then relaunch the app.
 - Create and clear table buttons for fusion table. Note that clear doesn't remove it from your drive just yet.
 
 - You should then be able to use interface, try testing taping dispersion
 
 If you activate the service by tapping Turn service on toogle, Alohar tech will start working, meaning your location will be tracked.
 Just tap the same button again to end tracking.
 - If you start moving around while Alohar service is on, radio buttons will be automatically available and app will automatically record data at specified frequency
  
 - TextField input is not used
 
 - THE MENU BUTTON : flush flushes ALOHAR DATE from phone
 Flush SQL flushes error rows generated if something went wrong when logging. It's important to do it regularly.
 Exit exits alohar SDK.
 