package com.info.shcedule;

import com.info.schedule.ConsoleManager;
import com.info.schedule.core.TaskDefine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;


/**
 * @author juny.ye
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath*:/applicationContext1.xml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ZookeeperTest {

	@Test
	public void testCreateLocalTaskHiveParam() throws Exception {
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext1.xml");
		Thread.sleep(1000);
		Map<String, String> params = new HashMap();
		params.put("name", "chenghongchao");
		params.put("age", "28");
		//print5
		TaskDefine taskDefine5 = new TaskDefine();
		taskDefine5.setTargetBean("simpleTask");
		taskDefine5.setTargetMethod("print3");
		taskDefine5.setTaskDefineName("task3");
		taskDefine5.setPeriod(10000);
//		taskDefine5.setParams(new Gson().toJson(params));
		ConsoleManager.addScheduleTask(taskDefine5);

	}

	@Test
	public void testInterface () throws Exception {
		TaskDefine taskDefine5 = new TaskDefine();
		taskDefine5.setTargetBean("JDBCToESTask");
		taskDefine5.setTargetMethod("JDBCSinkES");
		taskDefine5.setTaskDefineName("testInterface");
		taskDefine5.setPeriod(10000);
//		taskDefine5.setParams(new Gson().toJson(params));
		ConsoleManager.addScheduleTask(taskDefine5);
	}
}
