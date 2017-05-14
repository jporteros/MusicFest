
package es.upsa.mimo.musicfest.Adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import es.upsa.mimo.musicfest.Model.Comment;
import es.upsa.mimo.musicfest.R;


public class CardAdapterComment extends RecyclerView.Adapter<CardAdapterComment.ViewHolder> implements View.OnClickListener{
    ArrayList<Comment> comments;
    private View.OnClickListener listener;
    private String event_eid;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    public CardAdapterComment(ArrayList<Comment> comments,String event_eid) {
        auth = FirebaseAuth.getInstance();
        this.event_eid=event_eid;
        if (comments==null){
            comments=new ArrayList<>();
        }
        this.comments=comments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_comment, viewGroup, false);
        v.setOnClickListener(this);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
        final Comment comment = comments.get(pos);
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(userId.equalsIgnoreCase(comment.uid)){

            viewHolder.del_comment.setVisibility(View.VISIBLE);
            if(userId.equalsIgnoreCase(comment.uid)) {
                viewHolder.edit_comment.setVisibility(View.VISIBLE);
                viewHolder.edit_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

                        alert.setTitle("Modificar Comentario");
                        alert.setMessage("Por favor edite el siguiente comentario");

                        final EditText input = new EditText(view.getContext());

                        input.setText(comment.body);

                        LinearLayout ll = new LinearLayout(view.getContext());
                        ll.setOrientation(LinearLayout.VERTICAL);
                        ll.addView(input);
                        alert.setView(ll);
                        // commentsDao.editComment(comment);
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Comment edit_comment = new Comment(comment.uid, comment.cid, comment.getUsername(), input.getText().toString(), comment.date);

                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                mDatabase.child("event-Comments").child(event_eid).child(comment.cid).setValue(edit_comment);

                            }

                        });
                        alert.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Canceled.
                                        Log.d("CANCEL", "CANCLECANCEL");
                                    }
                                });
                        alert.show();
                    }
                });
            }
            else {
                viewHolder.edit_comment.setVisibility(View.GONE);
            }
            viewHolder.del_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new android.support.v7.app.AlertDialog.Builder(view.getContext())
                            .setTitle("Eliminar Comentario")
                            .setMessage("¿Seguro que quiere eliminar el comentario?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    mDatabase = FirebaseDatabase.getInstance().getReference();
                                    mDatabase.child("event-Comments").child(event_eid).child(comment.cid).removeValue();

                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });
        }else{
            viewHolder.del_comment.setVisibility(View.GONE);
            viewHolder.edit_comment.setVisibility(View.GONE);
        }
        viewHolder.username.setText(comment.username);
        viewHolder.body.setText(comment.body);
        viewHolder.date.setText(comment.date);
        // Picasso.with(viewHolder.itemView.getContext()).load(comment.getImg()).into(viewHolder.img);
        // viewHolder.img.setImageResource(event.img);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView body;
        public TextView date;
        public Button del_comment,edit_comment;

        public ViewHolder(View itemView) {
            super(itemView);
            del_comment= (Button)itemView.findViewById(R.id.delete_comment);
            edit_comment= (Button)itemView.findViewById(R.id.edit_comment);
            body= (TextView)itemView.findViewById(R.id.comment_body);
            date= (TextView)itemView.findViewById(R.id.comment_date);
            username = (TextView)itemView.findViewById(R.id.comment_username);

        }
    }
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }
}
