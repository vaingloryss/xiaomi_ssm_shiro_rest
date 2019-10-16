package com.vainglory.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vainglory.pojo.Goods;
import com.vainglory.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author vaingloryss
 * @date 2019/9/25 0025 下午 1:05
 */

@Controller
@RequestMapping("/goodsController/")
public class GoodsController {

    @Autowired
    IGoodsService goodsService;

    @GetMapping(value = {"miGaming/{pageNum}/{typeId}","miGaming/{typeId}"})
    public String miGaming(@PathVariable("typeId") Integer typeId, @PathVariable(value = "pageNum",required = false) Integer pageNum, Model model){
        if (pageNum==null){
            pageNum=1;
        }
        PageHelper.startPage(pageNum,8);
        List<Goods> goods = goodsService.miGaming(typeId,pageNum);
        PageInfo pageInfo = new PageInfo(goods);
        model.addAttribute("typeId",typeId);
        model.addAttribute("pageInfo",pageInfo);
        return "goodsList";
    }

    @GetMapping("goodsDetail/{goodsId}")
    public String goodsDetail(@PathVariable("goodsId") Integer goodsId,Model model){
        Goods goods = goodsService.goodsDetail(goodsId);
        model.addAttribute("goods",goods);
        return "goodsDetail";
    }
}
