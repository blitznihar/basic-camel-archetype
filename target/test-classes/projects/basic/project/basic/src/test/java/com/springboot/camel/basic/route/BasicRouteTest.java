package com.springboot.camel.basic.route;

import static org.junit.Assert.assertTrue;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@RunWith(CamelSpringBootRunner.class)
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class BasicRouteTest {

	String messageOut;
	@Autowired
	ProducerTemplate producerTemplate;
	
	@Autowired
	Environment environment;
	
	@Test
	public void testMoveMQtoMQ() throws InterruptedException {
		
		String fileName = "testEmployeeSalaryProcessing.txt";
		String routeFrom = "routeIn";
		String message = "employeeId,rates\n"+
							"EM001, 140.00\n"+
							"EM016, 180.00\n"+
							"EM018, 150.00\n"+
							"EM031, 130.00\n"+
							"EM034, 120.00\n";
		producerTemplate.sendBodyAndHeader(environment.getProperty(routeFrom), message,Exchange.FILE_NAME,fileName);
		Thread.sleep(3000);
		System.out.println(message);
		assertTrue(!message.isEmpty());
		System.out.println(message);
	}
	
	@JmsListener(destination = "java2blog.queue")
	public void receiveQueue(String text) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		messageOut = text;
	}
	
}
