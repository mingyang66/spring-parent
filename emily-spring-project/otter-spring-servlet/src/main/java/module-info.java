/**
 * @author :  姚明洋
 * @since :  2024/8/16 上午11:08
 */
module otter.spring.servlet {
    requires jakarta.servlet;
    requires org.apache.commons.lang3;
    requires spring.web;
    requires oceansky.common;
    exports com.otter.infrastructure.servlet;
}