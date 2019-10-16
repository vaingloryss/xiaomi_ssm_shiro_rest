package com.vainglory.controller;

import com.vainglory.pojo.Address;
import com.vainglory.pojo.User;
import com.vainglory.service.IAddressService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author vaingloryss
 * @date 2019/9/26 0026 下午 4:40
 */
@Controller
@RequestMapping("/addressController/")
@RequiresAuthentication
public class AddressController {

    @Autowired
    IAddressService addressService;

    @RequestMapping(value = {"showAddress"})
    public String showAddress(HttpServletRequest request, Model model){
        User user = (User) request.getSession().getAttribute("user");
        List<Address> addresses = addressService.showAddress(user.getId());
        model.addAttribute("addresses",addresses);
        return "self_info";//查询之后跳转，使用请求转发
    }

    @PostMapping("addAddress")
    public String addAddress(Address address){
        //0为可选地址
        address.setLevel(0);
        addressService.addAddress(address);
        return "redirect:showAddress";//添加之后跳转，使用重定向
    }

    @DeleteMapping("deleteAddress")
    /*public String deleteAddress(@RequestBody Integer aid){}*/
    public String deleteAddress(Integer aid){
        addressService.deleteAddress(aid);
        return "redirect:showAddress";//删除之后跳转，使用重定向
    }
    @PutMapping("updateAddress")
    public String updateAddress(Address address){
        addressService.updateAddress(address);
        return "redirect:showAddress";
    }
    @GetMapping("defaultAddress/{aid}")
    public String defaultAddress(@PathVariable("aid") Integer aid,HttpServletRequest request){
        System.out.println("Address日志: defaultAddress");
        User user = (User) request.getSession().getAttribute("user");
        addressService.defaultAddress(user.getId(),aid);
        return "redirect:/addressController/showAddress";
    }
}
