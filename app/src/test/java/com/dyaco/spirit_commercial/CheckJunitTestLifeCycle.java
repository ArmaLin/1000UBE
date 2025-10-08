package com.dyaco.spirit_commercial;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class CheckJunitTestLifeCycle {

    /**
     * Before
     * 在這個class的每個測試前都會執行的區塊，可以在這邊放置共用測試的資料，例如Database、JSon、List等等，這樣就不用在每個testing function裡都要重複寫一次。所以我們在這邊設置一個global的list變數給下面的兩個test使用。
     */
    @BeforeClass
    public static void checkBeforeClass() {
        System.out.println("I'm before class");
    }

    @Before
    public void checkBefore() {
        System.out.println("I'm before method");
    }

    /**
     * Test
     * 加入@Test代表這是要被測試的function，當我們呼叫JUnit runner執行class時候這個function會被執行。我們第一個test測試List類別的size欄位是否會回傳正確值，第二個test測試List類別的index功能是否會抓到正確的值。
     */
    @Test
    public void doSomething() {
        System.out.println("Hi I'm test One");
    }

    @Test
    public void doSomethingTwo() {
        System.out.println("Hi I'm test Two");
    }

    @Ignore
    public void wontRun() {
        System.out.println("Hi I won't run");
    }

    @Test(timeout = 5000)
    public void checkTimeout() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Check time out");
    }

    @Test(expected = RuntimeException.class)
    public void checkException() {
        throw new RuntimeException();
    }


    /**
     * Test
     * 在這個class的每個測試後都會執行的區塊，用來釋放資源，例如database、server等等，這邊沒有需要釋放的資源。
     */
    @After
    public void cleanMethod() {
        System.out.println("Clean method");
    }

    @AfterClass
    public static void cleanClass() {
        System.out.println("Clean class");
    }

}