package com.example.bios.crypton.pojos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class SavedInstanceFragment extends Fragment {

    private static final String TAG = "SavedInstanceFragment";
    private Bundle mInstanceBundle = null;

    public SavedInstanceFragment() { // This will only be called once be cause of setRetainInstance()
        super();
        setRetainInstance(true);
    }

    public static final SavedInstanceFragment getInstance(FragmentManager fragmentManager) {
        SavedInstanceFragment out = (SavedInstanceFragment) fragmentManager.findFragmentByTag(TAG);

        if (out == null) {
            out = new SavedInstanceFragment();
            fragmentManager.beginTransaction().add(out, TAG).commit();
        }
        return out;
    }

    public SavedInstanceFragment pushData(Bundle instanceState) {
        if (this.mInstanceBundle == null) {
            this.mInstanceBundle = instanceState;
        } else {
            this.mInstanceBundle.putAll(instanceState);
        }
        return this;
    }

    public Bundle popData() {
        Bundle out = this.mInstanceBundle;
        this.mInstanceBundle = null;
        return out;
    }
}
