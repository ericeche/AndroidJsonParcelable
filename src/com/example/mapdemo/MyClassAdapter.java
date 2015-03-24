package com.example.mapdemo;

import java.util.ArrayList;
import java.util.Iterator;



import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MyClassAdapter extends ArrayAdapter<MyClass> {

    private ArrayList<MyClass> items;
    private Context context;

    public MyClassAdapter(Context context, int textViewResourceId, ArrayList<MyClass> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //    view = inflater.inflate(R.layout.list_mobile, null);
        }

        //get an Iterator object for ArrayList using iterator() method.
        Iterator<MyClass> itr = items.iterator();
       
        //use hasNext() and next() methods of Iterator to iterate through the elements
        System.out.println("Iterating through ArrayList elements...");
        while(itr.hasNext()) {
          System.out.println(itr.next());
        
        MyClass item = items.get(position);
        if (item!= null) {
            // My layout has only one TextView
         //   TextView itemView = (TextView) view.findViewById(R.id.label);
         //   TextView itemView1 = (TextView) view.findViewById(R.id.label1);
          //  TextView itemView2 = (TextView) view.findViewById(R.id.label2);
          /*
           
            if (itemView != null) {
                // do whatever you want with your string and long
                itemView.setText(item.reason);               
             
                
                if ( Float.parseFloat(item.reason ) >  Float.parseFloat(item.other_val ) ) {
                	itemView1.setBackgroundResource(R.drawable.rounded_greater);
                	 
                
                }
                if ( Float.parseFloat(item.reason ) <  Float.parseFloat(item.other_val ) ) {
                	itemView1.setBackgroundResource(R.drawable.rounded_less);
                	
                }
                itemView2.setText(item.other_val);
                itemView1.setText(Float.toString(Float.parseFloat(item.reason ) -  Float.parseFloat(item.other_val )));
            }
          */  
         }
        }

        return view;
    }
    
    
 
    
}
