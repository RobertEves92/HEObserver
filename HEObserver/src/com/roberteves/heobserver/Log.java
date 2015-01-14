package com.roberteves.heobserver;

public class Log {
	public static void LogException(Exception e) {
		System.out.println("Error: " + e.getMessage());
	}
}
