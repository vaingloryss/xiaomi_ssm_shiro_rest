package com.vainglory.controller;

import com.alibaba.fastjson.JSON;
import com.vainglory.pojo.*;
import com.vainglory.service.IAddressService;
import com.vainglory.service.ICartService;
import com.vainglory.service.IOrderService;
import com.vainglory.util.RandomUtils;
import com.vainglory.util.WeiXinResult;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author vaingloryss
 * @date 2019/9/27 0027 下午 3:36
 */

@Controller
@RequestMapping("/orderController/")
@RequiresAuthentication
public class OrderController {
    @Autowired
    private IOrderService orderService;
    @Autowired
    private ICartService cartService;
    @Autowired
    private IAddressService addressService;

    @GetMapping("showOrder")
    public String showOrder(Model model, HttpServletRequest request){
        System.out.println("showOrder======================");
        User user = (User) request.getSession().getAttribute("user");
        List<Order> orders = orderService.showOrder(user.getId());
        model.addAttribute("orders",orders);
        return "orderList";
    }

    @GetMapping("orderDetail/{oid}")
    public String showOrderDetail(@PathVariable("oid") String oid, Model model){
        System.out.println("显示订单详情：Rest");
        System.out.println("orderController日志:orderDetail");
        Order order = orderService.orderDetail(oid);
        model.addAttribute("order",order);
        return "orderDetail";
    }

    @GetMapping("getOrderView")
    public String getOrderView(Model model,HttpServletRequest request){

        User user = (User) request.getSession().getAttribute("user");
        List<Cart> carts = cartService.findByUid(user.getId());
        model.addAttribute("carts",carts);
        List<Address> addresses = addressService.getAddresses(user.getId());
        model.addAttribute("addresses",addresses);
        return "order";
    }

    @GetMapping("addOrder/{aid}")
    public String addOrder(@PathVariable("aid") Integer aid,HttpServletRequest request,Model model){
        User user = (User) request.getSession().getAttribute("user");
        List<Cart> carts = cartService.findByUid(user.getId());
        String orderId = RandomUtils.createOrderId();

        List<OrderDetail> orderDetails = new ArrayList<>();
        BigDecimal sum = new BigDecimal(0);
        for (Cart cart : carts) {
            OrderDetail orderDetail = new OrderDetail(null,orderId,cart.getGid(),cart.getNum(),cart.getMoney());
            orderDetails.add(orderDetail);
            sum = sum.add(cart.getMoney());
        }
        //创建订单
        Order order = new Order(orderId,user.getId(),sum,"1",new Date(),aid);
        order.setOrderDetails(orderDetails);
        orderService.addOrder(order);
        model.addAttribute("order",order);
        return "orderSuccess";
    }

    @GetMapping("toPayWeiXin/{oid}")
    public String toPayWeiXin(@PathVariable("oid") String oid,Model model){
        Order order = orderService.payOrder(oid);
        String goodsName = "";
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            goodsName += orderDetail.getGoods().getName()+" ";
        }
        model.addAttribute("orderId",oid);
        model.addAttribute("money",order.getMoney());
        model.addAttribute("goodsName",goodsName);
        return "payWeixin";
    }

    @GetMapping("wxSuccess/{result}")
    public String wxSuccess(@PathVariable("result") String result,Model model){
        System.out.println("wxSuccess日志：");
        String payMSG = "";
        WeiXinResult weiXinResult = JSON.parseObject(result, WeiXinResult.class);
        String result_code = weiXinResult.getResult().getResult_code();
        if ("SUCCESS".equals(result_code)){
            if (weiXinResult.getType()==0){
                payMSG = "您的订单号为:"+weiXinResult.getResult().getOut_trade_no()+",金额为:"+weiXinResult.getResult().getCash_fee()+"已经支付成功,等待发货~~";
                model.addAttribute("msg",payMSG);
                return "message";
            }
            orderService.updateOrderStatus(weiXinResult.getResult().getOut_trade_no(),"2");
        }else {
            payMSG = "您的订单号为:"+weiXinResult.getResult().getOut_trade_no()+"支付失败";
            model.addAttribute("msg",payMSG);
        }
        return "message";
    }
}
