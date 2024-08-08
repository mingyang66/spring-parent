/**
 * @author :  Emily
 * @since :  2024/6/14 下午1:59
 */
module oceansky.json {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    exports com.emily.infrastructure.json;
}