package org.csu.mypetstore.controller;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Config;
import org.csu.mypetstore.domain.Product;

import org.csu.mypetstore.domain.RandomValidateCode;
import org.csu.mypetstore.service.AccountService;
import org.csu.mypetstore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.util.DigestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/account/")
@SessionAttributes({"account", "myList", "authenticated", "code"})
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CategoryService categoryService;


    private static final List<String> LANGUAGE_LIST;
    private static final List<String> CATEGORY_LIST;

    static {
        List<String> langList = new ArrayList<String>();
        langList.add("ENGLISH");
        langList.add("CHINESE");
        LANGUAGE_LIST = Collections.unmodifiableList(langList);

        List<String> catList = new ArrayList<String>();
        catList.add("FISH");
        catList.add("DOGS");
        catList.add("REPTILES");
        catList.add("CATS");
        catList.add("BIRDS");

        CATEGORY_LIST = Collections.unmodifiableList(catList);
    }

    @GetMapping("signonForm")
    public String signonForm() {
        return "account/signon";
    }

    @PostMapping("signon")
    public String signon(Model model, HttpServletRequest httpServletRequest, HttpSession session) {
//        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        String username = httpServletRequest.getParameter("username");
        String md5Password = httpServletRequest.getParameter("password");
        System.out.println(md5Password);
        Account account = accountService.getAccount(username, md5Password);
        String inputStr = httpServletRequest.getParameter("vCode");
        inputStr = inputStr.toUpperCase();
        String random = (String) session.getAttribute("RANDOMVALIDATECODEKEY");


        if (account == null) {
            String msg = "Invalid username or password.  Signon failed.";
            model.addAttribute("msg", msg);
            return "account/signon";
        } else {
            String code = (String) httpServletRequest.getSession().getAttribute("code");
            if (!random.equals(inputStr)) {
                model.addAttribute("msg", "验证码错误");
                return "account/signon";
            } else {
                account.setPassword(null);
                List<Product> myList = categoryService.getProductListByCategory(account.getFavouriteCategoryId());
                boolean authenticated = true;
                model.addAttribute("account", account);
                model.addAttribute("myList", myList);
                model.addAttribute("authenticated", authenticated);
                return "catelog/main";
            }
//            else {
//                model.addAttribute("msg", "Invalid verification code .Signon failed.");
//                return "account/signon";
        }
//
    }
