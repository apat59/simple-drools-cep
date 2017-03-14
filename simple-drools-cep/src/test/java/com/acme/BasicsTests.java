package com.acme;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.jboss.ddoyle.drools.cep.demo.model.ExamEvent;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.time.SessionClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class BasicsTests {
	private static Logger logger = LoggerFactory.getLogger("com.acme.BasicsTests");
	
	
	/*
	 * Doc states
	 * Every event has an associated timestamp assigned to it. 
	 * By default, the timestamp for a given event is read from the Session Clock and assigned to the event at the time 
	 * the event is inserted into the working memory. 
	 * Although, sometimes, the event has the timestamp as one of its own attributes. 
	 * In this case, the user may tell the engine to use the timestamp from the event's attribute instead of reading it from the Session Clock.
	 * 
	 * Misunderstanding the doc?
	 * Actually the consequence is that ALL features that are supposed to work with timestamp will not work as expected, especially timer.
	 */
	@Test
	public void timeStampTest() throws InterruptedException{
		logger.info("Initialize KIE.");
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();

		// Initializing KieSession.
		logger.info("Creating KieSession.");
		final KieSession kieSession = kieContainer.newKieSession("ksession-rules-test-realtime");
		new Thread(new Runnable() {
            public void run() {
            	kieSession.fireUntilHalt();
            }
        }).start();
		SessionClock clock = kieSession.getSessionClock();
		kieSession.setGlobal("clock", clock);
		
		
		long sec10Later = clock.getCurrentTime() + 10000;
		//long sec20Later = clock.getCurrentTime() + 20000;
		ExamEvent scheduledOntimeEvent = new ExamEvent("launch", "SCHEDULE", "ontime", sec10Later,"");
		//ExamEvent actualOntimeEvent = new ExamEvent("launch", "SUCCESS", "ontime", sec20Later,"");
		
		String stream = "GUMTREE";
		logger.debug("Inserting fact with id: '" + scheduledOntimeEvent.getSessionId() + "' into stream: " + stream);
		EntryPoint ep = kieSession.getEntryPoint(stream);
		ep.insert(scheduledOntimeEvent);
		
		System.out.println(clock.getCurrentTime());
		// just in case my understanding of the doc is correct
		// btw allows to test the timer using real time clock
		Thread.sleep(12000);
		kieSession.dispose();	
		kieContainer.dispose();
	}
	/*
	 * we have the Exam start late rule that trigger is a SUCCESS event is not inserted within 5s
	 * testing using pseudo clock
	 * Using the realtime clock, it works
	 */
	@Test
	public void pseudoClockTest() throws InterruptedException{
		logger.info("Initialize KIE.");
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();

		// Initializing KieSession.
		logger.info("Creating KieSession.");
		final KieSession kieSession = kieContainer.newKieSession("ksession-rules-test-pseudo");
		new Thread(new Runnable() {
            public void run() {
            	kieSession.fireUntilHalt();
            }
        }).start();
		SessionPseudoClock clock = (SessionPseudoClock)kieSession.getSessionClock();
		kieSession.setGlobal("clock", clock);
		
		
		//long sec10Later = clock.getCurrentTime() + 10000;
		//long sec20Later = clock.getCurrentTime() + 20000;
		ExamEvent scheduledOntimeEvent = new ExamEvent("launch", "SCHEDULE", "ontime", clock.getCurrentTime(),"");
		//ExamEvent actualOntimeEvent = new ExamEvent("launch", "SUCCESS", "ontime", sec20Later,"");
		
		String stream = "GUMTREE";
		logger.debug("Inserting fact with id: '" + scheduledOntimeEvent.getSessionId() + "' into stream: " + stream);
		EntryPoint ep = kieSession.getEntryPoint(stream);
		ep.insert(scheduledOntimeEvent);
		
		// timer set to 5 sec, so we go far later
		clock.advanceTime(10,TimeUnit.SECONDS);
		System.out.println(clock.getCurrentTime());
		// just in case
		Thread.sleep(500);
		kieSession.dispose();	
		kieContainer.dispose();
	}
}
