package DAO;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;


public class ElGamalSignatureInstance {
	int securitylevel = 1024;
	//BigInteger q, a, x, y;
	private static BigInteger q1 = new BigInteger("125234189530149832160396161215102512096395753984670274535472129169239117638610780802680143860912655330818358237278614819509140395830300537674128630539325404000513761839518341187831476202434642599981117239605674561949382024190665786797654006146768398103752081323321627156117979261791027648426099074663094733913");
	private static BigInteger q2 = new BigInteger("111318393523961417887501266652750974174535114975130875565928837893947948171958168292155651961596154755096645849234861933851596665163253159640182119521112204700898066760204238762535214694773157420360486128617192504982005465850002699617012990087315932020424193823316502085727492030218684358003545133025275313991");;
	private static BigInteger a1 = new BigInteger("9919104564770821793899851600459505848255318152228434206043719433458263440387673454613270262397965075080008801555992504631358542563180022944679928402430874801889370042247943137124060339369703051005417535345336040759740302952810482986136295966549868993893132029448152165007412001424635436738713076778626649223");
	private static BigInteger a2 = new BigInteger("10455315784249658340723213997527655962851478627223648386821137967882753416159837375690628303569443286481197392115974236358918137069092971777139431266876862812133432712596257479782354404120569261340119211042854169193834903941092153365598397258052596577207819387851564480199132202123576897493507445277585922313");
	private static BigInteger x1 = new BigInteger("110482433108413665292528020867318697532884069752920007255863342876301057667912540669641912559607561660210002453856975085633117554565395394827899369763149054148698233301977357546414224177095363353394848339952806657684569201939595803819308291249570960058804868518313925977130978556430443804045851862529693955564");
	private static BigInteger x2 = new BigInteger("74012038184975391765849151941059524456804325244044833822518780383153598541471200052441628638902030396188148839698353917898485048745096686451491908785401328938655953465680808649014742733229666636107062909450959794248407899396973495908652874572718291322260621177779475592310320567197784777322841375359621300216");
	private static BigInteger y1 = new BigInteger("114121061427148593253992090035462572002160844620588981285261748571140395961064492070368161932810399651837645655553200565261082285641654298538003084824101614549322897055754147299749425183507526515914719115782431295373843852087505999634498304575901091651583839985543904797551953648013642890541630104787933825439");
	private static BigInteger y2 = new BigInteger("21256013050375646361999347911072241495289596223522672160312038248329309028953500741772584710426176775971725493974304614039138119012855510856619834891021628606045611840888089195913205099951002325631006525714419241855740534419759647915710403453340183469925280158631891860976518914549652640902277303575720381074");

	/*
	public BigInteger __randomInZq() {
		BigInteger r = null;
		do {
			//System.out.print(".");
			r = new BigInteger(securitylevel, new SecureRandom());
		}while(r.compareTo(q) >= 0);
		//System.out.println(".");
		return r;
	}
	*/

	public BigInteger __randomPrimeInZq(BigInteger q) {
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

	/*
	public void initKeys() {
		//System.out.println("choose a prime q with securitylevel " + securitylevel);
		q = new BigInteger(securitylevel, 100, new Random());
		//System.out.println("q : " + q);
		a = __randomInZq();
		//System.out.println("a : " + a);
		x = __randomInZq();
		//System.out.println("x : " + x);
		y = a.modPow(x, q);
		//System.out.println("y : " + y);
	}
	*/

	public BigInteger[] signature(byte m[], BigInteger q, BigInteger a, BigInteger x) {
		BigInteger sig[] = new BigInteger[2];
		BigInteger k = __randomPrimeInZq(q);
		sig[0] = a.modPow(k, q);
		sig[1] = __hashInZq(m).subtract(x.multiply(sig[0])).mod(q.subtract(BigInteger.ONE))
			.multiply(k.modInverse(q.subtract(BigInteger.ONE))).mod(q.subtract(BigInteger.ONE));
		System.out.println("[S1,S2] = [" + sig[0] + ", " + sig[1] + "]");
		return sig;
	}

	public boolean verify(byte m[], BigInteger sig[], BigInteger q, BigInteger a, BigInteger y) {
 		BigInteger l = y.modPow(sig[0], q).multiply(sig[0].modPow(sig[1], q)).mod(q);
		BigInteger r = a.modPow(__hashInZq(m), q);
		return l.compareTo(r) == 0;
	}

	public int getSecuritylevel() {
		return securitylevel;
	}

	public void setSecuritylevel(int securitylevel) {
		this.securitylevel = securitylevel;
	}


	public static BigInteger getQ1() {
		return q1;
	}

	public static BigInteger getQ2() {
		return q2;
	}

	public static BigInteger getA1() {
		return a1;
	}

	public static BigInteger getA2() {
		return a2;
	}

	public static BigInteger getX1() {
		return x1;
	}

	public static BigInteger getX2() {
		return x2;
	}

	public static BigInteger getY1() {
		return y1;
	}

	public static BigInteger getY2() {
		return y2;
	}
}