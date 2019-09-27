package com.sandboni.core.engine.render.banner;

import org.junit.Assert;
import org.junit.Test;

public class BannerRenderServiceTest {

    @Test
    public void testBasic(){
        try {
            BannerRenderService bannerRenderService = new BannerRenderService();
            bannerRenderService.render();
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
