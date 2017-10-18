package com.yb.btcinfo.platform;

import android.widget.Toast;

import com.yb.btcinfo.common.exception.ErrorMessageFactory;
import com.yb.btcinfo.repository.impl.MainRepostiory;
import com.yb.btcinfo.repository.manager.RepositoryManager;

import mvp.presenter.Presenter;


/**
 * Created by ericYang on 2017/5/18.
 * Email:eric.yang@huanmedia.com
 * what?
 */
public class PlatformMsgPresenter extends Presenter<PlatformMsgView> {
    private final MainRepostiory mRepository;

    public PlatformMsgPresenter() {
        this.mRepository = (MainRepostiory) RepositoryManager.getInstance().getRepositorys(RepositoryManager.DEFAULTREPOSITORY);
    }
    public void getPlatformNews(String id){
        addDisposable(mRepository.getPlatformNews(id,20+"").subscribe(platformListModes ->
        {
            if (!isNullView()) {
                getView().showPlatformNews(platformListModes);
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
