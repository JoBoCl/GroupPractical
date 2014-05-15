Oxford Group Practical - Team 9
===============================

Installing the application
--------------------------
_Note: the application should already be preinstalled on both tablets_

1. Enable side-loading applications in android preferences
2. Copy and paste the APK onto your device
3. Using a file manager, open and install the APK

*OR*

1. Run `adb install AugOx.apk` from the root directory of the repo, assuming `adb` is installed

Navigating the application
--------------------------

When you start the application for the first time, it will load up into the main screen, with the AR data being superimposed onto the camera feed.  Click an icon once to access a place's name and rating, and again to hide this information.  Click on the bubble that appears to access full information on the application.  Press and hold the radar view in the corner to access the map layer.

Each of the buttons on the top bar has a specific function:

* The circular icon is used to filter the icons that appear on the main screen.
* The page with lines is used to access a full list of places, sorted by proximity, category, name etc.
* The first arrow button is used to access the manual route planner.  Use the checkboxes in the bottom left corner to filter the available places.  Clicking on a place in the bottom list will open display more information about a given place, and clicking on the "Add to route" button will append it to the route.  Once in place, it is then possible to re-order the route, and remove elements from it.
* The second arrow button will open the automatic route planner.  Select the criteria for filtering place on the right hand side, and the number of places using the slider on top.  Select places from the options that appear, refresheing the options with the button on the right, or opting out of a timeslot using the checkbox.  Finally hit "Done" at the top to finish.
* The options (maximum automatic route length and maximum view distance) are available in the options menu, accessed via the cog.