//    }

    @GetMapping("signoff")
    public String signoff(Model model) {
        Account loginAccount = new Account();
        List<Product> myList = null;
        boolean authenticated = false;
        model.addAttribute("account", loginAccount);
        model.addAttribute("myList", myList);
        model.addAttribute("authenticated", authenticated);
        return "catelog/main";
    }

    @GetMapping("editAccountForm")
    public String editAccountForm(@SessionAttribute("account") Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute("LANGUAGE_LIST", LANGUAGE_LIST);
        model.addAttribute("CATEGORY_LIST", CATEGORY_LIST);
        return "account/edit_account";
    }

    @PostMapping("editAccount")
    public String editAccount(Account account, String repeatedPassword, Model model, HttpServletRequest httpServletRequest, HttpSession session) {
        repeatedPassword = DigestUtils.md5DigestAsHex(repeatedPassword.getBytes());
        account.setPassword(DigestUtils.md5DigestAsHex(account.getPassword().getBytes()));
        String inputStr = httpServletRequest.getParameter("vCode");
        inputStr = inputStr.toUpperCase();
        String random = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
        //System.out.println(repeatedPassword);
        // System.out.println(account.getPassword());
        if (account.getPassword() == null || account.getPassword().length() == 0 || repeatedPassword == null || repeatedPassword.length() == 0) {
            String msg = "密码不能为空";
            model.addAttribute("msg", msg);
            return "account/edit_account";
        } else if (!account.getPassword().equals(repeatedPassword)) {
            String msg = "两次密码不一致";
            model.addAttribute("msg", msg);
            return "account/edit_account";
        } else {
            if (!random.equals(inputStr)) {
                model.addAttribute("msg", "验证码错误");
                return "redirect:/catelog/view";
            } else {
                accountService.updateAccount(account);
                account = accountService.getAccount(account.getUsername());
                List<Product> myList = categoryService.getProductListByCategory(account.getFavouriteCategoryId());
                boolean authenticated = true;
                model.addAttribute("account", account);
                model.addAttribute("myList", myList);
                model.addAttribute("authenticated", authenticated);
                return "redirect:/catelog/view";
            }
        }
    }

    @GetMapping("newAccountForm")
    public String newAccountForm(Model model) {
        model.addAttribute("newAccount", new Account());
        model.addAttribute("LANGUAGE_LIST", LANGUAGE_LIST);
        model.addAttribute("CATEGORY_LIST", CATEGORY_LIST);
        return "account/new_account";
    }

    @PostMapping("sendVerificationCode")
    @ResponseBody
    public void sendVerificationCode(@RequestParam String phoneNumber, Model model, HttpServletRequest
            httpServletRequest) throws ClientException {
        String code = Config.sendSms(phoneNumber);
        httpServletRequest.getSession().setAttribute("code", code);
        System.out.println("成功发送短信给" + phoneNumber + "，验证码为" + code);
    }

    @PostMapping("newAccount")
    public String newAccount(Account account, Model model, String repeatedPassword, HttpServletRequest httpServletRequest, HttpSession session) {
        String inputStr = httpServletRequest.getParameter("vCode");
        inputStr = inputStr.toUpperCase();
        String random = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
        if (!(account.getPassword() == null || account.getPassword() == null) && account.getPassword().equals(repeatedPassword)) {
            Account temp = accountService.getAccount(account.getUsername());
            if (!random.equals(inputStr)) {
                model.addAttribute("msg", "验证码错误");
                return "account/register";
            }
            if (temp == null) {
                account.setPassword(DigestUtils.md5DigestAsHex(account.getPassword().getBytes()));
                accountService.insertAccount(account);
                Account t = null;
                model.addAttribute("account", account);

                return "account/signon";
            }
        }
        Account t = null;
        model.addAttribute("account", t);
        return "account/register";
    }

    /**
     * 获取生成验证码显示到 UI 界面
     */

    @RequestMapping(value = "/getVerify")
    public void getVerify(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
        response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        RandomValidateCode randomValidateCode = new RandomValidateCode();
        try {
            randomValidateCode.getRandcode(request, response);//输出验证码图片方法
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    /*@PostMapping("newAccount")
    public String newAccount(Account account, String repeatedPassword, Model model) {
        repeatedPassword = DigestUtils.md5DigestAsHex(repeatedPassword.getBytes());
        account.setPassword(DigestUtils.md5DigestAsHex(account.getPassword().getBytes()));
        //System.out.println(repeatedPassword);
        //System.out.println(account.getPassword());
        if (account.getPassword() == null || account.getPassword().length() == 0 || repeatedPassword == null || repeatedPassword.length() == 0) {
            String msg = "密码不能为空";
            model.addAttribute("msg", msg);
            return "account/new_account";
        } else if (!account.getPassword().equals(repeatedPassword)) {
            String msg = "两次密码不一致";
            model.addAttribute("msg", msg);
            return "account/new_account";
        } else {
           // accountService.updateAccount(account);
           // account = accountService.getAccount(account.getUsername());
           // List<Product> myList = categoryService.getProductListByCategory(account.getFavouriteCategoryId());
            //boolean authenticated = true;
           // model.addAttribute("account", account);
           // model.addAttribute("myList", myList);
          //  model.addAttribute("authenticated", authenticated);
            return "redirect:/catelog/view";
        }
        //return "redirect:/catelog/view";
    }*/
