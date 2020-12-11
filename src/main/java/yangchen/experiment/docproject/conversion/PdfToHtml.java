package yangchen.experiment.docproject.conversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.OutputStreamWriter;

import org.apache.pdfbox.PDFReader;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.util.PDFTextStripper;


public class PdfToHtml {

    private PdfToHtml() {}


    /**
     * 获取格式化后的时间信息
     * @param calendar   时间信息
     * @return
     */
    public static String dateFormat( Calendar calendar ){
        if( null == calendar )
            return null;
        String date = null;
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat( pattern );
        date = format.format( calendar.getTime() );
        return date == null ? "" : date;
    }

    /**打印纲要**/
    public static void getPDFOutline(String file){
        try {
            //打开pdf文件流
            FileInputStream fis = new   FileInputStream(file);
            //加载 pdf 文档,获取PDDocument文档对象
            PDDocument document=PDDocument.load(fis);
            //获取PDDocumentCatalog文档目录对象
            PDDocumentCatalog catalog=document.getDocumentCatalog();
            //获取PDDocumentOutline文档纲要对象
            PDDocumentOutline outline=catalog.getDocumentOutline();
            //获取第一个纲要条目（标题1）
            PDOutlineItem item=outline.getFirstChild();
            if(outline!=null){
                //遍历每一个标题1
                while(item!=null){
                    //打印标题1的文本
                    System.out.println("Item:"+item.getTitle());
                    //获取标题1下的第一个子标题（标题2）
                    PDOutlineItem child=item.getFirstChild();
                    //遍历每一个标题2
                    while(child!=null){
                        //打印标题2的文本
                        System.out.println("    Child:"+child.getTitle());
                        //指向下一个标题2
                        child=child.getNextSibling();
                    }
                    //指向下一个标题1
                    item=item.getNextSibling();
                }
            }
            //关闭输入流
            document.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PDFReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PDFReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**打印一级目录**/
    public static void getPDFCatalog(String file){
        try {
            //打开pdf文件流
            FileInputStream fis = new   FileInputStream(file);
            //加载 pdf 文档,获取PDDocument文档对象
            PDDocument document=PDDocument.load(fis);
            //获取PDDocumentCatalog文档目录对象
            PDDocumentCatalog catalog=document.getDocumentCatalog();
            //获取PDDocumentOutline文档纲要对象
            PDDocumentOutline outline=catalog.getDocumentOutline();
            //获取第一个纲要条目（标题1）
            if(outline!=null){
                PDOutlineItem item=outline.getFirstChild();
                //遍历每一个标题1
                while(item!=null){
                    //打印标题1的文本
                    System.out.println("Item:"+item.getTitle());
                    //指向下一个标题1
                    item=item.getNextSibling();
                }
            }
            //关闭输入流
            document.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PDFReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PDFReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**获取PDF文档元数据**/
    public static void getPDFInformation(String file){
        try {
            //打开pdf文件流
            FileInputStream fis = new   FileInputStream(file);
            //加载 pdf 文档,获取PDDocument文档对象
            PDDocument document=PDDocument.load(fis);
            /** 文档属性信息 **/
            PDDocumentInformation info = document.getDocumentInformation();

            System.out.println("页数:"+document.getNumberOfPages());

            System.out.println( "标题:" + info.getTitle() );
            System.out.println( "主题:" + info.getSubject() );
            System.out.println( "作者:" + info.getAuthor() );
            System.out.println( "关键字:" + info.getKeywords() );

            System.out.println( "应用程序:" + info.getCreator() );
            System.out.println( "pdf 制作程序:" + info.getProducer() );

            System.out.println( "Trapped:" + info.getTrapped() );

            System.out.println( "创建时间:" + dateFormat( info.getCreationDate() ));
            System.out.println( "修改时间:" + dateFormat( info.getModificationDate()));

            //关闭输入流
            document.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PDFReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PDFReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * 提取部分PDF文本转HTML
     *
     * @param file
     *            pdf文档路径
     * @param startPage
     *            开始页数
     * @param endPage
     *            结束页数
     * @param htmlFile
     *            保存文件路径
     */
    public static void getText(String pdfFile, int startPage, int endPage,
                        String htmlFile) throws Exception {

        // 是否排序
        boolean sort = true;
        // hpd文件路径
        // String pdfFile = "D:\\PDF\\" + file;
        // 编码方式
        String encoding = "UTF-8";
        // 开始提取页数
        // int startPage = 1;
        // 结束提取页数,最大
        // int endPage = Integer.MAX_VALUE;

        // 文件输入流，生成文本文件
        Writer output = null;
        // 内存中存储的PDF Document
        PDDocument document = null;

        FileInputStream is = null;
        String result = null;
        try {
            try {
                is = new FileInputStream(pdfFile);
                PDFParser parser = new PDFParser(is);
                parser.parse();
                // 创建pdf对象
                document = parser.getPDDocument();
                PDFTextStripper stripper = new PDFTextStripper();
                // 设置是否排序
                stripper.setSortByPosition(sort);
                // 设置起始页
                stripper.setStartPage(startPage);
                // 设置结束页
                stripper.setEndPage(endPage);
                // 调用PDFTextStripper的getText提取文字
                String str = stripper.getText(document);
                result = new String(str.replace("\r\n", "</br> \r\n"));

                // 以原来pdf名称来命名转换的html文件
                // if (file.length() > 4) {
                // File outputFile = new File(file.substring(0,
                // file.length() - 4)
                // + ".html");
                // htmlName = outputFile.getName();
                // }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // 文件输入流，写入HTML文件
            output = new OutputStreamWriter(new FileOutputStream(htmlFile),
                    encoding);

            output
                    .write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"> \r\n");
            output.write("<html> \r\n");
            output.write("<head> \r\n");
            output
                    .write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"> \r\n");
            output.write("</head> \r\n");
            output.write("<body> \r\n");
            output.write("<center>");
            output.write(result);
            // 调用PDFTextStripper的writeText提取并输出文本
            // stripper.writeText(document, output);
            output.write("</center>");
            output.write("</body> \r\n");
            output.write("</html> \r\n");
        } finally {
            if (output != null) {
                // 关闭输出流
                output.close();
            }
            if (document != null) {
                // 关闭PDF Document
                document.close();
            }
        }
    }

    public static void getText(PDDocument document, int startPage, int endPage,
                        String htmlFile) throws Exception {

        // 是否排序
        boolean sort = true;
        // hpd文件路径
        // String pdfFile = "D:\\PDF\\" + file;
        // 编码方式
        String encoding = "UTF-8";
        // 开始提取页数
        // int startPage = 1;
        // 结束提取页数,最大
        // int endPage = Integer.MAX_VALUE;

        // 文件输入流，生成文本文件
        Writer output = null;
        // 内存中存储的PDF Document

        FileInputStream is = null;
        String result = null;
        try {
            try {
                //is = new FileInputStream(pdfFile);
                //PDFParser parser = new PDFParser(is);
                //parser.parse();
                // 创建pdf对象
                //document = parser.getPDDocument();
                PDFTextStripper stripper = new PDFTextStripper();
                // 设置是否排序
                stripper.setSortByPosition(sort);
                // 设置起始页
                stripper.setStartPage(startPage);
                // 设置结束页
                stripper.setEndPage(endPage);
                // 调用PDFTextStripper的getText提取文字
                String str = stripper.getText(document);
                result = new String(str.replace("\r\n", "</br> \r\n"));

                // 以原来pdf名称来命名转换的html文件
                // if (file.length() > 4) {
                // File outputFile = new File(file.substring(0,
                // file.length() - 4)
                // + ".html");
                // htmlName = outputFile.getName();
                // }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // 文件输入流，写入HTML文件
            output = new OutputStreamWriter(new FileOutputStream(htmlFile),
                    encoding);

            output
                    .write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"> \r\n");
            output.write("<html> \r\n");
            output.write("<head> \r\n");
            output
                    .write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"> \r\n");
            output.write("</head> \r\n");
            output.write("<body> \r\n");
            output.write("<center>");
            output.write(result);
            // 调用PDFTextStripper的writeText提取并输出文本
            // stripper.writeText(document, output);
            output.write("</center>");
            output.write("</body> \r\n");
            output.write("</html> \r\n");
        } finally {
            if (output != null) {
                // 关闭输出流
                output.close();
            }
            if (document != null) {
                // 关闭PDF Document
                document.close();
            }
        }
    }

    /**
     * 提取PDF图片并保存
     *
     * @param file
     *            PDF文档路径
     * @param imgSavePath
     *            图片保存路径
     */
    public void getImage(String pdfFile, String imgSavePath) {
        try {
            // 打开pdf文件流
            FileInputStream fis = new FileInputStream(pdfFile);
            // 加载 pdf 文档,获取PDDocument文档对象
            PDDocument document = PDDocument.load(fis);
            /** 文档页面信息 **/
            // 获取PDDocumentCatalog文档目录对象
            PDDocumentCatalog catalog = document.getDocumentCatalog();
            // 获取文档页面PDPage列表
            List pages = catalog.getAllPages();
            int count = 1;
            int pageNum = pages.size(); // 文档页数
            // 遍历每一页
            for (int i = 0; i < pageNum; i++) {
                // 取得第i页
                PDPage page = (PDPage) pages.get(i);
                if (null != page) {
                    PDResources resource = page.findResources();
                    // 获取页面图片信息
                    Map<String, PDXObjectImage> imgs = resource.getImages();
                    for (Map.Entry<String, PDXObjectImage> me : imgs.entrySet()) {
                        // System.out.println(me.getKey());
                        PDXObjectImage img = me.getValue();
                        // 保存图片，命名
                        img.write2file(imgSavePath + "PDF-" + count);
                        count++;
                    }
                }
            }
            document.close();
            fis.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PDFReader.class.getName()).log(Level.SEVERE, null,
                    ex);
        } catch (IOException ex) {
            Logger.getLogger(PDFReader.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    public static void main(String[] args) {

        PdfToHtml pth = new PdfToHtml();

        String pdfFile = "D:\\PDF\\2.pdf";

//		int startPage = 1;
//		int endPage = Integer.MAX_VALUE;
//		String htmlFile = "D:\\HTML\\2.html";
        long startTime = System.currentTimeMillis();
        try {
            pth.getText("D:\\PDF\\2.pdf", 1, Integer.MAX_VALUE,"D:\\HTML\\2.html");
            pth.getImage(pdfFile, "D:\\HTML\\");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("读写所用时间为：" + (endTime - startTime) + "ms    " + pdfFile
                + "转换完成");
    }


}
