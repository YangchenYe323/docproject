package yangchen.experiment.docproject.service;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.mabb.fontverter.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yangchen.experiment.docproject.valueobject.Policy;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileService {


    public FileService(){
    }


    /**
     * extract text from the given file
     * @param map
     * @param file
     * @return
     * @throws IOException
     */
    public Policy process(Map<String, Integer> map, MultipartFile file) throws IOException {

        String text = null;
        List<String> tags = new ArrayList<String >();
        Map<String, Integer> subjectMap = new HashMap<>();
        Map<String, Integer> scopeMap = new HashMap<>();
        Map<String, Integer> classMap = new HashMap<>();


        String extension = getExtension(file.getOriginalFilename());

        File f = new File("src/main/resources/target.tmp");

        OutputStream os = new FileOutputStream(f);
        os.write(file.getBytes());

        if(extension.equals("pdf")){
            PDFTextStripper stripper = null;
            PDDocument document = null;
            PDFParser parser = new PDFParser(new RandomAccessBufferedFileInputStream(f));
            parser.parse();
            COSDocument cosDoc = parser.getDocument();
            stripper = new PDFTextStripper();
            document = new PDDocument(cosDoc);
            stripper.setStartPage(1);
            stripper.setEndPage(document.getNumberOfPages());
            text = stripper.getText(document);

        } else if(extension.equals("docx")){
            XWPFDocument doc = new XWPFDocument(new FileInputStream(f));
            XWPFWordExtractor ex = new XWPFWordExtractor(doc);

            text = ex.getText();
        } else{
            throw new RuntimeException("Document Type Not Supported");
        }

        //scan text by chinese character
        for (int offset = 0; offset < text.length();){
            int codepoint = text.codePointAt(offset);
            String ch = new String(Character.toChars(codepoint));


            //适用对象
            if (ch.equals("企")){
                int freq = map.getOrDefault("企业", 0);
                subjectMap.put("企业", freq+1);
            }

            if (ch.equals("人")){
                int freq = map.getOrDefault("人才", 0);
                subjectMap.put("人才", freq+1);
            }

            //政策级别
            if (ch.equals("国")){
                int freq = map.getOrDefault("国家级", 0);
                scopeMap.put("国家级", freq+1);
            }

            if (ch.equals("省")){
                int freq = map.getOrDefault("省/直辖市级", 0);
                scopeMap.put("省/直辖市级", freq+1);
            }

            if (ch.equals("市")){
                int freq = map.getOrDefault("地市级", 0);
                scopeMap.put("地市级", freq+1);
            }

            if (ch.equals("区") || ch.equals("县")){
                int freq = map.getOrDefault("地市级", 0);
                scopeMap.put("地市级", freq+1);

                freq = map.getOrDefault("区县级", 0);
                scopeMap.put("区县级", freq+1);
            }

            //政策类型
            if (ch.equals("产")){
                int freq = map.getOrDefault("产业", 0);
                classMap.put("产业", freq+1);
            }

            if (ch.equals("金")){
                int freq = map.getOrDefault("金融", 0);
                classMap.put("金融", freq+1);
            }

            if (ch.equals("科")){
                int freq = map.getOrDefault("科技", 0);
                classMap.put("科技", freq+1);
            }

            if (ch.equals("税")){
                int freq = map.getOrDefault("税收", 0);
                classMap.put("税收", freq+1);
            }

            if (ch.equals("专")){
                int freq = map.getOrDefault("专利", 0);
                classMap.put("专利", freq+1);
            }

            offset += Character.charCount(codepoint);
        }

        //Analyze the map to determine which tags to show
        if (subjectMap.getOrDefault("企业", 0) > subjectMap.getOrDefault("人才", 0)){
            tags.add("企业");
        } else{
            tags.add("人才");
        }

        String scope = (String)scopeMap.keySet().toArray() [0];
        for (String key: scopeMap.keySet()){
            if (scopeMap.get(key) > scopeMap.get(scope)){
                scope = key;
            }
        }
        tags.add(scope);

        String policy = (String)classMap.keySet().toArray() [0];
        for (String key: classMap.keySet()){
            if (classMap.get(key) > classMap.get(policy)){
                policy = key;
            }
        }
        tags.add(policy);


        //return a Policy object that contains all the information needed
        Policy response = new Policy();
        response.setText(text);
        response.setTags(tags);


        return response;
    }

    /**
     * extract and return the text inside the file, omly support pdf and docx extension
     * @param file
     * @return
     */
    public String getFileContent(MultipartFile file) throws IOException {

        String extension = getExtension(file.getOriginalFilename());

        File f = new File("src/main/resources/target.tmp");

        OutputStream os = new FileOutputStream(f);
        os.write(file.getBytes());

        if(extension.equals("pdf")){
            PDFTextStripper stripper = null;
            PDDocument document = null;
            PDFParser parser = new PDFParser(new RandomAccessBufferedFileInputStream(f));
            parser.parse();
            COSDocument cosDoc = parser.getDocument();
            stripper = new PDFTextStripper();
            document = new PDDocument(cosDoc);
            stripper.setStartPage(1);
            stripper.setEndPage(document.getNumberOfPages());
            String text = stripper.getText(document);

            //pc.setText(text);
            //System.out.println(text);
            for (int i = 0; i < text.codePointCount(0, text.length()); ++i){
                String ch = new String(Character.toChars(text.codePointAt(i)));
                System.out.println(ch);
            }

            System.out.println();

            return text;

        } else if(extension.equals("docx")){
            XWPFDocument doc = new XWPFDocument(new FileInputStream(f));
            XWPFWordExtractor ex = new XWPFWordExtractor(doc);

            String text = ex.getText();
            //pc.setText(text);

            return text;
        } else{
            //file extension not supported
            throw new NotImplementedException();
        }
    }

    private String getFileName(String originalFilename) {

        return originalFilename.substring(0, originalFilename.lastIndexOf('.'));

    }

    private String getExtension(String name){
        int i = name.lastIndexOf('.');
        return name.substring(i+1);
    }




}



