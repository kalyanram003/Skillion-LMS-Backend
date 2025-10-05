package com.lms.lms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    // Forward non-API routes to index.html for React Router
    @RequestMapping(value = { "/", "/{x:[\\w\\-]+}", "/{x:^(?!api$).*$}/**" })
    public String forward() {
        return "forward:/index.html";
    }
}


