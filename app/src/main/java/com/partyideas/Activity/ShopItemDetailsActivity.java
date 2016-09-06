package com.partyideas.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.partyideas.Adapter.ShopObject;
import com.partyideas.R;

public class ShopItemDetailsActivity extends AppCompatActivity {

    ImageView headerImg;
    ShopObject shopObj;
    private String BASE_API_SERVER ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_item_details);
        BASE_API_SERVER  = getResources().getString(R.string.base_api_server);
        shopObj = (ShopObject) getIntent().getSerializableExtra("data");
        headerImg = (ImageView)findViewById(R.id.gameimg);
        CardView orderCard = (CardView)findViewById(R.id.orderCard);
        setTitle(shopObj.name);
        boolean isLoggedIn = getSharedPreferences("account",MODE_PRIVATE).getBoolean("status",false);
        final String username = getSharedPreferences("account",MODE_PRIVATE).getString("username",null);

        if(isLoggedIn && shopObj.stock){
            ((TextView)findViewById(R.id.productname)).setText(shopObj.name+" x 1");
            ((TextView)findViewById(R.id.productprice)).setText("$"+shopObj.price);
            ((TextView)findViewById(R.id.orderas)).append(" "+username);
            ((TextView)findViewById(R.id.confirmbt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processOrder(username);
                }
            });
        }
        else
            orderCard.setVisibility(View.GONE);
        ((TextView)findViewById(R.id.price)).setText("$"+shopObj.price);
        ((TextView)findViewById(R.id.stock)).setText(shopObj.stock ? shopObj.quantity+" remaining" : "OUT OF STOCK");
        ((TextView)findViewById(R.id.desc)).setText(shopObj.desc);
        getHeaderImg();
    }
    private void getHeaderImg(){
        Ion.with(headerImg)
                .placeholder(R.mipmap.ic_picture)
                .load(shopObj.imgsrc);
    }
    private void processOrder(String username){
        final ProgressDialog pdialog = ProgressDialog.show(this,"Processing","Ordering item for you",true);
        JsonObject jobj = new JsonObject();
        jobj.addProperty("username",username);
        jobj.addProperty("qty",1);
        jobj.addProperty("price",shopObj.price);
        Ion.with(this)
                .load(BASE_API_SERVER+"/api/shop/"+shopObj.id)
                .setJsonObjectBody(jobj)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        pdialog.dismiss();
                        boolean status = result.get("status").getAsBoolean();
                        if(status){
                            Toast.makeText(getApplicationContext(),"Successfully ordered",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),result.get("err").getAsJsonObject().get("msg").getAsString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
