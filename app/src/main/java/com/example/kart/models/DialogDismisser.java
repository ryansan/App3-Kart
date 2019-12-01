package com.example.kart.models;

import java.util.List;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class DialogDismisser {

    public static void dismissAllDialogs(FragmentManager manager) {
        List<Fragment> allFragments = manager.getFragments();

        if (allFragments == null) {
            return;
        }

        for (Fragment fragment : allFragments) {
            if (fragment instanceof DialogFragment) {
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismissAllowingStateLoss();
            }

            FragmentManager childFragmentManager = fragment.getChildFragmentManager();

            if (childFragmentManager != null) {
                dismissAllDialogs(childFragmentManager);
            }
        }
    }
}
