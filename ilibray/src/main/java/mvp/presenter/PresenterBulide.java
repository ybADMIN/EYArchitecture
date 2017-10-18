package mvp.presenter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by ericYang on 2017/10/18.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class PresenterBulide {

    @SuppressWarnings("unchecked")
    public static <P extends Presenter> P newPresenterInstance(Object obj) {
        Type genType = obj.getClass().getGenericSuperclass();
        P presenter = null;
        try {
            if (!(genType instanceof  ParameterizedType))return null;

            Class<P> entityClass = (Class<P>) ((ParameterizedType) genType).getActualTypeArguments()[0];
            if (entityClass!=null && !entityClass.equals(Presenter.class)){
                presenter = entityClass.newInstance();
            }
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return presenter;
    }
}
