package com.meeting.chatroom.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;

@Controller
public class IndexController {

    @ResponseBody
    @GetMapping(value = "/index", produces = MediaType.TEXT_HTML_VALUE)
    public byte[] index() {
        File file = new File("D:\\IDEA\\sdumeeting\\parent\\chat\\src\\main\\resources\\templates\\index.html");
        if (!file.exists()) {
            System.out.println("换一个目录");
        }
        FileInputStream input = null;
        ByteArrayOutputStream output = null;
        try {
            input = new FileInputStream(file);
            output = new ByteArrayOutputStream();
            int read = -1;
            byte[] bytes = new byte[1024];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return output.toByteArray();
        } catch (IOException exception) {
            return null;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException exception) {

            }
        }
    }

}
