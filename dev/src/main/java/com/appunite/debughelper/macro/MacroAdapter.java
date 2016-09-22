package com.appunite.debughelper.macro;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.appunite.debughelper.R;
import com.appunite.debughelper.model.EditMacro;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import rx.functions.Action1;

public class MacroAdapter extends RecyclerView.Adapter<MacroAdapter.MacroHolder>
        implements Action1<List<MacroPresenter.MacroItem>> {

    private List<MacroPresenter.MacroItem> mData;
    private MacroListener listener;

    public MacroAdapter(@Nonnull final MacroListener listener) {
        this.listener = listener;
        mData = new ArrayList<>();
    }

    @Override
    public MacroHolder onCreateViewHolder(@Nonnull final ViewGroup parent, final int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.macro_item, parent, false);
        return new MacroHolder(v);
    }

    @Override
    public void onBindViewHolder(final MacroHolder holder, final int position) {
        final MacroPresenter.MacroItem macroItem = mData.get(position);
        holder.macroName.setText(macroItem.getMacroName());

        if (macroItem.isSelected()) {
            holder.itemView.setBackgroundColor(Color.DKGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.useMacro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.useMacro(macroItem);
                }
            }
        });

        holder.editMacro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.editMacro(new EditMacro(holder.getAdapterPosition(), macroItem.getMacroName()));
            }
        });

        holder.deleteMacro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteMacro(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                listener.selectMacro(macroItem.getId());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void call(final List<MacroPresenter.MacroItem> macroItems) {
        this.mData = macroItems;
        notifyDataSetChanged();
    }

    public static class MacroHolder extends RecyclerView.ViewHolder {

        @Nonnull
        private final View itemView;
        TextView macroName;
        ImageButton useMacro;
        ImageButton editMacro;
        ImageButton deleteMacro;

        public MacroHolder(@Nonnull final View itemView) {
            super(itemView);
            this.itemView = itemView;
            macroName = (TextView) itemView.findViewById(R.id.macro_name);
            useMacro = (ImageButton) itemView.findViewById(R.id.use_macro);
            editMacro = (ImageButton) itemView.findViewById(R.id.edit_macro);
            deleteMacro = (ImageButton) itemView.findViewById(R.id.remove_macro);
        }
    }

    public interface MacroListener {

        void useMacro(final MacroPresenter.MacroItem macroItem);

        void editMacro(final EditMacro editMacro);

        void deleteMacro(final int position);

        void selectMacro(final String id);
    }

}