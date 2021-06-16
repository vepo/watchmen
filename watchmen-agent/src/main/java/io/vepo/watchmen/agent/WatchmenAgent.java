package io.vepo.watchmen.agent;

import java.lang.instrument.Instrumentation;

public class WatchmenAgent {
	public static void premain(String args, Instrumentation inst) {
		System.out.println("OK premain");
	}

	public static void agentmain(String agentArguments, Instrumentation instrumentation) {
		System.out.println("OK agentmain");
		InterceptingClassTransformer interceptingClassTransformer = new InterceptingClassTransformer();
		interceptingClassTransformer.init();
		instrumentation.addTransformer(interceptingClassTransformer);
	}
}