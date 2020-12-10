package yangchen.experiment.docproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import yangchen.experiment.docproject.service.FileService;

@Controller
public class FileController {

    @Autowired
    FileService fileService;

    @GetMapping("/")
    public String index(){
        return "upload";
    }

    @PostMapping("/uploadFile")
    public String processFile(@RequestParam("file")MultipartFile file, RedirectAttributes ra) throws Exception {

        //fileService.processFile(file);


        return "redirect:/";
    }

}
