package com.babbangona.accesscontrol;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import junit.runner.Version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class VersionControlAdapter extends RecyclerView.Adapter<VersionControlAdapter.ProductViewHolder> {


    private Context mCtx;
    private List<VersionControlClass> productList;

    ControlDB controlDB;

    String online_status;
    int download_version,new_version;

    private static final int PERMISSION_REQUEST_CODE = 1;
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
         versionControlClass= productList.get(position);

holder.tvUser.setText("");
holder.tvUser.setText(holder.tvUser.getText()+versionControlClass.getStaff_name()+"\n"+versionControlClass.getStaff_id()+
"\n"+versionControlClass.getApp_name()+"\nAC Version: "+versionControlClass.getAccess_control_version()+"\nUser Version: "+versionControlClass.getUser_version());
holder.tvAppid_Staffid.setText("");
holder.tvAppid_Staffid.setText(holder.tvAppid_Staffid.getText()+versionControlClass.getAppid_staffid());

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

        TextView tvUser,tvAppid_Staffid;
        Button btnResolve;






        public ProductViewHolder(View itemView) {
            super(itemView);

            tvUser = itemView.findViewById(R.id.user_info);
            btnResolve=  itemView.findViewById(R.id.btn);
            tvAppid_Staffid = itemView.findViewById(R.id.appid_staffid);




        }



    }





}