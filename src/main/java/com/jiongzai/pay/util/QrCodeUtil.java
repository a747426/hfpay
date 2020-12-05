package com.jiongzai.pay.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QrCodeUtil {
	/**
     * 生成包含字符串信息的二维码图片
     * @param outputStream 文件输出流路径
     * @param content 二维码携带信息
     * @param qrCodeSize 二维码图片大小
     * @param imageFormat 二维码的格式
     * @throws WriterException 
     * @throws IOException 
     */
    public static boolean createQrCode(String  filePath, String content, int qrCodeSize, String imageFormat) throws WriterException, IOException{  
            //设置二维码纠错级别ＭＡＰ
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();  
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);  // 矫错级别  
            QRCodeWriter qrCodeWriter = new QRCodeWriter();  
            //创建比特矩阵(位矩阵)的QR码编码的字符串  
            BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);  
            // 使BufferedImage勾画QRCode  (matrixWidth 是行二维码像素点)
            int matrixWidth = byteMatrix.getWidth();  
            BufferedImage image = new BufferedImage(matrixWidth-200, matrixWidth-200, BufferedImage.TYPE_INT_RGB);  
            image.createGraphics();  
            Graphics2D graphics = (Graphics2D) image.getGraphics();  
            graphics.setColor(Color.WHITE);  
            graphics.fillRect(0, 0, matrixWidth, matrixWidth);  
            // 使用比特矩阵画并保存图像
            graphics.setColor(Color.BLACK);  
            for (int i = 0; i < matrixWidth; i++){
                for (int j = 0; j < matrixWidth; j++){
                    if (byteMatrix.get(i, j)){
                        graphics.fillRect(i-100, j-100, 1, 1);  
                    }
                }
            }
            OutputStream outputStream=new FileOutputStream(new File(filePath));
            return ImageIO.write(image, imageFormat, outputStream);  
    }  
    
    public static void responseQrCode(String content, int qrCodeSize, String imageFormat,HttpServletResponse response) throws WriterException, IOException{  
        //设置二维码纠错级别ＭＡＰ
       /* Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();  
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);  // 矫错级别  
        QRCodeWriter qrCodeWriter = new QRCodeWriter();  
        //创建比特矩阵(位矩阵)的QR码编码的字符串  
        BitMatrix byteMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hintMap);  
        // 使BufferedImage勾画QRCode  (matrixWidth 是行二维码像素点)
        int matrixWidth = byteMatrix.getWidth();  
        BufferedImage image = new BufferedImage(matrixWidth-200, matrixWidth-200, BufferedImage.TYPE_INT_RGB);  
        image.createGraphics();  
        Graphics2D graphics = (Graphics2D) image.getGraphics();  
        graphics.setColor(Color.WHITE);  
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);  
        // 使用比特矩阵画并保存图像
        graphics.setColor(Color.BLACK);  
        for (int i = 0; i < matrixWidth; i++){
            for (int j = 0; j < matrixWidth; j++){
                if (byteMatrix.get(i, j)){
                    graphics.fillRect(i-100, j-100, 1, 1);  
                }
            }
        }
        graphics.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        response.setContentType("image/jpg");
        response.setContentLength(baos.size());
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(baos.toByteArray());
        outputStream.flush();
        outputStream.close();*/
        
		// 定义二维码的参数
		HashMap hints = new HashMap();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
		hints.put(EncodeHintType.MARGIN, 2);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize, hints);
		BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, imageFormat, out);
        response.setContentType("image/"+imageFormat);
        response.setContentLength(out.size());
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(out.toByteArray());
        outputStream.flush();
        outputStream.close();
    }  
      
    /**
     * 读二维码并输出携带的信息
     */
    public static String readQrCode(InputStream inputStream) throws IOException{  
        //从输入流中获取字符串信息
        BufferedImage image = ImageIO.read(inputStream);  
        //将图像转换为二进制位图源
        LuminanceSource source = new BufferedImageLuminanceSource(image);  
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));  
        QRCodeReader reader = new QRCodeReader();  
        Result result = null ;  
        try {
         result = reader.decode(bitmap);  
        } catch (ReaderException e) {
            e.printStackTrace();  
        }
        return result.getText();  
    }
    
    /**
     * 生成讲课二维码
     * @param content
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static String createTeachQRCode(String id) throws WriterException, IOException {
    	String qrCodeUrl="/qrcode/"+id+".jpg";
    	createQrCode("src\\main\\webapp\\qrcode\\"+id+".jpg",id,900,"JPEG");
    	return qrCodeUrl;
    }
    
    public static void main(String[] args) throws Exception {
    	File file = new File("D:\\21.jpg");
    	InputStream input = new FileInputStream(file);
    	System.out.println(QrCodeUtil.readQrCode(input));
    	 
		// 定义参数：
		/*
		 * int width = 300; // 图片宽度 int height = 300; // 图片高度 String format = "png"; //
		 * 图片格式 String content = "http://blog.csdn.net/wyf2017?ref=toolbar";// 二维码内容
		 * 
		 * // 定义二维码的参数 HashMap hints = new HashMap();
		 * hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		 * hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
		 * hints.put(EncodeHintType.MARGIN, 2); // 生成二维码
		 * 
		 * // 1.定义HashMap hints // 2.hints调用put函数设置字符集、间距以及纠错度为M //
		 * 3.最后用MultiformatWriter函数类调用echoed函数并返回一个值 然后写入文件
		 * 
		 * try { BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
		 * BarcodeFormat.QR_CODE, width, height, hints); //
		 * 这里路径后面的img.png不可省略，前面是自己选取生成的图片地址 File file = new File("D:/img.png");
		 * MatrixToImageWriter.writeToFile(bitMatrix, format, file);
		 * //writeToPath(bitMatrix, format, file); } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
 
	}
 
}
