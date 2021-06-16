package io.vepo.watchmen.demo;

import io.vepo.watchmen.attach.WatchmenAttacher;

public class Demo {
	public static void main(String[] args) {
		WatchmenAttacher.attach();
		System.out.println("OK");
	}
}