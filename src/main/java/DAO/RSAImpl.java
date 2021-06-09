package DAO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class RSAImpl implements IRSA{


    private final static BigInteger ONE = new BigInteger("1");
    private BigInteger privateKey;
    private BigInteger e; //part of public key - relative prime of phi 
    private BigInteger modulus; //part of public key obtained with n = p*q
    private BigInteger p; //prime
    private BigInteger q; //prime
    private final BigInteger phi;// obtained with phi = (p-1)*(q-1)

    
    
    public RSAImpl(BigInteger p, BigInteger q, BigInteger e) {

        phi = (p.subtract(ONE)).multiply(q.subtract(ONE)); //phi = (p-1)*(q-1) 
        this.e = e;
        this.p = p;
        this.q = q;
        modulus = p.multiply(q);
        privateKey = e.modInverse(phi);//d = e^-1 mod phi, private key is obtained with the multiplative inverse of 'e' mod 'phi'
    }
    
    
//    
//    public RSAImpl(BigInteger n) {
//    	this.n = n;
//    }
    

    @Override
    public BigInteger encrypt(BigInteger bigInteger, BigInteger _e, BigInteger _n) {
        if (isModulusSmallerThanMessage(bigInteger,modulus)) {	//if (bigInteger)modulus is smaller than (bigInteger)
            throw new IllegalArgumentException("Could not encrypt - message bytes are greater than modulus");
        }
        return bigInteger.modPow(_e, _n);
    }

    
    // (_e,_n) is the public key of the recipient
    public List<BigInteger> encryptMessage(String message, BigInteger e, BigInteger n) {
        List<BigInteger> toEncrypt = new ArrayList<BigInteger>();
        BigInteger messageBytes = new BigInteger(message.getBytes());
        if (isModulusSmallerThanMessage(messageBytes,modulus)) {
            toEncrypt = getValidEncryptionBlocks(Utils.splitMessages(new ArrayList<String>() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{
                    add(message);
                }
            }));
        } else {
            toEncrypt.add((messageBytes));
        }
        List<BigInteger> encrypted = new ArrayList<BigInteger>();
        for (BigInteger bigInteger : toEncrypt) {
            encrypted.add(this.encrypt(bigInteger, e , n));
        }
        return encrypted;
    }

    @Override
    public List<BigInteger> encryptFile(String filePath) {
        BufferedReader br = null;
        FileInputStream fis = null;
        String line = "";
        List<BigInteger> encription = new ArrayList<BigInteger>();
        try {
            fis = new FileInputStream(new File(filePath));
            br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

            while ((line = br.readLine()) != null) {
                if ("".equals(line)) {
                    continue;
                }
                encription.addAll(this.encryptMessage(line));
            }

        } catch (IOException ex) {
            Logger.getLogger(RSAImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (br != null) {
                    br.close();
                }

            } catch (IOException ex) {
                Logger.getLogger(RSAImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return encription;


    }

    @Override
    public BigInteger decrypt(BigInteger encrypted , BigInteger _d , BigInteger _n) {
        return encrypted.modPow(_d , _n);
    }

    public List<BigInteger> decrypt(List<BigInteger> encryption , BigInteger _d , BigInteger _n) {
        List<BigInteger> decryption = new ArrayList<BigInteger>();
        for (BigInteger bigInteger : encryption) {
            decryption.add(this.decrypt(bigInteger , _d , _n));
        }
        return decryption;
    }

//    
//    @Override
//    public BigInteger sign(BigInteger bigInteger) {
//        return bigInteger.modPow(privateKey, modulus);
//    }

    public List<BigInteger> signMessage(final String message) {
        List<BigInteger> toSign = new ArrayList<BigInteger>();
        BigInteger messageBytes = new BigInteger(message.getBytes());
        if (isModulusSmallerThanMessage(messageBytes,modulus)) {
            toSign = getValidEncryptionBlocks(Utils.splitMessages(new ArrayList<String>() {
                {
                    add(message);
                }
            }));
        } else {
            toSign.add((messageBytes));
        }
        List<BigInteger> signed = new ArrayList<BigInteger>();
        for (BigInteger bigInteger : toSign) {
            signed.add(this.sign(bigInteger));
        }
        return signed;
    }

    @Override
    public List<BigInteger> signFile(String filePath) {
        BufferedReader br = null;
        FileInputStream fis = null;
        String line = "";
        List<BigInteger> signedLines = new ArrayList<BigInteger>();
        try {
            fis = new FileInputStream(new File(filePath));
            br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

            while ((line = br.readLine()) != null) {
                if ("".equals(line)) {
                    continue;
                }
                signedLines.addAll(this.signMessage(line));
            }

        } catch (IOException ex) {
            Logger.getLogger(RSAImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (br != null) {
                    br.close();
                }

            } catch (IOException ex) {
                Logger.getLogger(RSAImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return signedLines;
    }

//    @Override
//    public BigInteger Verify(BigInteger signedMessage) {
//        return signedMessage.modPow(e, modulus);
//    }

    public List<BigInteger> verify(List<BigInteger> signedMessages) {
        List<BigInteger> verification = new ArrayList<BigInteger>();
        for (BigInteger bigInteger : signedMessages) {
            verification.add(this.Verify(bigInteger));
        }
        return verification;
    }

    @Override
    public boolean isVerified(BigInteger signedMessage, BigInteger message) {
        return this.Verify(signedMessage).equals(message);
    }

    /**
     * ensures that blocks to encrypt are smaller than modulus
     *
     * @param messages list of blocks to be splited at half recursively
     * @return list of valid blocs
     *
     * @author Rafael M. Pestano - Oct 21, 2012 7:15:19 PM
     */
    private List<BigInteger> getValidEncryptionBlocks(List<String> messages) {
        List<BigInteger> validBlocks = new ArrayList<BigInteger>();
        BigInteger messageBytes = new BigInteger(messages.get(0).getBytes());
        if (!isModulusSmallerThanMessage(messageBytes,modulus)) {
            for (String msg : messages) {
                validBlocks.add(new BigInteger(msg.getBytes()));
            }
            return validBlocks;
        } else {//message is bigger than modulus so we have o split it
            return getValidEncryptionBlocks(Utils.splitMessages(messages));
        }

    }

    
    public List<BigInteger> messageToDecimal(final String message) {
        List<BigInteger> toDecimal = new ArrayList<BigInteger>();
        BigInteger messageBytes = new BigInteger(message.getBytes());
        if (isModulusSmallerThanMessage(messageBytes,modulus)) {
            toDecimal = getValidEncryptionBlocks(Utils.splitMessages(new ArrayList<String>() {
                {
                    add(message);
                }
            }));
        } else {
            toDecimal.add((messageBytes));
        }
        List<BigInteger> decimal = new ArrayList<BigInteger>();
        for (BigInteger bigInteger : toDecimal) {
            decimal.add(bigInteger);
        }
        return decimal;
    }

    
    public List<BigInteger> fileToDecimal(final String filePath) {
        BufferedReader br = null;
        FileInputStream fis = null;
        String line = "";
        List<BigInteger> decimalLines = new ArrayList<BigInteger>();
        try {
            fis = new FileInputStream(new File(filePath));
            br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

            while ((line = br.readLine()) != null) {
                if ("".equals(line)) {
                    continue;
                }
                decimalLines.addAll(this.messageToDecimal(line));
            }

        } catch (IOException ex) {
            Logger.getLogger(RSAImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (br != null) {
                    br.close();
                }

            } catch (IOException ex) {
                Logger.getLogger(RSAImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return decimalLines;
    }

    private boolean isModulusSmallerThanMessage(BigInteger messageBytes, BigInteger n) {
        return n.compareTo(messageBytes) == -1;
    }

    @Override
    public String toString() {
        String s = "";
//        s += "p                     = " + p + "\n";
//        s += "q                     = " + q + "\n";
//        s += "e                     = " + e + "\n";
//        s += "private Key              = " + privateKey + "\n";
//        s += "modulus (p*q)             = " + modulus;
        return s;
    }


	@Override
	public BigInteger encrypt(BigInteger bigInteger) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<BigInteger> encryptMessage(String message) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public BigInteger decrypt(BigInteger encrypted) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<BigInteger> decrypt(List<BigInteger> encryption) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public BigInteger sign(BigInteger bigInteger) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public BigInteger Verify(BigInteger signedMessage) {
		// TODO Auto-generated method stub
		return null;
	}
    
//    public static void main(String[] args) {
//    	 BigInteger p;
//         BigInteger q;
//         BigInteger e;
//         p = new BigInteger("5700734181645378434561188374130529072194886062117");
//         q = new BigInteger("35894562752016259689151502540913447503526083241413");
//         e = new BigInteger("33445843524692047286771520482406772494816708076993");
//         //String message = "This is a test This is a test This is a test";
//         String message = "MMMMMMMMMMMMMMMMMMMMMMMMMMMM";
//         RSAImpl instance = new RSAImpl(p, q, e);
//         List<BigInteger> encryption;
//         encryption = instance.encryptMessage(message);
//         System.out.println("Encrypt: " + encryption);
//         System.out.println("Encrypt: " + Utils.bigIntegerToString(encryption));
//         List<BigInteger> decrypt = instance.decrypt(encryption);
//         System.out.println("Decrypt: " + Utils.bigIntegerSum(decrypt));
//         System.out.println("Decrypt: " + Utils.bigIntegerToString(decrypt));
//        
//    }
    
    
    
}