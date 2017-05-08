package com.flynetwifi.nhstream.Presenters;

/**
 * Created by fonseca on 3/18/17.
 */

import android.content.Context;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowHeaderPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flynetwifi.netplay.R;
import com.flynetwifi.nhstream.Models.CustomHeaderItemModel;

/**
 * Customized HeaderItem Presenter to show {@link CustomHeaderItemModel}
 */
public class CustomHeaderPresenter  extends RowHeaderPresenter {

    private static final String TAG = CustomHeaderPresenter.class.getSimpleName();

    private float mUnselectedAlpha;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        mUnselectedAlpha = viewGroup.getResources()
                .getFraction(R.fraction.lb_browse_header_unselect_alpha, 1, 1);
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_header_item, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object o) {
        CustomHeaderItemModel customHeaderItem = (CustomHeaderItemModel) ((ListRow) o).getHeaderItem();
        View rootView = viewHolder.view;

//        ImageView iconView = (ImageView) rootView.findViewById(R.id.header_icon);
//        int iconResId = customHeaderItem.getIconResId();
//        if( iconResId != customHeaderItem.ICON_NONE) { // Show icon only when it is set.
//            Drawable icon = rootView.getResources().getDrawable(iconResId, null);
//            iconView.setImageDrawable(icon);
//        }

        TextView label = (TextView) rootView.findViewById(R.id.header_label);
        label.setText(customHeaderItem.getName());
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        // no op
    }

    // TODO: TEMP - remove me when leanback onCreateViewHolder no longer sets the mUnselectAlpha,AND
    // also assumes the xml inflation will return a RowHeaderView
    @Override
    protected void onSelectLevelChanged(RowHeaderPresenter.ViewHolder holder) {
        // this is a temporary fix
        holder.view.setAlpha(mUnselectedAlpha + holder.getSelectLevel() *
                (1.0f - mUnselectedAlpha));
    }

}