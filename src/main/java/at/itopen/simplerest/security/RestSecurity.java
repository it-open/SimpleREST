/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodec;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;

/**
 *
 * @author roland
 */
public class RestSecurity {

    private static Class userClass = DefaultUser.class;
    private static SecretKey JwtSecretKey;
    private static CompressionCodec JwtCompressionCodec = null;
    private static SignatureAlgorithm JwtSignatureAlgorithm;

    static {
        setJwtSignatureAlgorithm(SignatureAlgorithm.NONE);
        setJwtCompressionCodec(CompressionCodecs.DEFLATE);
    }

    public static Class getUserClass() {
        return userClass;
    }

    public static void setUserClass(Class userClass) {
        userClass = userClass;
    }

    public static void setJwtCompressionCodec(CompressionCodec JwtCompressionCodec) {
        RestSecurity.JwtCompressionCodec = JwtCompressionCodec;
    }

    public static void setJwtSecretKey(SecretKey JwtSecretKey) {
        RestSecurity.JwtSecretKey = JwtSecretKey;

    }

    public static void setJwtSignatureAlgorithm(SignatureAlgorithm JwtSignatureAlgorithm) {
        RestSecurity.JwtSignatureAlgorithm = JwtSignatureAlgorithm;
        if (!SignatureAlgorithm.NONE.equals(JwtSignatureAlgorithm))
        setJwtSecretKey(Keys.secretKeyFor(JwtSignatureAlgorithm));
    }

    public static String JWS_BUILD(String Id, String issuer, String Subject, Date expiration) {
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(new Date())
                .setId(Id)
                .setIssuer(issuer)
                .setSubject(Subject)
                .setExpiration(expiration)
                .compressWith(JwtCompressionCodec)
                .signWith(JwtSignatureAlgorithm, JwtSecretKey);

        return builder.compact();

    }

    public static class JwtInfo {

        /**
         * @return the Id
         */
        public String getId() {
            return Id;
        }

        /**
         * @return the issuer
         */
        public String getIssuer() {
            return issuer;
        }

        /**
         * @return the Subject
         */
        public String getSubject() {
            return Subject;
        }
        private String Id;
        private String issuer;
        private String Subject;

        public JwtInfo(String Id, String issuer, String Subject) {
            this.Id = Id;
            this.issuer = issuer;
            this.Subject = Subject;
        }

    }

    public static JwtInfo JWS_DECRYPT(String JwsData) {
        Jws<Claims> jws = null;
        try {
            jws = Jwts.parser().setSigningKey(JwtSecretKey).parseClaimsJws(JwsData);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException ex) {

        }
        if (jws != null) {
            return new JwtInfo(jws.getBody().getId(), jws.getBody().getIssuer(), jws.getBody().getSubject());
        } else {
            return null;
        }

    }

}
