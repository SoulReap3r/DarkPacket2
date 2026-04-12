package core.svg.svg;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Base64;

public class Canvas extends Entity {
    private final javafx.scene.canvas.Canvas source;

    public Canvas(final javafx.scene.canvas.Canvas source) {
        this.source = source;
    }

    @Override
    public String toSvg(final TransformStack transforms) {
        final WritableImage image = source.snapshot(null, null);

        StringBuilder result = new StringBuilder();

        result.append("<image x='").append(transforms.get().getX() + source.getLayoutX());
        result.append("' y='").append(transforms.get().getY() + source.getLayoutY());
        result.append("' width='").append(source.getWidth());
        result.append("' height='").append(source.getHeight());
        result.append("' xlink:href='data:image/png;base64,");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            PixelReader pr = image.getPixelReader();

            int iw = (int) source.getWidth();
            int ih = (int) source.getHeight();

            BufferedImage img = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
            int[] data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
            int scan = iw;
            int offset = 0;
            WritablePixelFormat<IntBuffer> pf = PixelFormat.getIntArgbInstance();
            pr.getPixels(0, 0, iw, ih, pf, data, offset, scan);

            ImageIO.write(img, "png", stream);
        } catch(IOException ex) {
            //Ignore
        }
        result.append(Base64.getEncoder().encodeToString(stream.toByteArray()));

        result.append("' />");

        return result.toString();
    }
}
