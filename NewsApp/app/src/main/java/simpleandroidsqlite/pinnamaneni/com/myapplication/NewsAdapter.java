package simpleandroidsqlite.pinnamaneni.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import simpleandroidsqlite.pinnamaneni.com.myapplication.Pojos.Result;

/**
 * Created by pinnamak on 8/12/16.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private static final String TAG = "NewsAdapter";
    private List<Result> newsResultList;
    private List<Result> filteredNewsResultList;
    ValueFilter valueFilter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView itemTypeImage;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            itemTypeImage = (ImageView) view.findViewById(R.id.itemTypeImage);
        }
    }


    public NewsAdapter(List<Result> newsResultList) {
        this.newsResultList = newsResultList;
        this.filteredNewsResultList = newsResultList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Result result = filteredNewsResultList.get(position);
        holder.title.setText(result.getTitle());
        String itemType = result.getItemType();
        if (StringUtils.isNotEmpty(itemType)) {
            itemType = "ic_"+itemType.toLowerCase();
            Log.d(TAG , "Image name"+itemType);
        }
        Context ctx = holder.itemView.getContext();
        int resourceId = ctx.getResources().getIdentifier(itemType, "drawable", ctx.getPackageName());
        holder.itemTypeImage.setImageResource(resourceId);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = v.getContext();
                Intent intent =  new Intent(ctx, NewsDescription.class);
                intent.putExtra("newsDetailsUrl", result.getUrl());
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredNewsResultList.size();
    }

    public android.widget.Filter getFilter() {

        if (valueFilter == null) {

            valueFilter = new ValueFilter();
        }

        return valueFilter;
    }


    private class ValueFilter extends android.widget.Filter {


        //Invoked in a worker thread to filter the data according to the constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {

                List<Result> filterList = new ArrayList<>();

                for (int i = 0; i < newsResultList.size(); i++) {

                    if (StringUtils.containsIgnoreCase(newsResultList.get(i).getTitle(), constraint)) {

                        filterList.add(newsResultList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;

            } else {

                results.count = newsResultList.size();
                results.values = newsResultList;

            }

            return results;
        }


        //Invoked in the UI thread to publish the filtering results in the user interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            filteredNewsResultList = (ArrayList<Result>) results.values;

            notifyDataSetChanged();


        }
    }
}