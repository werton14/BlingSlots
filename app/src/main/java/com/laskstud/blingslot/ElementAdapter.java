package com.laskstud.blingslot;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import java.util.List;

import butterknife.BindDrawable;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ElementViewHolder> {

    private List<ElementItem> elements;

    public  ElementAdapter(List<ElementItem> elements) {
        this.elements = elements;
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.element_layout,
                viewGroup, false);
        return  new ElementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder elementViewHolder, int i) {
        ElementItem elementItem = elements.get(i);
        elementViewHolder.imageView.setImageDrawable(elementItem.drawable);
        if(elementItem.anim != null) {
            elementViewHolder.view.startAnimation(elementItem.anim);
        }
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public class ElementViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imageView;

        public ElementViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            imageView = itemView.findViewById(R.id.elementImageView);
        }
    }
}
