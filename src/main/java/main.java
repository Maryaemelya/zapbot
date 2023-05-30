import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class main {
    public static void main(String[] args) {
        String botName = "medical_appointment_bot"; // В место звездочек указываем имя созданного вами ранее Бота
        String botToken = "6283503469:AAHLmoqlps4-7jDwtcAopDMpxd-SWPI-dxE"; // В место звездочек указываем токен созданного вами ранее Бота
        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new zapbot(botName, botToken));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}