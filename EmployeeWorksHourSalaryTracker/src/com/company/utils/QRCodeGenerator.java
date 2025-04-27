package com.company.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class QRCodeGenerator {
    // This stub creates a simple patterned image as a placeholder for a QR Code.
    public static BufferedImage generateQRCode(String data) {
        int size = 200;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        // Fill background white
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size, size);
        // Draw grid lines in black for a visual pattern
        g.setColor(Color.BLACK);
        for (int i = 0; i < size; i += 10) {
            g.drawLine(0, i, size, i);
            g.drawLine(i, 0, i, size);
        }
        g.dispose();
        return image;
    }
}
