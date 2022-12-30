package org.pathcheck.statelessplandefevaluator

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class UIController {
    @RequestMapping("/index")
    fun index() = "index"
}