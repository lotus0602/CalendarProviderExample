package com.n.calendarproviderexample;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by N on 2017-09-11.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private List<CalendarModel> mCalendarModels;
    private int mMode = -1;
    private Context mContext;

    SimpleDateFormat format = new SimpleDateFormat("YYYY/MM/dd HH:mm");

    public CalendarAdapter(Context context) {
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView id, title, name, date;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.item_id);
            title = (TextView) itemView.findViewById(R.id.item_title);
            name = (TextView) itemView.findViewById(R.id.item_name);
            date = (TextView) itemView.findViewById(R.id.item_date);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CalendarModel model = mCalendarModels.get(getAdapterPosition());

            Intent intent = new Intent(mContext, EditEventActivity.class);
            intent.putExtra("id", model.id);
            intent.putExtra("startMillis", model.startMillis);
            intent.putExtra("endMillis", model.endMillis);

            mContext.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return mCalendarModels == null ? 0 : mCalendarModels.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_recycler, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CalendarModel calendarModel = mCalendarModels.get(position);

        if (mMode == 0) {       // Calendar
            holder.id.setText(calendarModel.calID + "");
            holder.title.setText(calendarModel.displayName);
            holder.name.setText("Account : " + calendarModel.accountName + ", OwnerName : " + calendarModel.ownerName);
//        holder.date.setText();
        } else if (mMode == 1) {        // Event
            holder.id.setText("EventId : " + calendarModel.mId + ", CalendarId : " + calendarModel.mCalendarId);
            holder.title.setText(calendarModel.mTitle);
            holder.name.setText("OwnerName : " + calendarModel.mOwnerAccount);
            Date start_date = new Date(calendarModel.mStart);
            Date end_date = new Date(calendarModel.mEnd);
            holder.date.setText("DTSTART : " + format.format(start_date) +
                    ", DTEND : " + format.format(end_date));
            Log.d("", calendarModel.getEventToString());
        } else if (mMode == 2) {
            holder.id.setText("EventId : " + calendarModel.id);
            holder.title.setText(calendarModel.title);
            holder.name.setText("hasAlarm : " + calendarModel.hasAlarm +
                    ", Repeat : " + calendarModel.isRepeating);
            Date start_date = new Date(calendarModel.startMillis);
            Date end_date = new Date(calendarModel.endMillis);
            holder.date.setText("START : " + format.format(start_date)
                    + ", END : " + format.format(end_date));
        }

    }

    public void setData(List<CalendarModel> models, int mode) {
        mCalendarModels = models;
        mMode = mode;
        notifyDataSetChanged();
    }
}
