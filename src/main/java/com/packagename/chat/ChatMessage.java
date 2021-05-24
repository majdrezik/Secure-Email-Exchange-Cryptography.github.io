package com.packagename.chat;

import java.time.LocalDateTime;

import javax.print.attribute.standard.DateTimeAtCompleted;

public class ChatMessage {
	private String from;
	private String message;
	private LocalDateTime time;

	public ChatMessage(String from, String message) {
		this.from = from;
		this.message = message;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

}
