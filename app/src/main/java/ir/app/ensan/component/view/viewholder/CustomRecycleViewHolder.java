package ir.app.ensan.component.view.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import ir.app.ensan.EnsanApp;

/**
 * Created by k.monem on 6/2/2016.
 */
public abstract class CustomRecycleViewHolder extends RecyclerView.ViewHolder {

    protected Context context;

    public CustomRecycleViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        registerWidgets(itemView);
        setListener();
    }

    public String getString(int stringId) {
        return EnsanApp.getAppContext().getString(stringId);
    }

    public int getColor(int colorId) {
        return EnsanApp.getAppContext().getResources().getColor(colorId);
    }

    public abstract void registerWidgets(View view);

    public abstract void bind(Object model, int position);
    public void setListener(){};
}
