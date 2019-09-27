package com.sandboni.core.engine.result;

import com.sandboni.core.engine.Arguments;
import com.sandboni.core.engine.ArgumentsBuilder;
import com.sandboni.core.engine.Processor;
import com.sandboni.core.engine.ProcessorBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ResultGeneratorTest {

    private Arguments getArguments(String fromChangeId, boolean runSelectiveModeIfBuildFileHasChanged, boolean runAllExternalTests, String stage) {

        return new ArgumentsBuilder().with($->{
            $.fromChangeId = fromChangeId;
            $.toChangeId = "999eee";
            $.repository = ".";
            $.selectiveMode = runSelectiveModeIfBuildFileHasChanged;
            $.stage = stage;
            $.runAllExternalTests = runAllExternalTests;
        }).build();
    }

    private Processor getProcessor(String fromChangeId, boolean runSelective, boolean runAllExternal, String stage)  {
        return new ProcessorBuilder().with(procBuilder->{
                procBuilder.arguments = getArguments(fromChangeId, runSelective, runAllExternal, stage);
                procBuilder.gitDetector = new ChangeDetectorResultMock();
            }).build();
    }

    @Test
    public void testResultWhenEmptyContext(){
        Processor processor = getProcessor("0", false, false, Arguments.BUILD_STAGE);

        Result res = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS);
        Assert.assertTrue(res.isSuccess());
        Assert.assertEquals(FilterIndicator.SELECTIVE, res.getFilterIndicator());
    }

    @Test
    public void testResultWhenOnlyCnfgContext(){
        Processor processor = getProcessor("1", false, false, Arguments.BUILD_STAGE);
        Result res = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS);
        Assert.assertTrue(res.isSuccess());
        Assert.assertEquals(FilterIndicator.ALL, res.getFilterIndicator());
    }

    @Test
    public void testResultWhenOnlyJavaContext(){
        Processor processor = getProcessor("2", false, false, Arguments.BUILD_STAGE);
        Result res = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS);
        Assert.assertTrue(res.isSuccess());
        Assert.assertEquals(FilterIndicator.SELECTIVE, res.getFilterIndicator());
    }

    @Test
    public void testResultWhenBothBuildCnfgAndJavaContext(){
        Processor processor = getProcessor("3", false, false, Arguments.BUILD_STAGE);
        Result res = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS);
        Assert.assertTrue(res.isSuccess());
        Assert.assertEquals(FilterIndicator.ALL, res.getFilterIndicator());
    }


    @Test
    public void testWhenCnfgVVersionChangeAndJavaContext(){
        Processor processor = getProcessor("4", false, false, Arguments.BUILD_STAGE);
        Result res = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS);
        Assert.assertTrue(res.isSuccess());
        Assert.assertEquals(FilterIndicator.SELECTIVE, res.getFilterIndicator());
    }



    @Test
    public void testResultWhenEmptyContextAndRunSelectiveModeIsTrue(){
        Processor processor = getProcessor("0", true, false, Arguments.BUILD_STAGE);
        Result res = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS);
        Assert.assertTrue(res.isSuccess());
        Assert.assertEquals(FilterIndicator.SELECTIVE, res.getFilterIndicator());
    }

    @Test
    public void testResultWhenOnlyCnfgContextAndRunSelectiveModeIsTrue(){
        Processor processor = getProcessor("1", true, false, Arguments.BUILD_STAGE);
        Result res = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS);
        Assert.assertTrue(res.isSuccess());
        Assert.assertEquals(FilterIndicator.SELECTIVE, res.getFilterIndicator());
    }

    @Test
    public void testResultWhenOnlyJavaContextAndRunSelectiveModeIsTrue(){
        Processor processor = getProcessor("2", true, false, Arguments.BUILD_STAGE);
        Result res = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS);
        Assert.assertTrue(res.isSuccess());
        Assert.assertEquals(FilterIndicator.SELECTIVE, res.getFilterIndicator());
    }

    @Test
    public void testResultWhenBothBuildCnfgAndJavaContextAndRunSelectiveModeIsTrue(){
        Processor processor = getProcessor("2", true, false, Arguments.BUILD_STAGE);
        Result res = processor.getResultGenerator().generate(ResultContent.RELATED_TESTS);
        Assert.assertTrue(res.isSuccess());
        Assert.assertEquals(FilterIndicator.SELECTIVE, res.getFilterIndicator());
    }

    @Test
    public void testResultWhenRunAllExternalTestsIsTrueDuringBuildStage() {
        Processor processor = getProcessor("2", true, true, Arguments.BUILD_STAGE);
        Result res = processor.getResultGenerator().generate(ResultContent.ALL_EXTERNAL_TESTS);
        Assert.assertTrue(res.isSuccess());
        Assert.assertEquals(FilterIndicator.SELECTIVE, res.getFilterIndicator());
    }

    @Test
    public void testResultWhenRunAllExternalTestsIsTrueDuringIntegrationStage() {
        Processor processor = getProcessor("2", true, true, Arguments.INTEGRATION_STAGE);
        Result res = processor.getResultGenerator().generate(ResultContent.ALL_EXTERNAL_TESTS);
        Assert.assertTrue(res.isSuccess());
        Assert.assertEquals(FilterIndicator.ALL_EXTERNAL, res.getFilterIndicator());
    }
}