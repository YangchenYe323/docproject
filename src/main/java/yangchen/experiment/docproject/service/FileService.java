package yangchen.experiment.docproject.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.fit.pdfdom.PDFDomTree;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Paths;

@Service
public class FileService {


    public void processFile(MultipartFile file) throws Exception {

        String extension = getExtension(file.getOriginalFilename());

        System.out.println(Paths.get("").toAbsolutePath().toString());

        //handles pdf file
        File f = new File("src/main/resources/target.tmp");

        OutputStream os = new FileOutputStream(f);
        os.write(file.getBytes());

        if (extension.equals("pdf")) {

            PDDocument document = PDDocument.load(f);
            Writer output = new PrintWriter("src/output/out.html", "utf-8");
            new PDFDomTree().writeText(document, output);
            output.close();
        }
        else if(extension.equals("docx")){
            XWPFDocument document = new XWPFDocument(new FileInputStream(f));
            OutputStream out = new FileOutputStream(new File("src/output/wordout.html"));
            

        }
        else if (extension.equals("doc")){
            InputStream in = new FileInputStream(f);
            HWPFDocument document = new HWPFDocument(in);

            WordToHtmlConverter converter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            converter.processDocument(document);

            Document html = converter.getDocument();
            OutputStream out = new FileOutputStream("src/output/wordout.html");
            DOMSource domSource = new DOMSource(html);
            StreamResult result = new StreamResult(out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer serializer = factory.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");
            serializer.transform(domSource, result);
            out.close();



        }

    }

    private String getExtension(String name){
        int i = name.lastIndexOf('.');
        return name.substring(i+1);
    }



}



