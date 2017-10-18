package com.yb.btcinfo.main.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.thefinestartist.finestwebview.FinestWebView;
import com.yb.btcinfo.R;
import com.yb.btcinfo.common.BaseFragment;
import com.yb.btcinfo.main.model.IndexDataModel;
import com.yb.ilibray.utils.data.assist.Check;
import com.yb.ilibray.widgets.ErrorView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mvp.data.store.glide.GlideApp;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment<HomePresenter> implements HomeView {
    @BindView(R.id.home_rv)
    RecyclerView mHomeRv;
    @BindView(R.id.swipe_ly)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.errorView)
    ErrorView mErrorView;
    Unbinder unbinder;
    private HomeFListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }


    @Override
    protected void initView(View view) {
        if (view == null) return;
        //设置在listview上下拉刷新的监听
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            getBasePresenter().getIndexNews("10");
        });
        mHomeRv.setAdapter(new BaseQuickAdapter<IndexDataModel, BaseViewHolder>(R.layout.item_home) {
            @Override
            protected void convert(BaseViewHolder helper, IndexDataModel item) {
                helper.setText(R.id.item_home_time, Check.checkReplace(item.getTime()))
                        .setText(R.id.item_home_content_tv, Check.checkReplace(Check.checkReplace(item.getUrl())))
                        .setText(R.id.item_home_title_tv, Check.checkReplace(item.getTitle()))
                        .setText(R.id.item_home_platform, Check.checkReplace(item.getPlatform()));
                GlideApp.with(getContext()).load(Check.checkReplace(item.getLogourl())).into((ImageView) helper.getView(R.id.item_home_logo));
            }
        });
        mHomeRv.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                new FinestWebView.Builder(getActivity())
                        .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down)
                        .webViewBuiltInZoomControls(true)
                        .showSwipeRefreshLayout(true)
                        .toolbarScrollFlags(0)
                        .webViewSupportZoom(true)
                        .statusBarColor(ContextCompat.getColor(getActivity(), R.color.primaryDarkColor))
                        .show(Check.checkReplace(((IndexDataModel) adapter.getData().get(position)).getUrl()));
            }
        });
    }

    @Override
    protected void initData() {
       mSwipeRefreshLayout.setRefreshing(true);
       getBasePresenter().getIndexNews("10");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.homeAction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeFListener) {
            mListener = (HomeFListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HomeFListener");
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
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showRetry() {

    }

    @Override
    public void hideRetry() {

    }

    @Override
    public void showError(String message) {
        mErrorView.setSubtitle(message);
    }

    @Override
    public Context context() {
        return getActivity();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showIndexNewList(List<IndexDataModel> models) {
        if (null != mHomeRv && models.size() > 0) {
            BaseQuickAdapter adapter = (BaseQuickAdapter) mHomeRv.getAdapter();
            adapter.setNewData(models);
            mErrorView.setVisibility(View.GONE);
        }
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    public interface HomeFListener {
        void homeAction(Uri uri);
    }
}
