package com.saas.Planify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * React SPA fallback: 파일 확장자 없는 모든 GET 요청 → index.html
 * /api/** 는 각 API 컨트롤러가 우선 처리
 */
@Controller
public class SpaController {

    @GetMapping(value = "/**/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}
