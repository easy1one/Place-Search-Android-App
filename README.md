# Place-Search-App(Android.ver)
- Trevel-n-Entertainment Search Android App using Google Places API

# 1. High Level Description
: This is the Android App version of "Place-Search-Web". 
The App that allows users to search for a place, look at information about it, save some as favorites and post on Twitter about the place. Once the user clicks on a button to serach for place details, the webpage should display several tabs which contain an info/ photos/ map/ route/ reviews respectively.
   
   
# 2. Skillsets
: JavaScript, PHP, HTML, CSS, Bootstrap, Angular, Google App Engine(GAE)
: Java, PHP, Android Studio
   
   
# 3. Code relationships
1)


# 4. Demo and Details
- The application makes an AJAX call to the search.php script hosted on GAE
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

