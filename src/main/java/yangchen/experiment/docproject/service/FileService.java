package yangchen.experiment.docproject.service;

import com.aspose.words.SaveFormat;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
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

    /**
     * Transform the input file to html and save to a local file
     * @param file
     * @throws Exception
     */
    public void processFile(MultipartFile file) throws Exception {

        String extension = getExtension(file.getOriginalFilename());
        String fileName = getFileName(file.getOriginalFilename());

        System.out.println(Paths.get("").toAbsolutePath().toString());

        //handles pdf file
        File f = new File("src/main/resources/target.tmp");

        OutputStream os = new FileOutputStream(f);
        os.write(file.getBytes());

        if (extension.equals("pdf")) {

            RandomAccessRead is = new RandomAccessBufferedFileInputStream(f);
            PDFParser parser = new PDFParser(is);
            parser.parse();

            PDDocument document = parser.getPDDocument();
            Writer output = new PrintWriter("src/output/" + fileName + ".html", "utf-8");
            new PDFDomTree().writeText(document, output);
            output.close();
        }
        else if(extension.equals("docx")){

            InputStream in = new FileInputStream(f);
            XWPFDocument document = new XWPFDocument(in);

            XHTMLOptions options = XHTMLOptions.create().URIResolver(new FileURIResolver((new File("\"src/output/\" + fileName + \".html\""))));
            OutputStream out = new ByteArrayOutputStream();

            XHTMLConverter.getInstance().convert(document, out, options);

        }
        else if (extension.equals("doc")){
            InputStream in = new FileInputStream(f);
            HWPFDocument document = new HWPFDocument(in);

            WordToHtmlConverter converter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            converter.processDocument(document);

            Document html = converter.getDocument();
            OutputStream out = new FileOutputStream("src/output/" + fileName + ".html");
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

    /**
     * extract and return the text inside the file, omly support pdf and docx extension
     * @param file
     * @return
     */
    public String getFileContent(MultipartFile file) throws IOException {

        System.out.println("D");

        String extension = getExtension(file.getOriginalFilename());

        File f = new File("src/main/resources/target.tmp");

        OutputStream os = new FileOutputStream(f);
        os.write(file.getBytes());

        if(extension.equals("pdf")){
            System.out.println("E");
            PDFTextStripper stripper = null;
            PDDocument document = null;
            PDFParser parser = new PDFParser(new RandomAccessBufferedFileInputStream(f));
            parser.parse();
            try{
                COSDocument cosDoc = parser.getDocument();
                stripper = new PDFTextStripper();
                document = new PDDocument(cosDoc);
                stripper.setStartPage(1);
                stripper.setEndPage(document.getNumberOfPages());
                return stripper.getText(document);
            } catch (Exception e){
                e.printStackTrace();
            }

        }

        System.out.println("SSS");

        return null;

    }

    private String getFileName(String originalFilename) {

        return originalFilename.substring(0, originalFilename.lastIndexOf('.'));

    }

    private String getExtension(String name){
        int i = name.lastIndexOf('.');
        return name.substring(i+1);
    }



}



