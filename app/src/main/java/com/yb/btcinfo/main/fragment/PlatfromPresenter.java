package com.yb.btcinfo.main.fragment;

import android.widget.Toast;

import com.yb.btcinfo.common.exception.ErrorMessageFactory;
import com.yb.btcinfo.main.datamodel.mapper.UserModelDataMapper;
import com.yb.btcinfo.repository.datasouce.impl.MainRepostiory;

import mvp.presenter.Presenter;


/**
 * Created by ericYang on 2017/5/18.
 * Email:eric.yang@huanmedia.com
 * what?
 */
public class PlatfromPresenter extends Presenter<PlatfromView> {
    private final MainRepostiory mRepository;
    private UserModelDataMapper mUserModelDataMapper = new UserModelDataMapper();

    public PlatfromPresenter() {
        this.mRepository = new MainRepostiory();
    }

    public void getPlatfroms() {
        addDisposable(mRepository.getSupportPlatform().subscribe(indexDataModels ->
        {
            if (!isNullView()) {
                getView().showPlatfromList(indexDataModels);
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
