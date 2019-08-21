package com.sandboni.core.engine.render.banner;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BannerRenderServiceTest {

    @Test
    public void testBasic(){
        BannerRenderService bannerRenderService = new BannerRenderService();
        bannerRenderService.render();
        assertTrue(true);
    }
}
