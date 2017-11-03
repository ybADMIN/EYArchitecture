package com.yb.btcinfo.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.yb.btcinfo.R;
import com.yb.btcinfo.common.BaseFragment;
import com.yb.btcinfo.main.datamodel.PlatformModel;
import com.yb.btcinfo.platform.PlatformMsgActivity;
import com.yb.ilibray.utils.DisplayUtil;
import com.yb.ilibray.utils.DividerGridItemDecoration;
import com.yb.ilibray.utils.data.assist.Check;

import java.util.List;

import butterknife.BindView;
import mvp.data.store.glide.GlideApp;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlatfromFListener} interface
 * to handle interaction events.
 * Use the {@link PlatformFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlatformFragment extends BaseFragment<PlatfromPresenter> implements PlatfromView {
    @BindView(R.id.platfrom_rv)
    RecyclerView mPlatfromRv;
    private PlatfromFListener mListener;

    public PlatformFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlatformFragment.
     */
    public static PlatformFragment newInstance() {
        PlatformFragment fragment = new PlatformFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_platform_layout;
    }



    @Override
    protected void initView(View view) {
        if (view == null) return;
        mPlatfromRv.addItemDecoration(new DividerGridItemDecoration(context(),
                DisplayUtil.dip2px(context(),8), ContextCompat.getColor(context(),R.color.windows_background)));
        int itemHight = (DisplayUtil.getDisplayWidth(context())
                - DisplayUtil.dip2px(context(),66)) / 4;
        mPlatfromRv.setAdapter(new BaseQuickAdapter<PlatformModel,BaseViewHolder>(R.layout.item_platform) {
            @Override
            protected View getItemView(int layoutResId, ViewGroup parent) {
                if (layoutResId==R.layout.item_platform){
                    View view1=mLayoutInflater.inflate(layoutResId, parent, false);
                    RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(itemHight,itemHight);
                    view1.setLayoutParams(lp);
                    return view1;
                }else {
                    return super.getItemView(layoutResId, parent);
                }
            }

            @Override
            protected void convert(BaseViewHolder helper, PlatformModel item) {
                helper.setText(R.id.itme_tv_name, Check.checkReplace(item.getName()));
                GlideApp.with(mContext).load(item.getLogourl()).into((ImageView) helper.getView(R.id.itme_iv_logo));
            }
        });
        mPlatfromRv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent =new Intent(getContext(), PlatformMsgActivity.class);
                intent.putExtra("platform", (PlatformModel) adapter.getData().get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        getBasePresenter().getPlatfroms();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }



    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.platfromAction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlatfromFListener) {
            mListener = (PlatfromFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PlatfromFListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showRetry() {

    }

    @Override
    public void hideRetry() {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public Context context() {
        return getActivity();
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void showPlatfromList(List<PlatformModel> models) {
        if (null != mPlatfromRv && models.size()>0){
            BaseQuickAdapter adapter = (BaseQuickAdapter) mPlatfromRv.getAdapter();
            adapter.setNewData(models);
        }
    }

    public interface PlatfromFListener {
        void platfromAction(Uri uri);
    }

}
