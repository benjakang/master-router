package com.sustc.masterrouter;

import com.sustc.masterrouter.domain.Evaluator;
import com.sustc.masterrouter.service.Master;
import com.sustc.masterrouter.service.Router;
import com.sustc.masterrouter.utils.JSONUtil;
import com.sustc.masterrouter.utils.MillisecondClock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisApplicationTests {

    @Autowired
    Master master;

    @Test
    public void test(){
        long s = MillisecondClock.CLOCK.now();
        long s1 = System.currentTimeMillis();
        System.out.println(s);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(MillisecondClock.CLOCK.now());
//        System.out.println(System.currentTimeMillis());
    }


//
//    @Autowired
//    Router router;
//
//
//    @Before
//    public void initClient(){
//        Evaluator eva0 = new Evaluator("localhost", 10000);
//        Evaluator eva1 = new Evaluator("localhost", 10001);
//        Evaluator eva2 = new Evaluator("localhost", 10002);
////        eva0.setStarted(true);
////        eva1.setStarted(true);
////        eva2.setStarted(true);
//        router.getEvaluators().add(eva0);
//        router.getEvaluators().add(eva1);
//        router.getEvaluators().add(eva2);
//    }
//
////    @Before
//    public void initServer(){
//        master.startTCPServer();
//    }
//
//    @Test
//    public void testFile(){
//        File f = new File("C:\\Users\\kangbingjie\\Desktop\\my_poss.py");
//        master.file(f);
//    }
//
//    @Test
//    public void testStart(){
//        master.start(200, 5, 6);
//        System.out.println(master.getLeftNeedEva());
//    }
//
//    @Test
//    public void testStop(){
//        master.stop();
//    }
//
//    @Test
//    public void testQuery(){
//        testStart();
//        master.query();
//        System.out.println("query后："+master.getLeftNeedEva());
//        Evaluator eva = router.getEvaluators().get(1);
//        String info = JSONUtil.beanToJson(eva);
//        System.out.println(info);
//
//    }
//
//    @Test
//    public void testRegister() throws InterruptedException {
////        ats.executeAsyncTask("xianc");
//////        Thread.sleep(5000);
////        System.out.println(11111);
////        for (Evaluator eva : router.getEvaluators()) {
////            System.out.println(eva.getIp() + ":" + eva.getPort());
////        }
////
////        Thread.sleep(1000000);
////        router.ssss();
//        System.out.println("ll");
//    }
//
//    @Test
//    public void test(){
//
//        Thread thread = master.startTCPServer();
//        System.out.println(1111111111);
//        testQuery();
//
//        try {
//            Thread.sleep(20000);
//
//            for (Evaluator eva : router.getEvaluators()) {
//                System.out.println(eva.getPort());
//            }
//
//            thread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }

}
