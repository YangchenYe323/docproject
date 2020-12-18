package yangchen.experiment.docproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
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
    public ModelAndView processFile(@RequestParam("file")MultipartFile file, Model model) throws Exception {

        System.out.println("C");

        ModelAndView mv = new ModelAndView();


        mv.addObject("doctext", fileService.getFileContent(file));
        mv.setViewName("upload");


        return mv;
    }

}
