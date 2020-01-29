package top.bilibililike.subtitle.roomInfo;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RepoService {

    /**
     * https://api.live.bilibili.com/xlive/app-room/v1/index/getInfoByRoom?access_key=714c96f2612cd82609c480ee85b43211&actionKey=appkey&appkey=1d8b6e7d45233436&build=5521100&channel=xiaomi&device=android&mobi_app=android&platform=android&room_id=21560356&ts=1579088024&sign=e077df65bd51e31e90637ee14ec92bab
     *
     * @return Observer
     */
    @GET("xlive/app-room/v1/index/getInfoByRoom")
    Observable<RepoBean> getRoomData();
}
