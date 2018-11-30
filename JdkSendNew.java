import com.tsystems.aviation.esb.client.EsbClient;
import com.tsystems.aviation.esb.esbEnum.EsbDataEnum;
import com.tsystems.aviation.esb.esbInterface.IBussReceiver;
import com.tsystems.aviation.esb.esbInterface.ISender;
import com.tsystems.aviation.esb.resultModel.EsbMessage;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.util.Date;

public class JdkSendNew extends AbstractJavaSamplerClient implements IBussReceiver {
    private static long start = 0;
    private static long end = 0;
    String topicName;
    String esbUrl;
    String appId;
    String appKey;
    String dataType;
    String message = "我发的消息是这个。";
    private EsbClient esbClient;
    private ISender iSender;

    @Override
    public void receiveData(EsbMessage message) throws Exception {
//        System.out.println("我发的消息。" + message.getMessageBody());
    }

    public void setupTest(JavaSamplerContext arg0) {
        try {
            getLogger().info(Thread.currentThread().getName() + "初始化");
            // 开始时间
            topicName = arg0.getParameter("topicName");
            esbUrl = arg0.getParameter("esbUrl");
            appId = arg0.getParameter("appId");
            appKey = arg0.getParameter("appKey");
            dataType = arg0.getParameter("dataType");
            message = arg0.getParameter("message");
            esbClient = new EsbClient(esbUrl, this);
            System.out.println("appID============="+appId+"登录");
            getLogger().info("appID=============" + appId);
            getLogger().info("appKey=============" + appKey);
            esbClient.login(appId, appKey);
            if (null == iSender) {
                if (dataType.contentEquals("1")) {
                    esbClient.connTopic(topicName, EsbDataEnum.NONSEQDATA);
                } else {
                    esbClient.connTopic(topicName, EsbDataEnum.SEQDATA);
                }
            }
            System.out.println(new Date());
            Date strdate = new Date();
        } catch (Exception e) {
            getLogger().error("appID=============" + appId + "setupTest===========" + e.toString());
            e.printStackTrace();
        }
    }

    public SampleResult runTest(JavaSamplerContext arg0) {
        SampleResult rs = new SampleResult();
        rs.sampleStart();
        boolean flag = false;
        int i = 0;
        try {
            if (null == iSender) {
                iSender = esbClient.getSenders().get(topicName);
            } else {
                start = System.currentTimeMillis();
                getLogger().info("拿到的esbClient是" + esbClient);
                iSender.sendMessage(message + Thread.currentThread().getName());
                flag = true;
                end = System.currentTimeMillis();
            }
        } catch (Exception e) {
            getLogger().error("appID=============" + appId + "runTest===========" + e.toString());
            e.printStackTrace();
        }
        rs.setSuccessful(flag);
        rs.sampleEnd();
        rs.getErrorCount();
        rs.getResponseCode();
        rs.getResponseHeaders();
        rs.getBodySize();
        rs.getBytes();
        return rs;
    }

    public void teardownTest(JavaSamplerContext arg0) {
        if (null != esbClient) {
            esbClient.logOut();
        }
    }

    //    @Override
    public Arguments getDefaultParameters() {
        Arguments arg0 = new Arguments();
        arg0.addArgument("topicName", "AirportToptic/RoadTypeService");
        arg0.addArgument("esbUrl", "http://10.255.36.126:8080/jcesb");
        arg0.addArgument("appId", "7IXBNT");
        arg0.addArgument("appKey", "3H2WLF");
        arg0.addArgument("dataType", "0");
        arg0.addArgument("message", null);

        return arg0;
//        return super.getDefaultParameters();
    }

    public static void main(String arg0[]) {
        Arguments arguments = new Arguments();
        arguments.addArgument("topicName", "AirportToptic/RoadTypeService");
        arguments.addArgument("esbUrl", "http://10.255.36.126:8080/jcesb");
        arguments.addArgument("appId", "A3266G");
        arguments.addArgument("appKey", "W17ZQI");
        arguments.addArgument("dataType", "1");
        JavaSamplerContext context = new JavaSamplerContext(arguments);
        JdkSendNew jdkSendNew = new JdkSendNew();
        jdkSendNew.setupTest(context);
        jdkSendNew.runTest(context);
        jdkSendNew.teardownTest(context);
    }
}
