package uk.ac.ox.cs.GPT9.augox.newsfeed;

import uk.ac.ox.cs.GPT9.augox.PlaceData;

// Interface representing what we need from our News Modules:
// 1.  Hooking them up to the news feed and data to operate on
// 2.  Telling them to search the Internet for data asynchronously
interface INewsModule {
	// Place to get news about and NewsFeed to give it to
	void GiveData(NewsFeed newsFeed, PlaceData place);
	
	// Tell the module to start trying to get data from the Internet
	void StartCall();
}