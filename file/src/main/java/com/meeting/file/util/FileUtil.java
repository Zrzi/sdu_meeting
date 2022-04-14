package com.meeting.file.util;

import com.meeting.common.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Component
public class FileUtil {

    @Value("${file.path}")
    public String path = "/www/league";

    @Value("${file.location}")
    public String location = "http://211.87.227.234:8000/";

    @Autowired
    private UUIDUtil uuidUtil;

    /**
     * 处理图片
     * @param type 类型
     * @param file 图片
     * @return 文件地址 http://{ip地址}/{图片 类型}/{文件名}.png
     */
    public String handlePicture(String type, MultipartFile file)
            throws IOException{
        // 文件后缀名
        String suffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
        // 新的文件名
        String fileName = uuidUtil.getUUID() + suffix;
        // 文件目的地址
        File dest = new File(path + '/'+ type + '/' + fileName);
        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdir();
        }
        file.transferTo(dest);
        return location + '/' + type + '/' + dest.getName();
    }

}
