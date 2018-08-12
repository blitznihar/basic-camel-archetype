#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package com.springboot.camel.basic.route;

import static org.junit.Assert.assertTrue;


import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;



import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;


@ActiveProfiles("unittest")
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
		
		String message = "employeeId,rates\n"+
							"EM001, 140.00\n"+
							"EM016, 180.00\n"+
							"EM018, 150.00\n"+
							"EM031, 130.00\n"+
							"EM034, 120.00\n";
		producerTemplate.sendBody("{{routeIn}}", message);
		//Thread.sleep(10000);
		
		try {

            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("foo");

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            // Wait for a message
            Message messageO = consumer.receive(1000);

            if (messageO instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) messageO;
                String text = textMessage.getText();
                messageOut=text;
                System.out.println("Received: " + text);
            } else {
                System.out.println("Received: " + messageO);
            }
           
            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }

		System.out.println(messageOut);
		assertTrue(!messageOut.isEmpty());
		System.out.println(messageOut);
	}
	
	
}
