package br.com.bittrexanalizer.api;


import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import br.com.bittrexanalizer.utils.HexUtil;
import br.com.bittrexanalizer.utils.SessionUtil;


public class EncryptionUtility {

    public final static String algorithmUsed = "HmacSHA512";

    public static String calculateHash(String url, String algorithm) {

        Mac shaHmac = null;

        try {

            shaHmac = Mac.getInstance(algorithm);

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }

        SecretKeySpec secretKey = new SecretKeySpec(SessionUtil.getInstance().getApiCredentials().getSecret().getBytes(), algorithm);

        try {

            shaHmac.init(secretKey);

        } catch (InvalidKeyException e) {

            e.printStackTrace();
        }

        byte[] hash = shaHmac.doFinal(url.getBytes());

        String check = HexUtil.encodeHexString(hash);


        return check;
    }

    public static String generateNonce() {

        SecureRandom random = null;

        try {

            random = SecureRandom.getInstance("SHA1PRNG");

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }

        random.setSeed(System.currentTimeMillis());

        byte[] nonceBytes = new byte[16];
        random.nextBytes(nonceBytes);

        String nonce = null;

        nonce = new String(Base64.encode(nonceBytes, Base64.NO_WRAP));

        return nonce;
    }

    private static String hexToASCII(String hexValue)
    {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexValue.length(); i += 2)
        {
            if(i+2<=hexValue.length())
            {
                String str = hexValue.substring(i, i + 2);
                output.append(((char)Integer.parseInt(str,16)));
            }
        }
        System.out.println(output.toString());
        return output.toString();
    }
}
