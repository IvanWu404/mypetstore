package org.csu.mypetstore;

import org.csu.mypetstore.domain.Category;
import org.csu.mypetstore.domain.Product;
import org.csu.mypetstore.persistence.CategoryMapper;
import org.csu.mypetstore.persistence.OrderMapper;
import org.csu.mypetstore.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@MapperScan("org.csu.mypetstore.persistence")
class MypetstoreApplicationTests {
    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    CategoryService categoryService;


    @Test
    void contextLoads() {
    }

    @Test
    void test(){

        Category c = categoryService.getCategory("BIRDS");
        System.out.println(c.getCategoryId()+" "+c.getName()+" "+c.getDescription());
    }

    @Test
    void productTest(){
        Product p = categoryService.getProduct("AV-CB-01");
        List<Product> list = categoryService.getProductListByCategory("BIRDS");
        System.out.println(list.size());
    }
    @Test
    void orderTest(){
        Product p = categoryService.getProduct("AV-CB-01");
        List<Product> list = categoryService.getProductListByCategory("BIRDS");
        System.out.println(list.size());


    }

}
