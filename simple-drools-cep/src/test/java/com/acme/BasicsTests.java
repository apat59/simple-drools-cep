package com.acme;

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
		//kieSession.fireAllRules();
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
		
		//pseudoClock.advanceTime(4,TimeUnit.SECONDS);
		System.out.println(clock.getCurrentTime());
		//pseudoClock.advanceTime(7,TimeUnit.SECONDS);
		Thread.sleep(21000);
		//cepService.advancedTime();
		//cepService.forceFire();
		kieSession.dispose();	
		kieContainer.dispose();
	}
}
