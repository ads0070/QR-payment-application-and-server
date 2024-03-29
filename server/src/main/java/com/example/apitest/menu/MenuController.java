package com.example.apitest.menu;

import com.example.apitest.message.Message;
import com.example.apitest.message.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@Controller
public class MenuController {

    @Autowired
    MenuService menuService;

    @PostMapping("/addMenuRequest")
    @ResponseBody
    public Status regist_menu(@RequestBody MenuDTO menuDTO){
        menuService.addmenu(menuDTO);
        Status status=new Status();
        status.setStatus("ok");
        return status;
    }

    @PostMapping("/getMenuInfo")
    @ResponseBody
    public List<Menu> returnMenu(@RequestParam String loginId){
        return menuService.findMenu(loginId);
    }

    @PostMapping("/getMenus")
    @ResponseBody
    public List<Menu> returnMenus(@RequestParam String storeName){
        return menuService.findMenus(storeName);
    }

    @GetMapping(value = "/img", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] imageSearch(@RequestParam("storeName") String storeName,
                              @RequestParam("menuName") String menuName
                              ) throws IOException {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String fileDir = "C:\\menu\\" + storeName + "\\" + menuName + ".jpeg";

        try {
            fis = new FileInputStream(fileDir);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int readCount = 0;
        byte[] buffer = new byte[1024];
        byte[] fileArray = null;

        try {
            while ((readCount = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, readCount);
            }
            fileArray = baos.toByteArray();
            fis.close();
            baos.close();
        } catch (IOException e) {
            throw new RuntimeException("File Error");
        }
        return fileArray;
    }


    @PostMapping("/removeMenu") //메뉴 지우기
    @ResponseBody
    public Message removeMenuRequest(@RequestParam String loginId, @RequestParam String menuName){
        menuService.removeMenu(loginId,menuName);
        System.out.println("메뉴 삭제중");
        Message message=new Message();
        message.setMessage("메뉴 삭제 성공");
        return message;
    }

    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public Map fileUpload(HttpServletRequest req, HttpServletResponse rep, @RequestParam Map<String, String> paramMap) {

        String menuName=paramMap.get("menuName");
        String menuPrice=paramMap.get("menuPrice");
        String menuDesc=paramMap.get("menuDesc");
        String loginId=paramMap.get("loginId");
        String storeName=paramMap.get("storeName");

        String path = "C:\\menu\\"+storeName;
        String fileName = "";

        Map returnObject = new HashMap();
        try {
            MultipartHttpServletRequest mhsr = (MultipartHttpServletRequest) req;
            Iterator iter = mhsr.getFileNames();
            MultipartFile mfile = null;
            String fieldName = "";
            List resultList = new ArrayList();

            File dir = new File(path);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }

            while (iter.hasNext()) {
                fieldName = (String) iter.next();
                mfile = mhsr.getFile(fieldName);
                fileName = new String(mfile.getOriginalFilename().getBytes("8859_1"), "UTF-8");

                if ("".equals(fileName)) {
                    continue;
                }

                File serverFile = new File(path + File.separator + menuName+".jpeg");
                mfile.transferTo(serverFile);

                Map file = new HashMap();
                file.put("fileName", menuName+".jpeg");
                file.put("sfile", serverFile);
                resultList.add(file);
            }

            returnObject.put("files", resultList);
            returnObject.put("params", mhsr.getParameterMap());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        menuService.registMenu(menuName,menuPrice,menuDesc,loginId,path+"\\"+menuName+".jpeg",storeName);
        return null;
    }

}
