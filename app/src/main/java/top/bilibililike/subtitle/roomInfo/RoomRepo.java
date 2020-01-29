package top.bilibililike.subtitle.roomInfo;

import android.util.Log;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import top.bilibililike.subtitle.utils.LiverRepo;

/**
 * @author Xbs
 * @date 2020年1月19日14:59:27
 */
public class RoomRepo {
    /**
     * 面包狗 21421141  aqua 14917277  星街 190577 coco 21752686  peko 21560356 狗妈 21304638 心心 14275133
     *
     */
    private static final String[] ROOM_ID = new String[]
            {LiverRepo.MINATO_AQUA,LiverRepo.USADA_PEKORA,LiverRepo.URUHA_RUSHIA,LiverRepo.SUISEI,LiverRepo.MATSURI,LiverRepo.KAGURA_MEA,
             LiverRepo.SORA,LiverRepo.KIRYUU_COCO,LiverRepo.HIMEMORI_LUNA,LiverRepo.AMANE_KANATA
            };

    public static void getLivers(LiverCallback callback){
        List<RepoBean.DataBean> resultList = new ArrayList<>();
        RoomIntercepter roomIntercepter = new RoomIntercepter(null);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(roomIntercepter)
                .build();
        Retrofit retrofit =  new Retrofit.Builder()
                .baseUrl("https://api.live.bilibili.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        RepoService service = retrofit.create(RepoService.class);

        Observable.fromArray(ROOM_ID)
                .subscribeOn(Schedulers.io())
                .flatMap(roomId -> {
                    roomIntercepter.replaceRoom(roomId);
                    return service.getRoomData();
                })
                .map(repoBean -> {
                    if (repoBean.getCode() == 0) {
                        return repoBean.getData();
                    }else {
                        Observable.error(new Throwable("sign Error"));
                        return repoBean.getData();
                    }
                })
                .retryWhen(throwableObservable -> throwableObservable
                        .flatMap((Function<Throwable, ObservableSource<?>>)
                                throwable -> {
                                    Log.d("RoomRepo",throwable.toString());
                                    roomIntercepter.replaceParam(null);
                                    resultList.clear();
                                    return Observable.timer(100,TimeUnit.MILLISECONDS);
                                })
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RepoBean.DataBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RepoBean.DataBean dataBean) {
                        if (dataBean.getRoom_info().getLive_status() == 1){
                            resultList.add(dataBean);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        callback.onSuccess(resultList);
                    }
                });
    }

    public interface LiverCallback{
        /**
         * callBack，用于获取Liver房间的回调
         * @param liverList 正在开播的Liver房间列表
         */
        void onSuccess(List<RepoBean.DataBean> liverList);

        void onStartLoading();
            //todo show dialog


        public void onError(String reason);
    }

}
