/**
 * 
 */
package uk.ac.ox.cs.GPT9.augox;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GalleryPickerFragment extends Fragment {


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_picker, null);

        // Here we are fetching the layoutParams from parent activity and setting it to the fragment's view.


        return view;
    }
	
}
