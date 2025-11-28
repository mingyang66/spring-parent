/**
 * @author :  Emily
 * @since :  2024/6/21 下午1:23
 */
module emily.json.test {
    opens com.emily.infrastructure.test.json;
    requires org.junit.jupiter.api;
    requires emily.json;
    requires tools.jackson.databind;
    exports com.emily.infrastructure.test.json;
    exports com.emily.infrastructure.test.json.entity;

}