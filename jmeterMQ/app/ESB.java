package app;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import mq.*;
//import com.passpod.core.t8.*;

/**
 * @author 椰子
 * <p>
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ESB extends AbstractJavaSamplerClient {
    private SampleResult results;
    private String msgStr;
//    private String host;
//    private String port;

    //初始化方法，实际运行时每个线程仅执行一次，在测试方法运行前执行，类似于LoadRunner中的init方法
    public void setupTest(JavaSamplerContext arg0) {
        results = new SampleResult();
        msgStr = arg0.getParameter("msgStr", "");
        if (msgStr != null && msgStr.length() > 0) {
            results.setSamplerData(msgStr);
        }
    }

    //设置传入的参数，可以设置多个，已设置的参数会显示到Jmeter的参数列表中
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("msgStr", "");   //定义一个参数，显示到Jmeter的参数列表中，第一个参数为参数默认的显示名称，第二个参数为默认值
//        params.addArgument("host", "");
//        params.addArgument("port", "");
        return params;
    }


    //测试执行的循环体，根据线程数和循环次数的不同可执行多次，类似于LoadRunner中的Action方法
    public SampleResult runTest(JavaSamplerContext arg0) {
        SampleResult results = new SampleResult();  // 定义SampleResult 为子变量，避免并发超过1个线程错误
        results.sampleStart();     //定义一个事务，表示这是事务的起始点，类似于LoadRunner的lr.start_transaction
//        int len = 0;
//        len = msgStr.length();

        if (msgStr == null) {
            System.out.println("参数不能为空！");
            results.setSuccessful(false);   //用于设置运行结果的成功或失败，如果是"false"则表示结果失败，否则则表示成功
        } else {
            MQTest mqst = new MQTest();
            try {
//                mqst.SendMsg("你好,我是一条测试消息!");
                mqst.SendMsg(msgStr);
                mqst.GetMsg();
                results.setSuccessful(true);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mqst.finalizer();
                results.sampleEnd();     //定义一个事务，表示这是事务的结束点，类似于LoadRunner的lr.end_transaction
            }
        }
        return results;
    }

    //结束方法，实际运行时每个线程仅执行一次，在测试方法运行结束后执行，类似于LoadRunner中的end方法
    public void teardownTest(JavaSamplerContext arg0) {
    }

//    public static void main(String[] args) {
//        ESB esb = new ESB();
//
//        Arguments arguments = new Arguments();
//        arguments.addArgument("msgStr","1212121");
//
//        JavaSamplerContext context = new JavaSamplerContext(arguments);
//
//        esb.setupTest(context);
//        esb.runTest(context);
//    }

}