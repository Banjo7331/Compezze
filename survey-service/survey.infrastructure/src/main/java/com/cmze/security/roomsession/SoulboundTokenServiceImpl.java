package com.cmze.security.roomsession;

import com.cmze.spi.helpers.invites.SoulboundTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class SoulboundTokenServiceImpl implements SoulboundTokenService {

    private static final Logger logger = LoggerFactory.getLogger(SoulboundTokenServiceImpl.class);

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24;

    @Override
    public String mintInvitationToken(UUID roomId, UUID targetUserId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(targetUserId.toString())
                .claim("roomId", roomId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key())
                .compact();
    }

    @Override
    public boolean validateSoulboundToken(String token, UUID currentUserId, UUID currentRoomId) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String boundUserId = claims.getSubject();
            String boundRoomId = claims.get("roomId", String.class);

            if (!boundRoomId.equals(currentRoomId.toString())) {
                logger.warn("Token valid but for wrong room. Expected: {}, Got: {}", currentRoomId, boundRoomId);
                return false;
            }

            if (!boundUserId.equals(currentUserId.toString())) {
                logger.warn("Soulbound breach attempt! User {} tried to use ticket belonging to {}", currentUserId, boundUserId);
                return false;
            }

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid invitation token: {}", e.getMessage());
            return false;
        }
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
