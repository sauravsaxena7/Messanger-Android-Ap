package com.jlcsoftware.bloodbankcommunity.Interface;

import com.google.android.material.button.MaterialButton;

public interface RecyclerViewClickListener {

    void MakeLinksClickListener(int position, MaterialButton make_links);
    void setOnItemClickListener(int position);

}
