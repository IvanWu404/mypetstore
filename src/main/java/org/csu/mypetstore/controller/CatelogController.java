package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Category;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.domain.Product;
import org.csu.mypetstore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/catelog")
public class CatelogController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/view")
    public String view(){
        return "catelog/main";
    }

    @GetMapping("/viewCategory")
    public String viewCategory(String categoryId, Model model){
        if(categoryId != null){
            Category category = categoryService.getCategory(categoryId);
            List<Product> productList = categoryService.getProductListByCategory(categoryId);
            model.addAttribute("category",category);
            model.addAttribute("productList",productList);
            return "catelog/category";
        }
        return "catelog/main";
    }
    @GetMapping("/viewProduct")
    public String viewProduct(String productId, Model model) {
        if (productId != null) {
            List<Item> itemList = categoryService.getItemListByProduct(productId);
            Product product = categoryService.getProduct(productId);
            model.addAttribute("product", product);
            model.addAttribute("itemList", itemList);
        }
        return "catelog/product";
    }
    @GetMapping("/viewItem")
    public String viewItem(String itemId, Model model){
        Item item = categoryService.getItem(itemId);
        Product product = item.getProduct();

        processProductDescription(product);
        model.addAttribute("item",item);
        model.addAttribute("product",product);
        return "catelog/item";
    }
    @PostMapping("/searchProducts")
    public String searchProducts(String keyword, Model model){
        if(keyword == null || keyword.length() < 1){
            String msg = "Please enter a keyword to search for, then press the search button.";
            model.addAttribute("msg",msg);
            return "common/error";
        }else {
            List<Product> productList = categoryService.searchProductList(keyword.toLowerCase());
            processProductDescription(productList);
            model.addAttribute("productList",productList);
            return "catelog/search_products";
        }

    }
    private void processProductDescription(Product product){
        String [] temp = product.getDescription().split("\"");
        product.setDescriptionImage(temp[1]);
        product.setDescriptionText(temp[2].substring(1));
    }
    private void processProductDescription(List<Product> productList){
        for(Product product : productList) {
            processProductDescription(product);
        }
    }
}
