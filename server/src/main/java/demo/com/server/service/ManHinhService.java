package demo.com.server.service;

import demo.com.server.entity.ManHinh;
import org.springframework.stereotype.Service;

@Service
public class ManHinhService {

    public ManHinh getManHinh2(String ten1){
        ManHinh manHinh = new ManHinh();
        manHinh.setId(2);
        manHinh.setName(ten1);
        manHinh.setPrice(521);

        return manHinh;
    }
}
