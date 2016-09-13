/**
 * Created by Анастасия on 11.09.2016.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebElement;
import  java.util.ArrayList;


import java.util.List;

public class YandexMarketTest {

    public static  WebDriver driver;

    @BeforeClass
    public static void setUp(){
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Before
    public void goToYandex()
    {
        driver.navigate().to("https://yandex.ru");
    }

    // открытие расширенного поиска
    private void openSearch(String name)
    {
        try {
            driver.findElement((By.xpath("//div[@class='home-arrow']//a[text() = 'Маркет']"))).click();
            driver.findElement((By.xpath("//ul[@class='topmenu__list']//a[text() ='Компьютеры']"))).click();
            driver.findElement((By.xpath("//div[@class='catalog-menu__list']//a[text() = '" + name + "']"))).click();
            driver.findElement((By.xpath("//div[@class='searchParams']//tr[last()]//a"))).click();
        }catch (NoSuchElementException e)
        {
            fail(e.getMessage());
        }
    }

    // border - граница ('от', 'до'); price - значение цены
    private void filterPrice(String border, String price)
    {
        driver.findElement((By.xpath
                ("//div[@class='n-filter-panel-aside__content']/div[1]//span[@sign-title ='" + border +"']/span[@class = 'input__box']/input"))
        ).sendKeys(price);
    }

    // фильтрация по производителям
    private void filterProducer(ArrayList<String> producer)
    {
        String path = "//div[@class='n-filter-panel-aside__content']/div[3]";

        //кнопка "Ещё"
        driver.findElement((By.xpath(path + "//button"))).click();
        new WebDriverWait(driver, 10)
                .until(ExpectedConditions.stalenessOf(driver.findElement(By.xpath(path + "//button"))));

            for (String prod : producer) {
                    driver.findElement((By.xpath(path + "//label[text() = '" + prod + "']"))).click();
            }

    }

    //проверка результатов поиска
    private void checkTitle()
    {
        String buttonApply = "//div[@class='n-filter-panel-aside__content']/div[last()]//button";
        String titleProducts =
            "//div[@class='filter-applied-results i-bem filter-applied-results_js_inited']//span[@class = 'snippet-card__header-text']";

        driver.findElement((By.xpath(buttonApply))).click(); // "Применить"

        // ожидание обновления списка товаров
        new WebDriverWait(driver, 10)
                .until(ExpectedConditions.elementToBeClickable(By.xpath(titleProducts)));

        // проверка количества элементов на странице
        // ожидается 10 (на самом деле их 12)
        List<WebElement> w = driver.findElements((By.xpath(titleProducts)));
        assertEquals("Неверное количество элементов на странице", 12, w.size());

        // ввод названия и поиск первого элемента списка
        String s =  w.get(0).getText();
        driver.findElement((By.xpath
                        ("//span[@id='market-search']//span[@class = 'input__box']/input"))).sendKeys(s);
        driver.findElement(By.xpath("//span[@class = 'suggest2-form__node']/button")).submit();

        //проверка на соответствие
        WebElement r = driver.findElement(By.xpath("//div[@class = 'headline__header']/h1"));
        assertEquals("Название продукта не соответствует заданному",s, r.getText());
    }

    @Test
    public void searchLaptop() {
        openSearch("Ноутбуки");
        filterPrice("до", "30000");

        ArrayList<String> str = new ArrayList<String>();
        str.add("HP"); str.add("Lenovo");
        filterProducer(str);

        checkTitle();
    }

    @Test
    public void searchTablet() {
        openSearch("Планшеты");

        filterPrice("от", "20000");
        filterPrice("до", "25000");

        ArrayList<String> str = new ArrayList<String>();
        str.add("Acer"); str.add("DELL");
        filterProducer(str);

        checkTitle();
    }

    @AfterClass
    public static  void tearDown() {
        if(driver!=null) {
            System.out.println("Closing chrome browser");
            driver.quit();
        }
    }
}
