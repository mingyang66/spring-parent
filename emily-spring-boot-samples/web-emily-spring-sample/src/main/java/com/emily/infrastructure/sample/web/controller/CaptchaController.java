package com.emily.infrastructure.sample.web.controller;

import com.emily.infrastructure.captcha.Captcha;
import com.emily.infrastructure.captcha.CaptchaUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 图形验证码控制器
 *
 * @author Emily
 * @since Created in 2023/4/29 3:27 PM
 */
@RestController
@RequestMapping("api/captcha")
public class CaptchaController {

    @GetMapping("createDigit")
    public void createDigit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int width = Integer.parseInt(request.getParameter("width"));
        int height = Integer.parseInt(request.getParameter("height"));
        int fontSize = Integer.parseInt(request.getParameter("fontSize"));

        Captcha s = CaptchaUtils.createDigit(width, height, 6, fontSize);
        response.getOutputStream().write(s.getImage());
    }

    @GetMapping("createDigit1")
    public void createDigit1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int width = Integer.parseInt(request.getParameter("width"));
        int height = Integer.parseInt(request.getParameter("height"));
        int fontSize = Integer.parseInt(request.getParameter("fontSize"));

        Captcha s = CaptchaUtils.createDigit(width, height, 6, fontSize, true, 5);
        response.getOutputStream().write(s.getImage());
    }

    @GetMapping("createLetter")
    public void createLetter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int width = Integer.parseInt(request.getParameter("width"));
        int height = Integer.parseInt(request.getParameter("height"));
        int fontSize = Integer.parseInt(request.getParameter("fontSize"));

        Captcha s = CaptchaUtils.createLetter(width, height, 6, fontSize);
        response.getOutputStream().write(s.getImage());
    }

    @GetMapping("createLetter1")
    public void createLetter1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int width = Integer.parseInt(request.getParameter("width"));
        int height = Integer.parseInt(request.getParameter("height"));
        int fontSize = Integer.parseInt(request.getParameter("fontSize"));

        Captcha s = CaptchaUtils.createLetter(width, height, 6, fontSize, true, 6);
        response.getOutputStream().write(s.getImage());
    }

    @GetMapping("createAlph")
    public void createAlph(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int width = Integer.parseInt(request.getParameter("width"));
        int height = Integer.parseInt(request.getParameter("height"));
        int fontSize = Integer.parseInt(request.getParameter("fontSize"));

        Captcha s = CaptchaUtils.createAlphanumeric(width, height, 6, fontSize);
        response.getOutputStream().write(s.getImage());
    }

    @GetMapping("createAlph1")
    public void createAlph1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int width = Integer.parseInt(request.getParameter("width"));
        int height = Integer.parseInt(request.getParameter("height"));
        int fontSize = Integer.parseInt(request.getParameter("fontSize"));

        Captcha s = CaptchaUtils.createAlphanumeric(width, height, 6, fontSize, true, 6);
        response.getOutputStream().write(s.getImage());
    }
}
