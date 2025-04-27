package com.company.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeGenerator {
    private static final Logger LOGGER = Logger.getLogger(QRCodeGenerator.class.getName());
    private static final int DEFAULT_SIZE = 200;
    private static final String DEFAULT_FORMAT = "PNG";
    private static final Color DEFAULT_BACKGROUND = Color.WHITE;
    private static final Color DEFAULT_FOREGROUND = Color.BLACK;
    
    public static BufferedImage generateQRCode(String data) {
        return generateQRCode(data, DEFAULT_SIZE, DEFAULT_BACKGROUND, DEFAULT_FOREGROUND, ErrorCorrectionLevel.H);
    }
    
    public static BufferedImage generateQRCode(String data, int size, Color background, Color foreground, ErrorCorrectionLevel errorCorrection) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrection);
            hints.put(EncodeHintType.MARGIN, 2);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size, hints);
            
            BufferedImage qrImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? foreground.getRGB() : background.getRGB());
                }
            }
            
            LOGGER.info("QR Code generated successfully for data length: " + data.length());
            return qrImage;
            
        } catch (WriterException e) {
            LOGGER.log(Level.SEVERE, "Error generating QR code", e);
            return createErrorImage(size);
        }
    }
    
    public static BufferedImage generateQRCodeWithLogo(String data, BufferedImage logo) {
        return generateQRCodeWithLogo(data, logo, DEFAULT_SIZE, DEFAULT_BACKGROUND, DEFAULT_FOREGROUND, ErrorCorrectionLevel.H);
    }
    
    public static BufferedImage generateQRCodeWithLogo(String data, BufferedImage logo, int size, Color background, Color foreground, ErrorCorrectionLevel errorCorrection) {
        try {
            // Generate QR code
            BufferedImage qrImage = generateQRCode(data, size, background, foreground, errorCorrection);
            
            // Calculate logo size (30% of QR code size)
            int logoSize = size / 3;
            BufferedImage scaledLogo = new BufferedImage(logoSize, logoSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledLogo.createGraphics();
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(logo, 0, 0, logoSize, logoSize, null);
            g2d.dispose();
            
            // Insert logo in center
            Graphics2D qrGraphics = qrImage.createGraphics();
            int x = (size - logoSize) / 2;
            int y = (size - logoSize) / 2;
            qrGraphics.drawImage(scaledLogo, x, y, null);
            qrGraphics.dispose();
            
            LOGGER.info("QR Code with logo generated successfully");
            return qrImage;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating QR code with logo", e);
            return createErrorImage(size);
        }
    }
    
    public static boolean saveQRCode(BufferedImage qrCode, String filePath) {
        try {
            File outputFile = new File(filePath);
            String extension = filePath.substring(filePath.lastIndexOf('.') + 1);
            ImageIO.write(qrCode, extension.toUpperCase(), outputFile);
            LOGGER.info("QR Code saved successfully to: " + filePath);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving QR code to file: " + filePath, e);
            return false;
        }
    }
    
    public static byte[] imageToByteArray(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, DEFAULT_FORMAT, baos);
            LOGGER.info("QR Code converted to byte array successfully");
            return baos.toByteArray();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error converting QR code to byte array", e);
            return new byte[0];
        }
    }
    
    public static BufferedImage byteArrayToImage(byte[] imageData) {
        try {
            BufferedImage image = ImageIO.read(new java.io.ByteArrayInputStream(imageData));
            LOGGER.info("Byte array converted to QR Code successfully");
            return image;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error converting byte array to QR code", e);
            return createErrorImage(DEFAULT_SIZE);
        }
    }
    
    private static BufferedImage createErrorImage(int size) {
        BufferedImage errorImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = errorImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, size, size);
        g2d.setColor(Color.RED);
        g2d.drawLine(0, 0, size, size);
        g2d.drawLine(0, size, size, 0);
        g2d.dispose();
        return errorImage;
    }
}
