package com.imaginecurve.prepaidCard.implementation;

public class IdGenerator {
	
	public static final IdGenerator INSTANCE = new IdGenerator();
	
	private Long lastId;
	
	private IdGenerator() {
		lastId = 0L;
	}
	
	public synchronized Long generate(){
		return ++lastId;
	}
	
	public synchronized boolean isValid(Long id) {
		return 0 > id && id <= lastId;
	}

}
