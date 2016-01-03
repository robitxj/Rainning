A Flexible 3-tier Mobile App Prototype on Bluemix

Summary:
When we talk about mobile app development on Bluemix, we usually mention Mobile Data Service, which provides a simple way to access to mobile database on Bluemix from Mobile clients. Basically speaking, Mobile Data Service could be taken as 2-Tier framework, one is the data tier, which provides the data access features on the server side, such as add, delete, update, query, etc; the other is the client tier, which provides the relevant SDK to access the data. It is very easy to understand Mobile Data Service and bundle its SDK in the mobile apps. However, it lacks the flexibility to implement business logic on the server side while accessing the data. 

Inspired by Mobile Data service and classic J2EE 3-tier framework, we propose a 3-tier framework for mobile app development to release this limitation. For 3 tiers, we mean mobile client tie, app server tier, and the data tier. The app server tie acts as the broker between mobile client and data. The mobile client mainly focuses on the UI and it accesses the data from the app server tier. The data tie mainly focuses on the data storage. Once the app server gets the data requests from the mobile client, it goes to the data tier, and performs relevant actions on accessing the data, and then return the data to mobile client. During such process, it is very easy to append relevant business logic. The mobile client and data tier are isolated, and they have to leverage the app server tier for communication. 

Frankly speaking, we could use any runtime (Java, Node.js, etc) and data services (either SQL services or NoSQL services) to implement the 3-tier framework. In our prototype, we choose Android as the mobile client, use Asynchronous HTTP Library for Android (https://github.com/loopj/android-async-http) to communicate with the app server tie via HTTP Protocol. We choose Cloudant Service as the data tier. We choose the Java Liberty to implements the app server tier, including a REST like interface in JSON format for mobile client, a Java API for CouchDB Library (https://github.com/helun/Ektorp) to access the Cloudant data, and some relevant business logic. 

For mobile client, we begin with the vanilla BlueList Android which is originally for Mobile Data example and we implement similar features as using Mobile Data services, such as ADD/DELETE/Modify/Query items. Besides, we also implement a blacklist feature on the app server tier, that is when user will fail to add an item which is in blacklist. This logic is implemented on the app server tie, and we can easily change the blacklist criteria without modifying the client side app. It is difficult to implement such logic on Bluemix server side while using Mobile Data service.

In a word, by using the 3-tier mobile app prototype could implement not only the similar data access features as Mobile Data service, but also complex business logic on Bluemix server side. If only the mobile data access is required for the mobile app, it is suggested using the Mobile Data service, as it provides bundled SDK for easy development; while it requires additional complex business logic, it is suggested using the 3-tier mobile app framework.

Outline:
I. Introduction
	a.  App URL: 
	b.  Code URL: https://hub.jazz.net/project/chunbintang/mobiled/overview
II. Before getting started
	a.  Bluemix Account
	b.  Java Liberty and Cloudant Service Skill
	c.  Android Development Skill
III. How to create the app on Bluemix
	a.  Java Liberty Runtime
        1. Open the Catalog menu.
		2. From the Runtimes section, click Liberty for Java.
        3. In the App field, specify the name of your app, in this case, it is set to md001.
        4. Click Create.Wait for your application to provision.
　　b.  Cloudant Service Skill
        1. Click the App created in the Dashboard. Open the Catalog menu.
		2. Click Add A Service.
        3. Choose Cloudant under Data Management.
		4. Click Create. Click OK if it is prompted to restart the application.
IV. Build and Run
	a.  Server Side
        1. Build the project using Eclipse and export it as war archive, for example, "mobiled.war". You may find a compiled one under MyData/ under source code in case you do not want to compile it by yourself.
		2. Use cf tools to deploy app.
cf push <app_name> -p mobiled.war
	b.  Mobile Client Side
        1. Set up the Android IDE using Eclipse.
		2. Clone the Android package from  /MobileDAndroid, import it into Android IDE .
		3. Modify com.ibm.bluelist.HttpUtil.java to your app name.
　　	private final static String MOBILE_BACKEND_URL = "http://<app_name>.mybluemix.net/MobileServlet";
　　In my case, ad the app_name is md001, it is modified to:
　　private final static String MOBILE_BACKEND_URL = "http://md001.mybluemix.net/MobileServlet";
　　c. Build Android app.
V. Using the existing Android App if you do not have the Android environment 
        1. Go to https://hub.jazz.net/project/chunbintang/mobiled/overview#https://hub.jazz.net/gerrit/plugins/gerritfs/list/chunbintang%252Fmobiled/refs%252Fheads%252Fmaster/MyData, and download the MobileDAndroid.apk file, it will points to my app md001.
		2. Install it on a Android phone or tablet later than Android 4.0.
		3. Add an item, for example, "apple", etc.
		4. Try to add an item as "blackberry", it will fail. The reason is that word "black" is the keyword in the blacklist criteria.
