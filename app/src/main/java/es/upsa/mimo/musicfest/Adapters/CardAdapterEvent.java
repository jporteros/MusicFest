package es.upsa.mimo.musicfest.Adapters;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import es.upsa.mimo.musicfest.Model.Event;
import es.upsa.mimo.musicfest.R;

/**
 * Created by Javier on 25/04/2017.
 */

public class CardAdapterEvent extends RecyclerView.Adapter<CardAdapterEvent.EventViewHolder> implements View.OnClickListener{

    ArrayList<Event> events;
    private View.OnClickListener listener;
    public CardAdapterEvent(ArrayList<Event> events) {
        if(events ==null){
            events = new ArrayList<>();
        }
        this.events = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_event,parent,false);
        v.setOnClickListener(this);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder viewHolder, int position) {
        Event event = events.get(position);
        if (viewHolder!=null){
            viewHolder.title.setText(event.getTitle());

            Picasso.with(viewHolder.itemView.getContext()).load(event.getImg()).error(R.drawable.es).into(viewHolder.img);

        }

    }
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

   public static class EventViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        private TextView title;
        public EventViewHolder(View itemView){
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.card_img);
            title = (TextView) itemView.findViewById(R.id.card_text);

        }
    }

}
