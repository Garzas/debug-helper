package com.appunite.debughelper.macro;

import android.app.Activity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.appunite.debughelper.DebugHelperPreferences;
import com.appunite.debughelper.R;
import com.google.gson.Gson;

import java.util.List;

import javax.annotation.Nonnull;

public class FieldManager {

    private static DebugHelperPreferences debugPreferences;

    public static void doMacro(final MacroPresenter.MacroItem macroItem, final ViewGroup viewGroup) {
        final List<SavedField> baseFieldItems = macroItem.getBaseFieldItems();

        for (SavedField baseField : baseFieldItems) {
            if (baseField != null) {
                View view = viewGroup.findViewById(baseField.getIdView());
                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    editText.setText(baseField.getText());
                } else if (view instanceof CompoundButton) {
                    CompoundButton compoundButton = (CompoundButton) view;
                    compoundButton.setChecked(baseField.isChecked());
                } else if (view instanceof Spinner) {
                    Spinner spinner = (Spinner) view;
                    spinner.setSelection(baseField.getSelectedPosition());
                } else if (view instanceof SearchView) {
                    SearchView searchView = (SearchView) view;
                    searchView.setQuery(baseField.getText(), false);
//                } else if (view instanceof RecyclerView) {
//                    RecyclerView recyclerView = (RecyclerView) view;
//                    MacroRecyclerViewListener listener = (MacroRecyclerViewListener) recyclerView.getAdapter();
//
//                    listener.fillFields((List) adapterGson().fromJson(savedField.getJson(), savedField.getTypeToken()));
                } else {
                    //TODO list all not updated items
                }
            }
        }
    }

    public static void init(final DebugHelperPreferences debugPreferences) {
        FieldManager.debugPreferences = debugPreferences;
    }

    public static void fillFields(@Nonnull final Activity activity) {
        final String json = debugPreferences.getFastMacro(activity.getClass().getSimpleName());

        if (json.isEmpty()) {
            Toast.makeText(activity, "Fast Macro not selected", Toast.LENGTH_SHORT).show();
        } else {
            final MacroPresenter.MacroItem macroItem = new Gson().fromJson(json, MacroPresenter.MacroItem.class);

            doMacro(macroItem, (ViewGroup) activity.findViewById(R.id.main_frame));
        }

    }
}
