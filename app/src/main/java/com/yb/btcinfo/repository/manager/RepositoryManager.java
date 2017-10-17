package com.yb.btcinfo.repository.manager;

import android.support.annotation.IntDef;

import com.yb.btcinfo.common.FApplication;
import com.yb.btcinfo.common.manager.ResourceManager;
import com.yb.btcinfo.repository.DataSource;
import com.yb.btcinfo.repository.impl.MainRepostiory;
import com.yb.btcinfo.repository.impl.UserRepository;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ericYang on 2017/5/24.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class RepositoryManager {
    public static final int USERREPOSITORY=1;
    public static final int DEFAULTREPOSITORY=2;

    /**
     * defined Repository Type
     */
    @IntDef({USERREPOSITORY,DEFAULTREPOSITORY})
    @Retention(RetentionPolicy.SOURCE)
    public  @interface  RepositoryTpye {
    }


    private Map<Integer,DataSource> repositorys =new HashMap<>();
    private static RepositoryManager instance;

    private RepositoryManager() {
        initRepository();
    }

    private void initRepository() {
        repositorys.put(USERREPOSITORY
                ,new UserRepository(FApplication.getApplication()
                ,ResourceManager.getInstance().getUserApiService()
                ,ResourceManager.getInstance().getUserCacheProviders()
                ,ResourceManager.getInstance().getDefaultThreadProvider()));
        repositorys.put(DEFAULTREPOSITORY
                , new MainRepostiory(ResourceManager.getInstance().getDefaultApiService()
                ,ResourceManager.getInstance().getCacheProviders()
                ,ResourceManager.getInstance().getDefaultThreadProvider()));
    }

    /**
     * get instance for type
     * @param type
     * @return
     */
    public DataSource getRepositorys(@RepositoryTpye int type) {
        return repositorys.get(type);
    }

    /**
     * RepositoryManager be use for manager Repository instance
     * @return
     */
    public static RepositoryManager getInstance() {
        if (instance==null){
            synchronized (RepositoryManager.class){
                if (instance==null){
                    instance=new RepositoryManager();
                }
            }
        }
        return instance;
    }

}
