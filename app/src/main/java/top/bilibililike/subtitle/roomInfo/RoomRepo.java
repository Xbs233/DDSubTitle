package top.bilibililike.subtitle.roomInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Xbs
 * @date 2020年1月19日14:59:27
 */
public class RoomRepo {
    /**
     * 面包狗 21421141  aqua 14917277  星街 190577 coco 21752686  高槻律 947447 peko 21560356
     *
     */
    private static final String[] ROOM_ID = new String[]{"21421141","14917277","190577","21752686","947447","21560356"};

    public static void getLivers(LiverCallback callback){
        List<RepoBean.DataBean.RoomInfoBean> resultList = new ArrayList<>();
        RoomIntercepter roomIntercepter = new RoomIntercepter(null);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(roomIntercepter)
                .build();
        Retrofit retrofit =  new Retrofit.Builder()
                .baseUrl("https://api.live.bilibili.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        RepoService service = retrofit.create(RepoService.class);

        Observable.fromArray(ROOM_ID)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .flatMap(service::getRoomData)
                .map(repoBean -> repoBean.getData().getRoom_info())
                .takeWhile(roomInfoBean -> roomInfoBean.getLive_status() != 0)
                .subscribe(new Observer<RepoBean.DataBean.RoomInfoBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RepoBean.DataBean.RoomInfoBean roomInfoBean) {
                        resultList.add(roomInfoBean);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        callback.onSuccess(resultList);
                    }
                });
    }

    interface LiverCallback{
        /**
         * callBack，用于获取Liver房间的回调
         * @param liverList 正在开播的Liver房间列表
         */
        void onSuccess(List<RepoBean.DataBean.RoomInfoBean> liverList);
    }

}
