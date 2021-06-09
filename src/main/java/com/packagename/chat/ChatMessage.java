package com.packagename.chat;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import javax.print.attribute.standard.DateTimeAtCompleted;

public class ChatMessage {
	private String from,to,cc,subject;
	List<BigInteger> RSAKeyEncryption;
	byte[] body;
	BigInteger[] ElGamalSignature;


	public ChatMessage(String from, String to, String cc, String subject, byte[] body, List<BigInteger> RSAKeyEncryption, BigInteger[] ElGamalSignature) {
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.subject = subject;
		this.body = body;
		this.RSAKeyEncryption = RSAKeyEncryption;
		this.ElGamalSignature = ElGamalSignature;
	}

	public List<BigInteger> getRSAKeyEncryption() {
		return RSAKeyEncryption;
	}

	public void setRSAKeyEncryption(List<BigInteger> rSAKeyEncryption) {
		RSAKeyEncryption = rSAKeyEncryption;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

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

	public BigInteger[] getElGamalSignature() {
		return ElGamalSignature;
	}

	public void setElGamalSignature(BigInteger[] elGamalSignature) {
		ElGamalSignature = elGamalSignature;
	}

}
