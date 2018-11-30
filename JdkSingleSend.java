import com.tsystems.aviation.esb.connector.JmsEsbConnector;
import com.tsystems.aviation.esb.esbEnum.EsbClientEnum;
import com.tsystems.aviation.esb.esbEnum.EsbDataEnum;
import com.tsystems.aviation.esb.util.EsbMessageUtil;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import javax.jms.Message;

public class JdkSingleSend extends AbstractJavaSamplerClient {
    private static long start = 0;
    private static long end = 0;

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult rs = new SampleResult();
        rs.sampleStart();
        boolean flag = false;

        try {
            JmsEsbConnector jmsEsbConnector = new JmsEsbConnector("10.255.36.171", 1414, "SYSTEM.BKR.CONFIG", "QMESB", "AirportToptic/RoadTypeService", "aviation", "aviation", EsbClientEnum.PTOPIC);

            jmsEsbConnector.connect();

            Message message = EsbMessageUtil.genTextMessage(jmsEsbConnector.getSession(), "222", "222", "222", 1, "APP_0EK3VQ_BUSI", EsbDataEnum.SEQDATA, "sfsdf", "fff", "222222222222");
            start = System.currentTimeMillis();

            jmsEsbConnector.produce(message);
            flag = true;
            jmsEsbConnector.disconnect();
            rs.setSuccessful(flag);
            rs.sampleEnd();
            end = System.currentTimeMillis();
        } catch (Exception ex) {
            getLogger().error("runTest===========" + ex.toString());
            ex.printStackTrace();
        }

        rs.getErrorCount();
        rs.getResponseCode();
        rs.getResponseHeaders();
        rs.getBodySize();
        rs.getBytes();
        return rs;
    }

    public static void main(String arg0[]) {
        Arguments arguments = new Arguments();
        JavaSamplerContext context = new JavaSamplerContext(arguments);
        JdkSingleSend jdkSendNew = new JdkSingleSend();
        jdkSendNew.setupTest(context);
        jdkSendNew.runTest(context);
        jdkSendNew.teardownTest(context);
    }
}
