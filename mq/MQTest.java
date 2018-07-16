package mq;

import com.ibm.mq.*;

/*
 * 可以在MQ的资源管理器的某一个队列上放入测试消息、浏览消息等
 * 可以放入多条消息，按先进先出的方式取得
 */
public class MQTest {

    private String qManager;        // QueueManager名
    private MQQueueManager qMgr;    // 队列管理器名称
    private MQQueue qQueue;         // 消息通道
    String HOST_NAME;               // 主机名，在这里我填写了IP地址
    int PORT = 0;                   // 端口号
    String Q_NAME;                  // 本地队列
    String CHANNEL;                 // 连接通道
    int CCSID;
    String Msg;
//    String userID;
//    String password;

    /**
     * 初始化
     */
    public void init() {
        try {
//            HOST_NAME = "192.168.102.38";
//            HOST_NAME = "192.168.102.38";
            HOST_NAME = "192.168.102.24";
            PORT = 1414;
            qManager = "FOTIC_QMGR_GW";
            Q_NAME = "test_Monitor";
            CHANNEL = "SYSTEM.BKR.CONFIG";
            CCSID = 1208; // 表示是简体中文，
            MQEnvironment.hostname = HOST_NAME;
            MQEnvironment.port = PORT;
            MQEnvironment.channel = CHANNEL;
            MQEnvironment.CCSID = CCSID;
            MQEnvironment.userID ="mqm";
            MQEnvironment.password="123456";
            qMgr = new MQQueueManager(qManager);
            int qOptioin = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_INQUIRE | MQC.MQOO_OUTPUT;
            qQueue = qMgr.accessQueue(Q_NAME, qOptioin);
        } catch (MQException e) {
            e.printStackTrace();
            System.out.println("发生了一起异常，异常原因：" + e.reasonCode);
        }
    }

    public void finalizer() {
        try {
            if (qQueue!=null)
            qQueue.close();
            if (qMgr!=null)
            qMgr.disconnect();
        } catch (MQException e) {
            System.out.println("发生了一起异常，异常原因：" + e.reasonCode);
        }
    }
    /*
     * 获取消息
     */
    public void GetMsg() throws ClassNotFoundException {
        try {
            MQMessage revMessage = new MQMessage();
            MQGetMessageOptions gmo = new MQGetMessageOptions();
            revMessage.characterSet=CCSID;
            revMessage.encoding=CCSID;
            gmo.options += MQC.MQPMO_SYNCPOINT;
            qQueue.get(revMessage, gmo);

            String revString = revMessage.readStringOfByteLength(revMessage.getMessageLength());
            System.out.println("接收到的内容："+revString);

        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (MQException e) {
            if (e.reasonCode != 2033) // 没有消息
            {
                System.out.println("发生了一起异常，异常原因：" + e.reasonCode);
                e.printStackTrace();
            }
        } catch (java.io.IOException e) {
            System.out.println("发生了一起IO异常：" + e);
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param msgStr
     */
    public void SendMsg(String msgStr) {
        try {
            MQMessage qMsg = new MQMessage();
            qMsg.encoding=CCSID;
            qMsg.characterSet=CCSID;
            qMsg.writeString(msgStr);
            MQPutMessageOptions pmo = new MQPutMessageOptions();
            qQueue.put(qMsg, pmo);
            System.out.println("消息发送成功！发送的内容是：" + msgStr);
        } catch (MQException e) {
            System.out.println("发生了一起异常，异常原因：" + e.reasonCode);
        } catch (java.io.IOException e) {
            System.out.println("发生了一起IO异常：" + e);
        }
    }

    public MQTest() {
        init();
    }


}