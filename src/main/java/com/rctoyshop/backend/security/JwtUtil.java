package com.rctoyshop.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
// JWT = 身份驗證憑證

// Filter = 閘道守衛，攔截所有請求並驗證 JWT

// JwtUtil = 工具人，幫你解析 token、驗簽、檢查過期、產生 token

// 整合：

// 登入 → JwtUtil 生成 token

// 請求 → Filter 解析 & 驗證 token

// 過期 → Filter 可以阻擋或要求重新 generateToken

/**
 * 1. JwtUtil (工具人)：負責製造 Token (簽名) 和 驗證 Token (驗簽)
 * 這是不依賴其他人的底層工具，所以最先寫。
 */
@Component
public class JwtUtil {

    // 產生一個固定的密鑰 (Secret Key)，這把鑰匙只有後端知道，用來簽名和驗證真偽。
    // 使用 HS256 演算法，保證足夠的安全強度。
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // 「解析 token → 拿 Claims → 呼叫 getSubject()」 Lambda
    // --- 方法 1: 從 Token 中取出用戶名 (Username) ---
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // Subject 欄位通常存 Username
    }

    // --- 方法 2: 從 Token 中取出過期時間 ---
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 泛型方法：負責解析 Token 裡面的各種聲明 (Claims)
    // token → 整個 JWT 字串 會有一群金鑰亂碼
    // claimsResolver → 告訴方法「我要從 token 拿哪個資料」，回傳型別是 T（泛型）
    // 用密鑰解析 token → 產生 Claims 物件
    // Claims 裡面有整個 token 的資料（username、過期時間、角色等）
    // final → 這個 claims 變數不能再指向別的物件
    // 將解析出來的 claims 傳給你指定的方法（claimsResolver）
    // 方法會返回你想要的資料，例如：
    // Claims::getSubject → username
    // Claims::getExpiration → 過期時間
    // return 將結果回傳給呼叫者
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 私有方法：使用密鑰去解開 Token，如果密鑰不對或 Token 被竄改，這裡會報錯
    //     token (JWT字串)
    //       │
    // Jwts.parserBuilder() → 建立解析器
    //       │
    // .setSigningKey(SECRET_KEY) → 用鑰匙解鎖
    //       │
    // .build() → 解析器完成
    //       │
    // .parseClaimsJws(token) → 驗證簽名，解析 token
    //       │
    // .getBody() → 取得 Claims 物件（payload）
    //       │
    // 回傳 Claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) // 用同一把鑰匙解鎖
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 檢查 Token 是否過期 (現在時間 > 過期時間?)
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // --- 方法 3: 產生新 Token (發卡) ---
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // 額外資訊：我們可以在 Token 裡面偷塞 User 的權限 (例如 ROLE_ADMIN)
        claims.put("role", userDetails.getAuthorities().toString());

        // 呼叫 createToken 開始製造
        return createToken(claims, userDetails.getUsername());
    }

    // 私有方法：實際構建 JWT 字串
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // 放額外資訊
                .setSubject(subject) // 放主角 (Username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 發卡時間 (現在)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 有效期：10 小時
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 最後蓋上印章 (簽名)
                .compact(); // 壓縮成字串回傳
    }

    // --- 方法 4: 驗證 Token 是否有效 (驗卡) ---
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // 規則：Token 裡的 username 必須跟資料庫查出來的一樣，且 Token 還沒過期
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
