package com.dnu.compiler.controller;

import com.dnu.compiler.service.ResultHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Mike on 1/18/2016.
 */
@Controller
public class CompilerController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String show() {
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView analyze(String code) {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("text", code);
        mav.addObject("result", ResultHandler.getResult(code));
        return mav;
    }

}
