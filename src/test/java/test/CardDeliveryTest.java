package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static ru.netology.data.DataGenerator.*;

public class CardDeliveryTest {

    private final String date = generateDate(3);
    private final String secondDate = generateDate(15);
    private final String expiredDate = generateDate(-15);
    private final String city = generateCity();
    private final String phone = generatePhone();
    private final String name = generateName();


    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setupTest() {
        open("http://localhost:9999");
    }
}
@Test
@DisplayName("Should successful plan and plan meeting")
void shouldSuccessfulPlanAndPlanMeeting() {
    DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
    int daysToAddForFirstMeeting = 4;
    String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
    int daysToAddForSecondMeeting = 7;
    String secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
    $("[data-test-id=city] input").setValue(validUser.getCity());
    $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
    $("[data-test-id=date] input").setValue(firstMeetingDate);
    $("[data-test-id=name] input").setValue(validUser.getName());
    $("[data-test-id=phone] input").setValue(validUser.getPhone());
    $("[data-test-id=agreement]").click();
    $(byText("Запланировать")).click();
    $(byText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
    $("[data-test-id='success-notification'] .notification__content")
            .shouldHave(exactText("Встреча успешно запланирована на " + firstMeetingDate))
            .shouldBe(visible);
    $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
    $("[data-test-id=date] input").setValue(secondMeetingDate);
    $(byText("Запланировать")).click();
    $("[data-test-id='replan-notification'] .notification__content")
            .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"))
            .shouldBe(visible);
    $("[data-test-id='replan-notification'] .button").click();
    $("[data-test-id='success-notification'] .notification__content")
            .shouldHave(exactText("Встреча успешно запланирована на " + secondMeetingDate))
            .shouldBe(visible);
    }

@Test
@DisplayName("Should get error message if entered wrong phone number")
void shouldSetErrorIfWrongPhone() {
    $("[data-test-id='city'] input").setValue(validUser.getCity());
    $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
    $("[data-test-id='date'] input").setValue(firstMeetingDate);
    $("[data-test-id='name'] input").setValue(validUser.getName());
    $("[data-test-id='phone'] input").setValue(DataGenerator.generateWrongPhone("en"));
    $("[data-test-id='agreement']").click();
    $(byText("Запланировать")).click();
    $("[data-test-id='phone'] .input__sub")
            .shouldHave(exactText("Неверный формат номера мобильного телефона"));
    }
}