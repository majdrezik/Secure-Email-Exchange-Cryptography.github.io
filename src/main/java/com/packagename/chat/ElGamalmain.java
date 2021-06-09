package com.packagename.chat;

import java.math.BigInteger;

import DAO.ElGamalSignatureInstance;

public class ElGamalmain {

	/*
	public static void main(String args[]) {
		ElGamalSignatureInstance instance = new ElGamalSignatureInstance();
		//instance.initKeys();
		byte m[] = "my name is ElGamal, my student number is 201300012345.".getBytes();
		BigInteger sig[] = instance.signature(m);
		System.out.println("Real  signature verify result : " + instance.verify(m, sig));
		sig[0] = sig[0].add(BigInteger.ONE);// changing S1 so we've destroyed DS and hash result of sig
											//compared to hash result of message m unchanged won't be equal
		System.out.println("Faked signature verify result : " + instance.verify(m, sig));
	}
	*/

}
