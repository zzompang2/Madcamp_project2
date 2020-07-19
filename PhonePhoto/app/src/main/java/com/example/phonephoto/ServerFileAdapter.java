package com.example.phonephoto;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class ServerFileAdapter extends RecyclerView.Adapter<ServerFileAdapter.ServerFileViewHolder>{

    Context context;
    String[] fileNames;
    String[] pathArray;

    // ViewHolder : 각 view를 보관하는 holder 객체
    public class ServerFileViewHolder extends RecyclerView.ViewHolder {

        protected ConstraintLayout constraintLayout;
        protected TextView fileName;
       // protected ImageButton zoominButton;
        protected ImageButton downloadButton;

        public ServerFileViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.constraintLayout =itemView.findViewById(R.id.constraintLayout);
            this.fileName = itemView.findViewById(R.id.fileName);
            //this.zoominButton = itemView.findViewById(R.id.zoominButton);
            this.downloadButton = itemView.findViewById(R.id.downloadButton);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    zoomInSelectedImage(context, position);
                }
            });
        }
    }

    public ServerFileAdapter(Context context, String[] fileNames, String[] pathArray) {
        this.context = context;
        this.fileNames = fileNames;
        this.pathArray = pathArray;
    }

    @NonNull
    @Override
    public ServerFileAdapter.ServerFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_server_item, parent, false);
        ServerFileAdapter.ServerFileViewHolder serverFileViewHolder = new ServerFileAdapter.ServerFileViewHolder(view);
        return serverFileViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ServerFileAdapter.ServerFileViewHolder holder, int position) {
        holder.fileName.setText(fileNames[position]);
    }

    @Override
    public int getItemCount() {
        if(fileNames == null) return 0;
        return fileNames.length;
    }

    static void zoomInSelectedImage(Context context, int position) {
        ((ServerGalleryActivity)context).zoomInPhoto(position);
    }
}
