package es.upsa.mimo.musicfest.Adapters;

import android.graphics.PixelFormat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.upsa.mimo.musicfest.Model.Event;
import es.upsa.mimo.musicfest.R;

/**
 * Created by Javier on 25/04/2017.
 */

public class CardAdapterEvent extends RecyclerView.Adapter<CardAdapterEvent.EventViewHolder> {

    ArrayList<Event> events;
    public CardAdapterEvent(ArrayList<Event> events) {
        if(events ==null){
            events = new ArrayList<>();
        }
        this.events = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_event,parent,false);

        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, int position) {
        Event event = events.get(position);
        if (viewHolder!=null){
            viewHolder.title.setText(event.getTitle());
            //TODO Probar a poner una de fondo y si la que se descarga es transparente se veria la otra
            Picasso.with(viewHolder.itemView.getContext()).load(event.getImg()).error(R.drawable.es).into(viewHolder.img);
                Log.d("en adapter","atesdel if");
          /*  if(viewHolder.img.getBackground().getOpacity() == PixelFormat.TRANSPARENT){
                viewHolder.img.setImageResource(R.drawable.es);
                Log.d("en adapater","dentro del if");
            }*/
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
