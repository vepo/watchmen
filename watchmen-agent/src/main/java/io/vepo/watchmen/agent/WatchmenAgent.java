package io.vepo.watchmen.agent;

import java.lang.instrument.Instrumentation;

public class WatchmenAgent {
	public static void premain(String args, Instrumentation inst) {
		System.out.println("OK");
	}
}