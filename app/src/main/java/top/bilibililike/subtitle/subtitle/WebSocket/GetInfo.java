package top.bilibililike.subtitle.subtitle.WebSocket;

import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class GetInfo {
    private final int DEFAULT_COMMENT_PORT = 788;
    private final int PROTOCOL_VERSION = 1;
    public final int RECEIVE_BUFFER_SIZE = 10 * 1024;
    private Disposable disposable;

    public boolean sendSocketData(Socket socket, int total_len, int head_len, int version, int action, int param5, byte[] data){
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeInt(total_len);
            out.writeShort(head_len);
            out.writeShort(version);
            out.writeInt(action);
            out.writeInt(param5);
            if (data != null && data.length > 0) {
                out.write(data);
            }
            out.flush();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public boolean sendJoinRoomMsg(Socket socket, String roomID){
        String uid =  String.valueOf(new Random().nextInt(899999) + 100000);//生成随机Uid
        String jsonBody = "{\"roomid\": " + roomID + ", \"uid\": " + uid + "}";
        try {
            return sendSocketData(socket, jsonBody.length() + 16, 16, PROTOCOL_VERSION, 7, 1, jsonBody.getBytes("utf-8"));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public Socket connect(String roomId){
        String socketServerUrl = "broadcastlv.chat.bilibili.com";
        final Socket socket  = new Socket();
        InetSocketAddress address = new InetSocketAddress(socketServerUrl, DEFAULT_COMMENT_PORT);
        try {
            socket.setReceiveBufferSize(RECEIVE_BUFFER_SIZE);
            socket.connect(address);
            if(sendJoinRoomMsg(socket, roomId)){
                Observable observable = Observable.interval(1000, TimeUnit.MILLISECONDS)
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .doOnNext(aLong -> {
                            sendSocketData(socket, 16, 16, PROTOCOL_VERSION, 2, 1, null);
                            System.out.println("TAG弹幕 sendHeartBeat");
                        })
                        .doOnDispose(new Action() {
                            @Override
                            public void run() throws Exception {
                                System.out.println("TAG弹幕  onDispose");
                            }
                        })
                        .doOnError(Throwable::printStackTrace)
                        ;

                disposable = observable.subscribe();
                return socket;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return socket;
    }

    public void stopHeartbeat(){
        if (disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
    }

}
