package com.packagename.chat;

import java.math.BigInteger;
import java.util.Random;

import DAO.ElGamalSignatureInstance;
import DAO.RSAImpl;

public class User {
	private String userName;
	private String password;

	private BigInteger d; //d
	private BigInteger e; //e
	private BigInteger phi,n,p,q;
	private RSAImpl _RSA;

	private ElGamalSignatureInstance _ElGamal;
	private BigInteger ElGamalPrivateKey; //x
	private BigInteger ElGamalPublicKey;  //y
	private BigInteger elGam_q;
	private BigInteger elGam_a;
//	private BigInteger elGam_x;
//	private BigInteger elGam_y;


	public User(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
		if(_ElGamal == null)
			_ElGamal = new ElGamalSignatureInstance();
		//elGam_q = _ElGamal.init_q_Key();	// 1024 was "securitylevel"
		//elGam_a = _ElGamal.init_a_Key();
		//ElGamalPrivateKey = _ElGamal.init_x_Key();
		//ElGamalPublicKey = _ElGamal.init_y_Key();
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}


	public void setRSA(BigInteger p, BigInteger q, BigInteger e) {
		this.p = p;
		this.q = q;
		this.e = e;
		this.phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE)); //phi = (p-1)*(q-1)
		n = p.multiply(q);
		d = e.modInverse(phi);
		 //RSAImpl(BigInteger p, BigInteger q, BigInteger e) {
//		 _RSA = new RSAImpl(this.p, this.q, this.e);
	}
//	public RSAImpl get_RSA() {
//		return _RSA;
//	}
//	public void set_RSA(RSAImpl _RSA) {
//		this._RSA = _RSA;
//	}
	public BigInteger getD() {
		return d;
	}
//	public void setD(BigInteger d) {
//		this.d = d;
//	}
	public BigInteger getE() {
		return e;
	}
	public void setE(BigInteger e) {
		this.e = e;
	}
	public BigInteger getPhi() {
		return phi;
	}
	public void setPhi(BigInteger phi) {
		this.phi = phi;
	}
	public BigInteger getN() {
		return n;
	}
	public void setN(BigInteger n) {
		this.n = n;
	}
	public BigInteger getP() {
		return p;
	}
	public void setP(BigInteger p) {
		this.p = p;
	}
	public BigInteger getQ() {
		return q;
	}
	public void setQ(BigInteger q) {
		this.q = q;
	}
	public BigInteger getElGamalPrivateKey() {
		return ElGamalPrivateKey;
	}
	public void setElGamalPrivateKey(BigInteger elGamalPrivateKey) {
		ElGamalPrivateKey = elGamalPrivateKey;
	}
	public BigInteger getElGamalPublicKey() {
		return ElGamalPublicKey;
	}
	public void setElGamalPublicKey(BigInteger elGamalPublicKey) {
		ElGamalPublicKey = elGamalPublicKey;
	}
	public BigInteger getElGam_q() {
		return elGam_q;
	}
	public void setElGam_q(BigInteger elGam_q) {
		this.elGam_q = elGam_q;
	}
	public BigInteger getElGam_a() {
		return elGam_a;
	}
	public void setElGam_a(BigInteger elGam_a) {
		this.elGam_a = elGam_a;
	}

	public ElGamalSignatureInstance get_ElGamal() {
		return _ElGamal;
	}

	public void set_ElGamal(ElGamalSignatureInstance _ElGamal) {
		this._ElGamal = _ElGamal;
	}

	public void initElGamKeys(){

	}

}
