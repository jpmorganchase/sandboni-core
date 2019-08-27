package com.sandboni.core.engine.render.banner;

import com.sandboni.core.engine.common.CachingSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.function.Supplier;

public class BannerRenderService {

    private final Supplier<String> bannerSupplier = new CachingSupplier<>(this::bannerToDisplay);

    private static final String LOGO_FILE_NAME = "/sandboni.banner";

    private static final Logger log = LoggerFactory.getLogger(BannerRenderService.class);


    public void render() {
        log.info("{}", bannerSupplier.get());
    }

    private String bannerToDisplay() {
        final String N_LINE = "\n";
        StringBuilder builder = new StringBuilder(N_LINE);

        try (InputStream in = getClass().getResourceAsStream(LOGO_FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()))){

            String l;
            while ((l = reader.readLine()) != null) {
                builder.append(l).append(N_LINE);
            }
        } catch (IOException e) {
            log.warn("Error in rendering Sandboni banner : {} , {}", LOGO_FILE_NAME, e);
        }
        return builder.toString();
    }
}