package com.yb.btcinfo.main.fragment;

import com.yb.btcinfo.common.exception.ErrorMessageFactory;
import com.yb.btcinfo.main.datamodel.mapper.UserModelDataMapper;
import com.yb.btcinfo.repository.datasouce.impl.MainRepostiory;

import mvp.data.net.converter.RetryWhenHandler;
import mvp.presenter.Presenter;


/**
 * Created by ericYang on 2017/5/18.
 * Email:eric.yang@huanmedia.com
 * what?
 */
public class HomePresenter extends Presenter<HomeView> {
    private final MainRepostiory mRepository;
    private UserModelDataMapper mUserModelDataMapper = new UserModelDataMapper();

    public HomePresenter() {
        this.mRepository = new MainRepostiory();
    }

    public void getIndexNews(String number) {
        addDisposable(mRepository.getIndexNews(number)
                .retryWhen(new RetryWhenHandler(3)).subscribe(indexDataModels ->
                {
                    if (!isNullView()) {
                        getView().hideLoading();
                        getView().showIndexNewList(indexDataModels);
                    }
                }, throwable -> {
                    if (!isNullView()) {
                        getView().hideLoading();
                        getView().showError(ErrorMessageFactory.create(getView().context(), throwable));
                    }
                }));
    }


    @Override
    public void destroy() {
        dispose();
    }
}
