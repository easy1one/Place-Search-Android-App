# Place-Search-App(Android.ver)
- Trevel-n-Entertainment Search Android App using Google Places API

# 1. High Level Description
: This is the Android App version of "Place-Search-Web". 
The App that allows users to search for a place, look at information about it, save some as favorites and post on Twitter about the place. Once the user clicks on a button to serach for place details, the webpage should display several tabs which contain an info/ photos/ map/ route/ reviews respectively.
   
   
# 2. Skillsets
: Java, Android Studio
: Google Maps API, Google Serch Nearby API, etc.
   
# 3. Code relationships
1) AndroidMenifest.xml

2) build.gradle

3) "places_search" folder: Java Code
(1) Adapeters : 
- CustomListAdapterReview.java,
- CustomPhotoAdapter.java, 
- CustomRecyclerAdapter.java, 
- CustomRecyclerAdapterFav.java, 
- PlaceArrayAdapter.java, 
- PlacesAutoAdapter.java, 
- SectionsPageAdapter.java
						
(2) main Fragments : 
- DetailActivity.java, 
- FavFragment.java, 
- InfoFragment.java, 
- MainActivity.java, 
- MapFragment.java, 
- PhotosFragment.java, 
- ReviewsFragment.java, 
- SearchFragment.java, 
- SearchTable.java
							  
(3) Object Classes : 
- Yelp.java, 
- Reviews.java, 
- MyPlace.java

4) "res" folder : XML code mostly 
(1) "drawable" for images
(2) "layout" for each pages
(3) "values" > "arrays.xml" for the values of spinners 



# 4. Demo and Details
- Runs on Nexus 5X API 27(Android 8.1.0, API 27) on Android Studio simulator
- For the full video: https://youtu.be/hJdGYt2Aop0

- The result table display upto 20 places and active the Previous/Next button if there are more pages
- A user can modify the start location with autocomplete service
!

- Info tab contains Address/ Phone Number/ Price Level/ Rating/ Google Page/ Webpage/ Website/ Hours
!

- Photos tab contains photos in 4 rows from the Google library
- HTTP request by Picasso () for asynchronously loading data and images into ImageViews

- Map tab contains Travel mode{Driving/Bicycling/Transit/Walking}
- Google Play services for getting the current lication 
!

- Review tab displays the Google reviews and Yelp review of the place
- For Yelp, "Business Match" and "Business Reviews" APIs
!

- Favorite tab keeps the selected places and they could be deleted from the favorite list using HTML5 local storage 
!

- Error handlings for searching, favorite list are applied
- A dynamic progress bar is displayed while data is being fetched

