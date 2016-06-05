package edu.rolc.ollie;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

public class ContentFragment extends Fragment implements AdapterView.OnItemClickListener {
    CurrentContentView currentContentView;

    public ContentFragment() {
    }

    // Container Activity must implement this interface
    public interface CurrentContentView {
        public int getCurItem();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.currentContentView = (CurrentContentView) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int curItem = this.currentContentView.getCurItem();
        if (curItem == R.id.gridview) {
            return inflater.inflate(R.layout.fragment_grid, container, false);
        } else {
            // by default, we return the list view
            return inflater.inflate(R.layout.fragment_list, container, false);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int curItem = this.currentContentView.getCurItem();
        if (curItem == R.id.gridview) {
            GridView gridView = (GridView) getActivity().findViewById(R.id.grid);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    RemoteConfig.getSubjects());
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(this);
        } else {
            ListView listView = (ListView) getActivity().findViewById(R.id.d_list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    RemoteConfig.getSubjects());
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
    }
}
