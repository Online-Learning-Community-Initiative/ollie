package edu.rolc.ollie;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.List;

public class ContentFragment extends Fragment implements AdapterView.OnItemClickListener {
    private List<String> content;
    private int curView;

    public void setContent(List<String> content) {
        this.content = content;
    }

    public void setCurView(int curView) {
        this.curView = curView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (this.curView == R.id.gridview) {
            return inflater.inflate(R.layout.fragment_grid, container, false);
        } else {
            // by default, we return the list view
            return inflater.inflate(R.layout.fragment_list, container, false);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (this.content == null) {
            return;
        }

        if (this.curView == R.id.gridview) {
            GridView gridView = (GridView) getActivity().findViewById(R.id.grid);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    this.content);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(this);
        } else {
            ListView listView = (ListView) getActivity().findViewById(R.id.d_list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    this.content);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String topic = (String) parent.getItemAtPosition(position);
        if (ContentDB.containsTopic(topic)) {
            final ContentFragment contentFragment = this;
            ContentDB.getTopics(topic, new TopicResponseCallback() {
                @Override
                public void onReceivingResponse(List<String> topics) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ContentFragment fragment = new ContentFragment();
                    fragment.setContent(topics);
                    fragment.setCurView(contentFragment.curView);
                    fragmentTransaction.replace(R.id.fragment, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        } else {
            Intent intent = new Intent(getActivity(), RenderContentActivity.class);
            startActivity(intent);
        }
    }
}
