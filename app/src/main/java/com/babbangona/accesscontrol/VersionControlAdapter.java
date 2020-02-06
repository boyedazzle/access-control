package com.babbangona.accesscontrol;


import android.annotation.SuppressLint;
import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class VersionControlAdapter extends RecyclerView.Adapter<VersionControlAdapter.ProductViewHolder> {


    private Context mCtx;
    private List<VersionControlClass> productList;


    SessionManagement sessionManagement;
    private VersionControlClass versionControlClass;

    public VersionControlAdapter(Context mCtx, List<VersionControlClass> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.version_user_recycler, null);
        sessionManagement = new SessionManagement(view.getContext());

        return new ProductViewHolder(view);

    }


    @SuppressLint("SetTextI18n")

    public void onBindViewHolder(final ProductViewHolder holder, final int position) {
        versionControlClass = productList.get(position);

        holder.tvUser.setText("");
        holder.tvUser.setText(holder.tvUser.getText() + versionControlClass.getStaff_name() + "\n" + versionControlClass.getStaff_id() +
                "\n" + versionControlClass.getApp_name() + "\nAC Version: " + versionControlClass.getAccess_control_version() + "\nUser Version: " + versionControlClass.getUser_version());
        holder.tvAppid_Staffid.setText("");
        holder.tvAppid_Staffid.setText(holder.tvAppid_Staffid.getText() + versionControlClass.getAppid_staffid());

        holder.btnResolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ControlDB controlDB = ControlDB.getInstance(mCtx);
                controlDB.open();
                controlDB.updateIssue(holder.tvAppid_Staffid.getText().toString().trim());
                productList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), productList.size());
            }
        });
    }


    @Override
    public int getItemCount() {
        Log.d("product", String.valueOf(productList.size()));
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvUser, tvAppid_Staffid;
        Button btnResolve;


        public ProductViewHolder(View itemView) {
            super(itemView);

            tvUser = itemView.findViewById(R.id.user_info);
            btnResolve = itemView.findViewById(R.id.btn);
            tvAppid_Staffid = itemView.findViewById(R.id.appid_staffid);


        }


    }


}