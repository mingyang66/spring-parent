package com.emily.infrastructure.redis.connection;

import org.jspecify.annotations.Nullable;
import org.springframework.lang.Contract;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author :  Emily
 * @since :  2025/11/28 下午4:12
 */
record DataDbRedisUrl(URI uri, boolean useSsl, Credentials credentials, int database) {
    DataDbRedisUrl(URI uri, boolean useSsl, Credentials credentials, int database) {
        this.uri = uri;
        this.useSsl = useSsl;
        this.credentials = credentials;
        this.database = database;
    }

    @Contract("!null -> !null")
    static @Nullable DataDbRedisUrl of(@Nullable String url) {
        return url != null ? of(toUri(url)) : null;
    }

    private static DataDbRedisUrl of(URI uri) {
        boolean useSsl = "rediss".equals(uri.getScheme());
        DataDbRedisUrl.Credentials credentials = DataDbRedisUrl.Credentials.fromUserInfo(uri.getUserInfo());
        int database = getDatabase(uri);
        return new DataDbRedisUrl(uri, useSsl, credentials, database);
    }

    private static int getDatabase(URI uri) {
        String path = uri.getPath();
        String[] split = !StringUtils.hasText(path) ? new String[0] : path.split("/", 2);
        return split.length > 1 && !split[1].isEmpty() ? Integer.parseInt(split[1]) : 0;
    }

    private static URI toUri(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
                throw new DataDbRedisUrlSyntaxException(url);
            } else {
                return uri;
            }
        } catch (URISyntaxException var3) {
            URISyntaxException ex = var3;
            throw new DataDbRedisUrlSyntaxException(url, ex);
        }
    }

    public URI uri() {
        return this.uri;
    }

    public boolean useSsl() {
        return this.useSsl;
    }

    public Credentials credentials() {
        return this.credentials;
    }

    public int database() {
        return this.database;
    }

    static record Credentials(@Nullable String username, @Nullable String password) {
        private static final Credentials NONE = new Credentials((String)null, (String)null);

        Credentials(@Nullable String username, @Nullable String password) {
            this.username = username;
            this.password = password;
        }

        private static Credentials fromUserInfo(@Nullable String userInfo) {
            if (userInfo == null) {
                return NONE;
            } else {
                int index = userInfo.indexOf(58);
                return index != -1 ? new Credentials(userInfo.substring(0, index), userInfo.substring(index + 1)) : new Credentials((String)null, userInfo);
            }
        }

        public @Nullable String username() {
            return this.username;
        }

        public @Nullable String password() {
            return this.password;
        }
    }
}
