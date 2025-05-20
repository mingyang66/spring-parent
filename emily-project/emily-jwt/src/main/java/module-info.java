/**
 * @author :  Emily
 * @since :  2024/6/14 下午2:02
 */
module emily.jwt {
    requires org.bouncycastle.provider;
    requires com.auth0.jwt;
    exports com.emily.infrastructure.jwt;
}