---
services:
platforms:
author: azure
---

# URL Shortener - The Android Client
This is an Android client for a URL Shortening service.  The client depends on a web service backend written in PHP which is [available here](https://github.com/WindowsAzure-Samples/UrlShortener-PHP).  Once the PHP site is up and running in Windows Azure Websites, the Android client will allow users to view shortened URLs as well as adding their own.  This sample was built using Eclipse and the Android SDK.

Below you will find requirements and deployment instructions.

## Requirements
* Eclipse - This sample was built on Eclipse 3.7 though newer versions should work.  [Get Eclipse here](http://www.eclipse.org/downloads/).
* Android ADT - The ADT plugin for Eclipse was version 20 at build though newer versions should work.  [Get ADT here](http://developer.android.com/sdk/installing/installing-adt.html).
* Android SDK - The SDK was at version 20 at build and the app was compiled against API SDK version 15.  [Get the SDK here](http://developer.android.com/sdk/index.html).
* Windows Azure Account - Needed to run the PHP website.  [Sign up for a free trial](https://www.windowsazure.com/en-us/pricing/free-trial/).

## Additional Resources
Click the links below for more information on the technologies used in this sample.
* Blog Post - [Starting the Android Client - Displaying a list of shortened URLs](http://chrisrisner.com/Windows-Azure-Websites-and-Mobile-Clients-Part-6--The-Android-Client).
* Blog Post - [Displaying shortened URL Detials](http://chrisrisner.com/Windows-Azure-Websites-and-Mobile-Clients-Part-7--The-Android-Client-Continued).
* Blog Post - [Adding new Shortened URLs from the Android Client](http://chrisrisner.com/Windows-Azure-Websites-and-Mobile-Clients-Part-8--The-Android-Client-Finished).
* Blog Post - [Walkthrough of setting Eclipse and Android up for development](http://chrisrisner.com/31-Days-of-Android--Day-1---Getting-Set-Up-for-Development).

#Specifying your site's subdomain.
Once you've set up your PHP backend with Windows Azure Websites, you will need to enter your site's subdomain into the source/src/com/msdpe/shortifierdemo/misc/Constants.java file.  Replace all of the \<your-subdomain\> with the subdomain of the site you set up.

	public static final String kShortifierRootUrl = "http://<your-subdomain>.azurewebsites.net/";
	public static final String kGetAllUrl = "http://<your-subdomain>.azurewebsites.net/api-getall";
	public static final String kAddUrl = "http://<your-subdomain>.azurewebsites.net/api-add";

## Contact

For additional questions or feedback, please contact the [team](mailto:chrisner@microsoft.com).