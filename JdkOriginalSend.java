import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.visualizers.Sample;

import java.util.Hashtable;

public class JdkOriginalSend extends AbstractJavaSamplerClient {
    @SuppressWarnings("rawtypes")
    private static Hashtable<String, Comparable> env = new Hashtable<String, Comparable>();
    // 队列管理器名
    private static String queueManagerName;
    // 队列管理器引用
    private static MQQueueManager queueManager;
    // 队列名
    private static String queueName;
    // 队列引用
    private MQQueue queue;
    public Arguments getDefaultParameters() {
        Arguments arguments = new Arguments();
        arguments.addArgument("HOST_NAME_PROPERTY","10.255.36.82");
        return arguments;
    }

    public void setupTest(JavaSamplerContext context)  {
        // 服务器地id、名称
        env.put(MQConstants.HOST_NAME_PROPERTY, "10.255.36.82");
        // 连接通道
        env.put(MQConstants.CHANNEL_PROPERTY, "SYSTEM.BKR.CONFIG");
//		env.put(MQConstants.CHANNEL_PROPERTY, "NB.DC.CH");
        // 服务器MQ服务使用的编1381代表GBK,1208代表UTF(Coded Character Set Identifier:CCSID)
        env.put(MQConstants.CCSID_PROPERTY, 1208);
        // 端口号
        env.put(MQConstants.PORT_PROPERTY, 1414);
        //用户名
//		/env.put(MQConstants.USERID_PROPERTY, "1414");
        // 传输类型
        env.put(MQConstants.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES);

        // 设置目标队列管理器
//		queueManagerName = "NB_WBI_QM";
        queueManagerName = "QMESB";
        // 设置目标队列
//		queueName = "NB.GZPT.MD.GET";
        queueName = "APP_010_BUSI";
        //MQ中拥有权限的用户名
        MQEnvironment.userID = "aviation";
        //用户名对应的密码
        MQEnvironment.password = "aviation";

        // 建立队列管理器连接
        try {
            connectQM();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void connectQM() throws Exception {
        queueManager = new MQQueueManager(queueManagerName, env);
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult rs = new SampleResult();
        rs.sampleStart();

        try {
        // 队列打开参数
        int openOptions = MQConstants.MQOO_BIND_AS_Q_DEF
                | MQConstants.MQOO_OUTPUT;
        // 打开队列(同一线程内，同时只能打开该队列一次)
        queue = queueManager.accessQueue(queueName, openOptions);
        // 设置发送消息参数为：具有同步性，及支持事务
        MQPutMessageOptions pmo = new MQPutMessageOptions();
        pmo.options = MQConstants.MQPMO_SYNCPOINT;

            // 发送消息(这是为同时多条消息发送）
            for (int i = 0; i < 1; i++) {
                // 设置消息格式为字符串类型
                MQMessage msg = new MQMessage();
                msg.format = MQConstants.MQFMT_STRING;
                /*
                 * 设置自定义消息头
                 */
                msg.setStringProperty("service_id", "BS001"); // 服务id
                msg.setStringProperty("order_dispatch_type_code", "0");// 医嘱小分类
                msg.setStringProperty("domain_id", "0");// 域id
                msg.setStringProperty("exec_unit_id", "0");// 执行科室

                // 消息内容编码(1208:utf-8)
                msg.characterSet = 1208;
                // 消息内容
                String message = "<msg>你好<msg>";
                // 设置消息内容
                msg.writeString(message);
                // 发??消息
                queue.put(msg, pmo);
                rs.getSamplerData();
                rs.setSuccessful(true);

            }
            // 提交事务
            queueManager.commit();

        } catch (Exception e) {
            // 事务回滚
            try {
                queueManager.backout();
            } catch (MQException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            rs.setSuccessful(false);
        } finally {
            // 关闭队列
            if (queue != null) {
                try {
                    queue.close();
                } catch (MQException e) {
                    e.printStackTrace();
                }
            }
            rs.sampleEnd();
        }

        return rs;
    }


    public void teardownTest(JavaSamplerContext context) {
        try {
            disconnectQM();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void disconnectQM() throws Exception {
        if (queueManager != null) {
            queueManager.disconnect();
        }
    }


    public static void main(String []args){
        Arguments arguments = new Arguments();
        JavaSamplerContext context = new JavaSamplerContext(arguments);
        JdkOriginalSend jdkSendNew = new JdkOriginalSend();
        jdkSendNew.setupTest(context);
        jdkSendNew.runTest(context);
        jdkSendNew.teardownTest(context);
    }
}
