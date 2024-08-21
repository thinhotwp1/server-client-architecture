package demo.com.server.controller;

import demo.com.server.entity.ManHinh;
import demo.com.server.service.ManHinhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManHinhController {

    // client -> controller -> tra ve
    @GetMapping("/get-man-hinh-0")
    // public [Entity] [tenHamTuDat](@RequestParam String ten) {
    public ManHinh getManHinh0(@RequestParam String ten) {
        ManHinh manHinh = new ManHinh();
        manHinh.setId(1);
        manHinh.setName(ten);
        manHinh.setPrice(1000);
        return manHinh;
    }


    @Autowired
    private ManHinhService manHinhService;

    // client -> controller -> service -> tra ve
    @GetMapping("/get-man-hinh-1")
    public ManHinh getManHinh1(@RequestParam String ten1) {
        ManHinh manHinh = manHinhService.getManHinh2(ten1);

        return manHinh;
    }


}
