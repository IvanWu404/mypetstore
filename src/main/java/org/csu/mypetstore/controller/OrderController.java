package org.csu.mypetstore.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
//import com.sun.org.apache.xpath.internal.operations.Mod;
import org.csu.mypetstore.domain.*;
import org.csu.mypetstore.service.AccountService;
import org.csu.mypetstore.service.OrderService;
//import org.omg.CORBA.ORB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/order/")
@SessionScope
@SessionAttributes(value = {"order"})
public class OrderController {

    @Autowired
    private OrderService orderService;

    private static final List<String> CARD_TYPE_LIST;
    private List<Order> orderList;

    static {
        List<String> cardList = new ArrayList<>();
        cardList.add("Visa");
        cardList.add("MasterCard");
        cardList.add("American Express");
        CARD_TYPE_LIST = Collections.unmodifiableList(cardList);
    }

    @GetMapping("viewNewOrderForm")
    public String viewNewOrderForm(@SessionAttribute("account") Account account, @SessionAttribute("cart") Cart cart, Model model) {
//        若 account 不存在，则报错
        if (account.getUsername() == null) {
            String message = "You must sign on before attempting to check out.  Please sign on and try checking out again.";
            model.addAttribute("message", message);
            return "common/error";
        }
//        若 cart 不为空，构建 order
        else if (cart != null) {
//        清空order
            Order order = new Order();
            order.initOrder(account, cart);
            order.setOrderId(orderService.getNextId("ordernum"));
            Date date = new Date();
            order.setOrderDate(new java.sql.Date(date.getTime()));
            model.addAttribute("order", order);
            model.addAttribute("account",account);
            return "order/newOrderForm";
        }
//        若 cart 为空，则报错
        else {
            String message = "An order could not be created because a cart could not be found.";
            model.addAttribute("message", message);
            return "common/error";
        }
    }

    @PostMapping("confirmOrder")
    public String viewConfirmOrder(Order order, HttpServletRequest request, Model model) {
        model.addAttribute("order", order);
       // String shippingAddressRequired = request.getParameter("shippingAddressRequired");
        System.out.println(request.getParameter("shippingAddressRequired"));
        if (request.getParameter("shippingAddressRequired") == null) {
//            将 ship 设置为与 bill 相同
            order.setShipToFirstName(order.getBillToFirstName());
            order.setShipToLastName(order.getBillToLastName());
            order.setShipAddress1(order.getBillAddress1());
            order.setShipAddress2(order.getBillAddress2());
            order.setShipCity(order.getBillCity());
            order.setShipState(order.getBillState());
            order.setShipZip(order.getBillZip());
            order.setShipCountry(order.getBillCountry());
            return "order/confirmOrder";
        }
        return "order/shippingOrder";
    }

    @PostMapping("viewShippingOrder")
    public String viewShippingOrder(Order order, Model model) {
        model.addAttribute("order", order);
        return "order/confirmOrder";
    }


    @GetMapping("viewOrder")
    public String viewOrder(@SessionAttribute("order") Order order, @SessionAttribute("account") Account account, Model model) {
        if (account.getUsername().equals(order.getUsername())) {
            model.addAttribute("order", order);
            return "order/viewOrder";
        } else {
            order = null;
            String message = "You may only view your own orders.";
            model.addAttribute("message", message);
            return "common/error";
        }
    }
    //  查看订单历史
    @GetMapping("viewListOrder")
    public String viewListOrder(@SessionAttribute("account") Account account, Model model) {
        List<Order> orderList = orderService.getOrdersByUsername(account.getUsername());
        model.addAttribute("orderList",orderList);
        return "order/listOrders";
    }

    /*@GetMapping("pay")
    public String pay(@SessionAttribute("order") Order order, @SessionAttribute("account") Account account, Model model) throws AlipayApiException {
        if (account.getUsername().equals(order.getUsername())) {
            AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setReturnUrl(AlipayConfig.return_url);
            alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
            //商户订单号，商户网站订单系统中唯一订单号，必填
            Date date = new Date();
            String out_trade_no = Long.toString(date.getTime());
            //付款金额，必填
            String total_amount = String.valueOf(order.getTotalPrice());
            //订单名称，必填
            String subject = String.valueOf(order.getOrderId());
            //商品描述，可空
            String body = order.getUsername();
            // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
            String timeout_express = "1c";
            alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                    + "\"total_amount\":\""+ total_amount +"\","
                    + "\"subject\":\""+ subject +"\","
                    + "\"body\":\""+ body +"\","
                    + "\"timeout_express\":\""+ timeout_express +"\","
                    + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
            String result = null;
            try {
                result = alipayClient.pageExecute(alipayRequest).getBody();
            } catch (Exception e) {
                return "common/error";
            }

            return result;

        } else {
            order = null;
            String message = "You may only pay for your own orders.";
            model.addAttribute("message", message);
            return "common/error";
        }
    }

  /*  @RequestMapping(value = "/go/applytest", produces = "text/html; charset=UTF-8",method= RequestMethod.POST)
    @ResponseBody
    public void applytest(HttpServletRequest request, HttpServletResponse response, String WIDout_trade_no, BigDecimal WIDtotal_amount, String WIDbody) throws IOException, AlipayApiException {
        //获得初始化的AlipayClient
        PrintWriter out = response.getWriter();
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
        HttpSession session=request.getSession();

        String tmoney="";
        String val="";
        String ddh=(int)((Math.random()*99999)+100000)+"";
        System.out.println(tmoney+"这是跳转的"+val+"ddh"+ddh);
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = ddh;
        //付款金额，必填
        String total_amount = tmoney;
        //订单名称，必填
        String subject = "个人充值";
        //商品描述，可空
        String body ="余额充值";
        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
//        String timeout_express = "5m";
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
//                + "\"timeout_express\":\""+timeout_express +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节
        String url = alipayClient.pageExecute(alipayRequest,"get").getBody();
        System.out.println(url);
        //输出

//        request.setAttribute("result",result);
//        response.setContentType("text/html;charset=utf-8");
//        out.print(doc.outerHtml());
//      return result;
        out.print(url);
    }
 /*   @PostMapping("/AliPay")
    public String goPay(HttpServletRequest request){
        String clientId = request.getParameter("cID");
        Competition current = competitionMapper.getPayInfoByCID(clientId);
        System.out.println(clientId);
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
        //商户订单号，商户网站订单系统中唯一订单号，必填
        Date date = new Date();
        String out_trade_no = Long.toString(date.getTime());
        //付款金额，必填
        String total_amount = String.valueOf(current.getAmount());
        //订单名称，必填
        String subject = current.getTitle();
        //商品描述，可空
        String body = current.getPlace();
        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        String timeout_express = "1c";
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+ timeout_express +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求
        try {
            String result = alipayClient.pageExecute(alipayRequest).getBody();
            System.out.println(result);
            Order order = new Order();
            order.setOrder_no(out_trade_no);
            order.setOrder_amount(Float.valueOf(total_amount));
            order.setCompetition_id(clientId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            order.setCreate_time(sdf.format(new Date()));
            boolean flag = orderMapper.addOrder(order);
            if (!flag){
                return "创建订单失败";
            }
            return result;
        }catch (Exception e){
            System.out.println("订单请求错误");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status",500);
            return "fail";
        }


    }*/

}
