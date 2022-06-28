package org.example;

import java.io.UnsupportedEncodingException;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Arrays;

import com.google.common.base.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Main {


    public static void main(String args[]) throws Exception {
        String newStr = null;

//ID provided from the partner or "ctrip"
        String authUser="Cmtest";

//AES-256 encrypted guest's actual phone number
        String decryptKey ="F9SunTLe1Ehv/Z0o7BRHBg==";

        String valueKey ="";
        Main test = new Main();
        ImmutablePair<String, String> ivAndKey = test.genIvAndKey(authUser);
        valueKey = test.decrypt(decryptKey, ivAndKey.getLeft(), ivAndKey.getRight());
        System.out.println(valueKey);
    }
    private ImmutablePair<String, String> genIvAndKey(String authUser) throws NoSuchAlgorithmException {
        if (Strings.isNullOrEmpty(authUser)) {
            authUser = "ctrip";
        }
//MD5 encrypt the ID to generate a key
        String key = Md5Helper.getMd5Value(authUser.getBytes(Charsets.UTF_8));

//Use the last 16 characters of the keyas IV
        String iv = key.substring(key.length() - 16);

        return new ImmutablePair<>(key, iv);
    }

    public String decrypt(String encData, String secretKey, String ivStr) {
        try {
            byte[] ivByte = this.genIv(ivStr);
            byte[] raw = this.getLegalKey(secretKey);
            if (raw == null) {
                return "";
            } else {
                SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                IvParameterSpec iv = new IvParameterSpec(ivByte);
                cipher.init(2, skeySpec, iv);
                byte[] base64Data = (new BASE64Decoder()).decodeBuffer(encData);
                byte[] original = cipher.doFinal(base64Data);
                return new String(original, StandardCharsets.UTF_8.name().toLowerCase());
            }
        } catch (Exception var11) {
            return "";
        }
    }

    private byte[] genIv(String iv) {
        iv = iv.substring(0, 16);

        try {
            return iv.getBytes("ASCII");
        } catch (UnsupportedEncodingException var3) {
            return new byte[16];
        }
    }

    private byte[] getLegalKey(String key) {
        try {
            if (key.length() > 32) {
                key = key.substring(0, 32);
            } else if (key.length() < 32) {
                key = String.format("%1$-32s", key);
            }
            return key.getBytes("ASCII");
        } catch (Exception var3) {
            return null;
        }
    }
}