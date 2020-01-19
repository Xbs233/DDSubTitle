package top.bilibililike.subtitle.WebSocket;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.Socket;

public class SocketDataThread implements Runnable {
    Socket socket;
    String roomId;
    GetInfo client;
    private boolean keepRunning = true;
    private DanmakuCallBack callBack;

    public void start(String roomId) {
        this.roomId = roomId;
        client = new GetInfo();
    }

    public void bind(DanmakuCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void run() {
        socket = client.connect(this.roomId);
        if (socket != null) {
            HandleDataThread hdp = new HandleDataThread();
            try {
                hdp.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hdp.start();
        }
    }

    private class HandleDataThread extends Thread {
        DataInputStream input = null;

        @Override
        public void run() {
            super.run();
            if (socket != null) {
                int bufferSize = 10 * 1024;
                try {
                    bufferSize = socket.getReceiveBufferSize();
                    System.out.println("连接成功" + "真实直播间ID：" + roomId);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                byte[] ret = new byte[bufferSize];
                while (keepRunning) {
                    try {
                        input = new DataInputStream(socket.getInputStream());
                        int retLength = input.read(ret);
                        if (retLength > 0 && keepRunning) {
                            byte[] recvData = new byte[retLength];
                            System.arraycopy(ret, 0, recvData, 0, retLength);
                            analyzeData(recvData);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void analyzeData(byte[] data) {

            int dataLength = data.length;
            if (dataLength < 16) {
                System.out.println("错误的数据");
            } else {
                DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(data));
                try {
                    int msgLength = inputStream.readInt();
                    if (msgLength < 16) {
                        System.out.println("可能需要扩大缓冲区大小");
                    } else if (msgLength > 16 && msgLength == dataLength) {
                        // 其实是两个char
                        inputStream.readInt();
                        int action = inputStream.readInt() - 1;
                        // 直播间在线用户数目

                        if (action == 2) {
                            inputStream.readInt();
                            int userCount = inputStream.readInt();
                            System.out.println("人气：" + userCount);
                        } else if (action == 4) {
                            inputStream.readInt();
                            int msgBodyLength = dataLength - 16;
                            byte[] msgBody = new byte[msgBodyLength];
                            if (inputStream.read(msgBody) == msgBodyLength) {
                                String jsonStr = new String(msgBody, "utf-8");
                                //System.out.println(jsonStr);
                                JSONObject jsonObject = new JSONObject(jsonStr);
                                String cmd = (String) jsonObject.get("cmd");
                                if (cmd.equals("DANMU_MSG")) {
                                    JSONArray list = jsonObject.getJSONArray("info");
                                    String danMuData = list.getString(1);
                                    //System.out.println("弹幕消息 = " + danMuData);
                                    if (danMuData.matches("(.*)【(.*)】|(.*)【(.*)")) {
                                        StringBuilder builder = new StringBuilder(danMuData);
                                        if (danMuData.startsWith("[") || danMuData.startsWith("【")) {
                                            builder.deleteCharAt(0);
                                        }
                                        if (danMuData.endsWith("]") || danMuData.endsWith("】")){
                                            builder.deleteCharAt(builder.length() - 1);
                                        }
                                        callBack.onShow(builder.toString());
                                        Log.d("Subtitle",builder.toString());
                                    }

                                }
                            }
                        }
                    } else if (msgLength > 16 && msgLength < dataLength) {
                        byte[] singleData = new byte[msgLength];
                        System.arraycopy(data, 0, singleData, 0, msgLength);
                        analyzeData(singleData);
                        int remainLen = dataLength - msgLength;
                        byte[] remainDate = new byte[remainLen];
                        System.arraycopy(data, msgLength, remainDate, 0, remainLen);
                        analyzeData(remainDate);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
