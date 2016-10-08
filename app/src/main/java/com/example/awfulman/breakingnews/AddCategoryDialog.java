package com.example.awfulman.breakingnews;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikhaillyapich on 30.09.16.
 */

public class AddCategoryDialog extends DialogFragment {
    private ArrayList<Integer> mSelectedItems;

    public interface AddCategoryDialogListener{
        void onDialogPositiveClick(DialogFragment dialog, ArrayList<Category> values);
    }

    private AddCategoryDialogListener mListener;

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddCategoryDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final List<Integer> mSelectedItems = new ArrayList<>();
        Bundle args = getArguments();
        final CharSequence[] allCats = args.getCharSequenceArray("categories");
        boolean[] currentCategories = args.getBooleanArray("currentCategories");
        for(int i = 0; i < currentCategories.length; i++){
            if (currentCategories[i] == true)
                mSelectedItems.add(i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.addMenuTitle)
                .setMultiChoiceItems(allCats,currentCategories,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        }).setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK, so save the mSelectedItems results somewhere
                // or return them to the component that opened the dialog
                ArrayList<Category> newList = new ArrayList<>();
                for (Integer selectedItem : mSelectedItems){
                    Category toAdd = new Category(allCats[selectedItem].toString());

                    toAdd.setSelected(true);
                    newList.add(toAdd);
                }
                mListener.onDialogPositiveClick(AddCategoryDialog.this, newList);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //some cancel
            }
        });;
        return builder.create();
    }
}
