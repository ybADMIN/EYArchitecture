package com.yb.ilibray;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Rule
    public MethodRule rule = new RepeatableRule(5, new String[]{"test1"});
    @Rule
    public TestWatcher rule2 = new TestWatcher(){
        @Override
        protected void finished(Description description) {
            System.out.println("finished");
        }
    };
    @Test
    public void test1(){
        System.out.println("我是Test1");
    }
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        System.out.println("我是addition_isCorrect");
    }

    private class RepeatableRule implements MethodRule {
        //Loop times
        int times=1;
        //Loop methods
        String[] testMethods = null;

        RepeatableRule(int times, String[] testMethods){
            this.times = times;
            this.testMethods = testMethods;
        }

        @Override
        public Statement apply(final Statement base, final FrameworkMethod method, Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    int loopTime = 1;
                    if(Arrays.asList(testMethods).contains(method.getName())){//test method name matched
                        loopTime = times;
                    }
                    for(int i=0;i<loopTime;i++)//before() and after() are also executed
                        base.evaluate();
                }
            };
        }
    }
}