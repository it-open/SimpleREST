/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.security;

import io.jsonwebtoken.CompressionCodec;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
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

    /**
     *
     * @return
     */
    public static Class getUserClass() {
        return userClass;
    }

    /**
     *
     * @param userClass
     */
    public static void setUserClass(Class newUserClass) {
        userClass = newUserClass;
    }

    /**
     *
     * @param JwtCompressionCodec
     */
    public static void setJwtCompressionCodec(CompressionCodec JwtCompressionCodec) {
        RestSecurity.JwtCompressionCodec = JwtCompressionCodec;
    }

    /**
     *
     * @param JwtSecretKey
     */
    public static void setJwtSecretKey(SecretKey JwtSecretKey) {
        RestSecurity.JwtSecretKey = JwtSecretKey;

    }

    /**
     *
     * @param JwtSignatureAlgorithm
     */
    public static void setJwtSignatureAlgorithm(SignatureAlgorithm JwtSignatureAlgorithm) {
        RestSecurity.JwtSignatureAlgorithm = JwtSignatureAlgorithm;
        if (!SignatureAlgorithm.NONE.equals(JwtSignatureAlgorithm))
        setJwtSecretKey(Keys.secretKeyFor(JwtSignatureAlgorithm));
    }

    /**
     *
     * @param Id
     * @param issuer
     * @param Subject
     * @param expiration
     * @return
     */
    public static String JWS_BUILD(String Id, String issuer, String Subject, Date expiration) {
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(new Date())
                .setId(Id)
                .compressWith(JwtCompressionCodec);
        if (!JwtSignatureAlgorithm.equals(SignatureAlgorithm.NONE))
            builder.signWith(JwtSignatureAlgorithm, JwtSecretKey);
        if (expiration!=null)
            builder.setExpiration(expiration);
        if (Subject!=null)
            builder.setSubject(Subject);
        if (issuer!=null)
            builder.setIssuer(issuer);
                

        return builder.compact();

    }

    /**
     *
     */
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

        /**
         *
         * @param Id
         * @param issuer
         * @param Subject
         */
        public JwtInfo(String Id, String issuer, String Subject) {
            this.Id = Id;
            this.issuer = issuer;
            this.Subject = Subject;
        }

    }

    /**
     *
     * @param JwsData
     * @return
     */
    public static JwtInfo JWS_DECRYPT(String JwsData) {
        Jwt jwt = null;
        try {
            JwtParser parser=Jwts.parser();
            if (JwtSecretKey!=null)
                parser.setSigningKey(JwtSecretKey);
            jwt = parser.parse(JwsData);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException ex) {

        }
        if (jwt != null) {
            DefaultClaims body=(DefaultClaims)jwt.getBody();
            return new JwtInfo(body.getId(), body.getIssuer(), body.getSubject());
        } else {
            return null;
        }

    }

}
