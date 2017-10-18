package com.yb.btcinfo.main.fragment;

import android.widget.Toast;

import com.yb.btcinfo.common.exception.ErrorMessageFactory;
import com.yb.btcinfo.main.model.mapper.UserModelDataMapper;
import com.yb.btcinfo.repository.impl.MainRepostiory;
import com.yb.btcinfo.repository.manager.RepositoryManager;
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
        this.mRepository = (MainRepostiory) RepositoryManager.getInstance().getRepositorys(RepositoryManager.DEFAULTREPOSITORY);
    }

    public void getIndexNews(String number) {
        addDisposable(mRepository.getIndexNews(number)
                .retryWhen(new RetryWhenHandler(5)).subscribe(indexDataModels ->
        {
            if (!isNullView()) {
                getView().showIndexNewList(indexDataModels);
            }
        }, throwable -> {
            if (!isNullView()) {
                Toast.makeText(getView().context(),
                        ErrorMessageFactory.create(getView().context(), throwable), Toast.LENGTH_SHORT).show();
            }
        }));
    }


    @Override
    public void destroy() {
        dispose();
    }
}
