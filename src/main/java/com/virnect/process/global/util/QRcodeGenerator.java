package com.virnect.process.global.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description ZXing BarcodeGenerator
 * @since 2020.04.09
 */
public class QRcodeGenerator {
    public static BufferedImage generateQRCodeImage(String barcodeText, int width, int height) throws Exception {
        return MatrixToImageWriter.toBufferedImage(new QRCodeWriter().encode(barcodeText, BarcodeFormat.QR_CODE, width, height));
    }

    public static BufferedImage generateEAN13BarcodeImage(String barcodeText, int width, int height) throws Exception {
        return MatrixToImageWriter.toBufferedImage(new EAN13Writer().encode(barcodeText, BarcodeFormat.EAN_13, width, height));
    }
}
