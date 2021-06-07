package DAO;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;


public class ElGamalSignatureInstance {
	int securitylevel = 1024;
	BigInteger q, a, x, y;

	public BigInteger __randomInZq() {
		BigInteger r = null;
		do {
			//System.out.print(".");
			r = new BigInteger(securitylevel, new SecureRandom());
		}while(r.compareTo(q) >= 0);
		//System.out.println(".");
		return r;
	}

	public BigInteger __randomPrimeInZq() {
		BigInteger r = null;
		do {
			//System.out.print(".");
			r = new BigInteger(securitylevel, 100, new SecureRandom());
		}while(r.compareTo(q) >= 0);
		//System.out.println(".");
		return r;
	}

	public BigInteger __hashInZq(byte m[]) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(m);
		    byte b[] = new byte[33];
		    System.arraycopy(md.digest(), 0, b, 1, 32);
		    return new BigInteger(b);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("this cannot happen.");
		}
	    return null;
	}

	public void initKeys() {
		System.out.println("choose a prime q with securitylevel " + securitylevel);
		q = new BigInteger(securitylevel, 100, new Random());
		System.out.println("q : " + q);
		a = __randomInZq();
		System.out.println("a : " + a);
		x = __randomInZq();
		System.out.println("x : " + x);
		y = a.modPow(x, q);
		System.out.println("y : " + y);
	}

	public BigInteger[] signature(byte m[]) {
		BigInteger sig[] = new BigInteger[2];
		BigInteger k = __randomPrimeInZq();
		sig[0] = a.modPow(k, q);
		sig[1] = __hashInZq(m).subtract(x.multiply(sig[0])).mod(q.subtract(BigInteger.ONE))
			.multiply(k.modInverse(q.subtract(BigInteger.ONE))).mod(q.subtract(BigInteger.ONE));
		System.out.println("[S1,S2] = [" + sig[0] + ", " + sig[1] + "]");
		return sig;
	}

	public boolean verify(byte m[], BigInteger sig[]) {
 		BigInteger l = y.modPow(sig[0], q).multiply(sig[0].modPow(sig[1], q)).mod(q);
		BigInteger r = a.modPow(__hashInZq(m), q);
		return l.compareTo(r) == 0;
	}
}