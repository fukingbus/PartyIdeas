package com.partyideas.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.partyideas.Activity.ShopItemDetailsActivity;
import com.partyideas.Adapter.GameObject;
import com.partyideas.Adapter.ShopFragRecyclerViewAdapter;
import com.partyideas.Adapter.ShopObject;
import com.partyideas.Adapter.ShopRecyclerViewOnClickListener;
import com.partyideas.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopFragment extends Fragment {

    private String BASE_API_SERVER;
    RecyclerView recyclerView;
    ShopFragRecyclerViewAdapter adapter;
    ArrayList<ShopObject> dataset = new ArrayList<>();
    public ShopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BASE_API_SERVER  = getResources().getString(R.string.base_api_server);
        View root = inflater.inflate(R.layout.fragment_shop, container, false);
        recyclerView = (RecyclerView)root.findViewById(R.id.recyclerView);
       LinearLayoutManager  mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new ShopFragRecyclerViewAdapter(dataset);
        adapter.setOnClickListener(new ShopRecyclerViewOnClickListener() {
            @Override
            public void onClick(ShopObject obj) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ShopItemDetailsActivity.class);
                intent.putExtra("data",obj);
                getActivity().startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        getShopItems();
        return root;
    }
    private void getShopItems(){
        Ion.with(getContext())
                .load(BASE_API_SERVER+"/api/shop")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(e!= null)
                            e.printStackTrace();
                        else {
                            try {
                                JSONObject obj = new JSONObject(result.toString());
                                if(obj.getBoolean("status")){
                                    int length = obj.getInt("size");
                                    JSONArray data = obj.getJSONArray("data");
                                    for (int i=0;i<length;i++){
                                        JSONObject ApiItemObj = data.getJSONObject(i);
                                        ShopObject item = new ShopObject();
                                        item.id = ApiItemObj.getString("id");
                                        item.name = ApiItemObj.getString("name");
                                        item.desc = ApiItemObj.getString("description");
                                        item.quantity = ApiItemObj.getInt("quantity");
                                        item.stock = ApiItemObj.getBoolean("status");
                                        item.price = ApiItemObj.getString("price");
                                        item.imgsrc=  ApiItemObj.getString("imgsrc");
                                        dataset.add(item);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                            catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

}
