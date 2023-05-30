import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class zapbot extends TelegramLongPollingBot {
    final String botName;
    final String botToken;
    private int vari = 0;
    private boolean countMes = false;
    public String spec = "";
    public String datepriem = "";
    public String iddoc = "";
    public String fiodoc = "";
    public String idklient = "";
    public String timepriem = "";
    public ArrayList<String[]> place = new ArrayList<String[]>();
    public ArrayList<String[]> vibor = new ArrayList<String[]>();
    public ArrayList<String[]> spKlient = new ArrayList<String[]>();

    public zapbot(String botName, String botToken) {
        this.botName = botName;
        this.botToken = botToken;
    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            if(countMes == true){
            setButtons(sendMessage);}
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("Выбрать прием"));
        keyboardRow1.add(new KeyboardButton("Посмотреть свое расписание приемов"));
        keyboardRowList.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }
    public void setSpecialnost(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("Стоматолог"));
        keyboardRow1.add(new KeyboardButton("Эндокринолог"));
        keyboardRow1.add(new KeyboardButton("Оториноларинголог"));
        keyboardRow1.add(new KeyboardButton("Невролог"));
        keyboardRow1.add(new KeyboardButton("Онколог"));
        keyboardRow1.add(new KeyboardButton("Кардиолог"));
        keyboardRow1.add(new KeyboardButton("Назад"));
        keyboardRowList.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public void setDoctor(SendMessage sendMessage,ArrayList<String[]> docs, int column) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        for(int i = 0; i<docs.size();i++){
            keyboardRow1.add(new KeyboardButton(docs.get(i)[column]));
        }
        keyboardRow1.add(new KeyboardButton("Назад"));
        keyboardRowList.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Проверяем появление нового сообщения в чате, и если это текст
        Message message = update.getMessage();
        String phoneNumber = "";
        HashMap<String, String> chatUserDataMap = null;
        if (vari == 3 || vari==99){
            String userId = String.valueOf(update.getMessage().getChatId());
            chatUserDataMap = new HashMap<String, String>();
            chatUserDataMap.put(userId, "wait_fio_and_date");
        }
        if(StringUtils.isNumeric(message.getText())){
            this.vari = 1000;
            sendMsg(message,"Запись на прием удалена.");
            TestConnection testConnection = new TestConnection();
            int s = Integer.parseInt(message.getText());
            try {
                testConnection.deleteZapis(spKlient.get(s-1)[6],spKlient.get(s-1)[4],spKlient.get(s-1)[5],idklient);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (message != null && message.hasText()) {
            if (message.getText().equals("/start")) {
                this.countMes = false;
                numberphone(message);
            }
            if (message.getText().equals("/help")) {
                sendMsg(message, "Чем могу помочь?");
            }
            if (message.getText().equals("Назад")) {
                if(this.vari == 5){
                    sendMsg(message,"Выберите, что нужно сделать:");
                    this.vari = 100;
                }

            }
            if ((this.vari == 6 && message.getText().equals("Назад"))||message.getText().equals("Выбрать прием") || message.getText().equals("Посмотреть свое расписание приемов")) {
                if ((this.vari == 6 && message.getText().equals("Назад"))|| message.getText().equals("Выбрать прием")) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.enableMarkdown(true);
                    sendMessage.setChatId(message.getChatId().toString());
                    sendMessage.setReplyToMessageId(message.getMessageId());
                    sendMessage.setText("Выберите специальность врача ниже");
                    try {
                        setSpecialnost(sendMessage);
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    this.vari = 5;
                }
                if (message.getText().equals("Посмотреть свое расписание приемов")) {
                    this.vari = 22;
                    TestConnection testConnection = new TestConnection();
                    try {
                        spKlient = testConnection.raspisanieKlient(idklient);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    String spisok = "";
                    for (int i=0;i< spKlient.size();i++){
                        for (int j=0; j< 6;j++) {
                            spisok+= (spKlient.get(i)[j] + "  ");
                            if(j == 2 || j== 3){
                                spisok+=("\n");
                            }
                        }
                        spisok+=("\n");
                        spisok+=("\n");
                    }
                    sendMsg(message, "Ваше расписание: \n\n"+ spisok + "\n\n Если нужно убрать вас из записи, введите номер записи (она указана в начале информации о приеме).");
                }
            }else {
            if((this.vari == 6 && message.getText().equals("Назад")==false) || (this.vari == 8 && message.getText().equals("Назад"))){
                for(int i=0;i< this.vibor.size();i++){
                    if(this.vibor.get(i)[1].equals(message.getText())){
                        iddoc = this.vibor.get(i)[0];
                        this.fiodoc = this.vibor.get(i)[1];
                    }
                }
                TestConnection testConnection = new TestConnection();
                ArrayList<String[]> docs = new ArrayList<String[]>();
                try {
                    docs = testConnection.checkDay(iddoc);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                SendMessage sendMessage = new SendMessage();
                sendMessage.enableMarkdown(true);
                sendMessage.setChatId(message.getChatId().toString());
                sendMessage.setReplyToMessageId(message.getMessageId());
                sendMessage.setText("Выберите дату ниже");
                try {
                    setDoctor(sendMessage,docs,0);
                    execute(sendMessage);
                    this.vari = 7;
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else {
                if ((this.vari == 7 && message.getText().equals("Назад")) || (this.vari != 7 && (message.getText().equals("Стоматолог") || message.getText().equals("Эндокринолог") || message.getText().equals("Кардиолог") || message.getText().equals("Онколог") || message.getText().equals("Невролог") || message.getText().equals("Оториноларинголог")))) {
                    TestConnection testConnection = new TestConnection();
                    ArrayList<String[]> docs = new ArrayList<String[]>();
                    try {
                        if (this.vari != 7) {
                            this.spec = message.getText();
                        }
                        docs = testConnection.checkSpecData(this.spec);
                        this.vibor = docs;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.enableMarkdown(true);
                    sendMessage.setChatId(message.getChatId().toString());
                    sendMessage.setReplyToMessageId(message.getMessageId());
                    sendMessage.setText("Выберите врача ниже");
                    try {
                        setDoctor(sendMessage, docs, 1);
                        execute(sendMessage);
                        this.vari = 6;
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                    if ((this.vari == 7 && message.getText().equals("Назад") == false) || (this.vari == 9 && message.getText().equals("Назад"))) {
                        if(message.getText().equals("Назад") == false){
                            datepriem = message.getText();
                        }
                        TestConnection testConnection = new TestConnection();
                        ArrayList<String[]> docs = new ArrayList<String[]>();
                        try {
                            docs = testConnection.checkTime(iddoc, datepriem);
                            this.place = docs;
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.enableMarkdown(true);
                        sendMessage.setChatId(message.getChatId().toString());
                        sendMessage.setReplyToMessageId(message.getMessageId());
                        sendMessage.setText("Выберите время ниже");
                        try {
                            setDoctor(sendMessage, docs, 0);
                            execute(sendMessage);
                            this.vari = 8;
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        if ((this.vari == 8 && message.getText().equals("Назад") == false) || (this.vari == 10 && message.getText().equals("Назад"))) {
                            ArrayList<String[]> docs = new ArrayList<String[]>();
                            this.timepriem = message.getText();
                            String [] w = new String[1];
                            w[0]="Да";
                            String [] v = new String[1];
                            v[0] = "Нет, вернуться в меню";
                            docs.add(w);
                            docs.add(v);
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.enableMarkdown(true);
                            sendMessage.setChatId(message.getChatId().toString());
                            sendMessage.setReplyToMessageId(message.getMessageId());
                            sendMessage.setText("Записать вас на выбранное время?");
                            try {
                                setDoctor(sendMessage, docs, 0);
                                execute(sendMessage);
                                this.vari = 9;
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            if ((this.vari == 9 && message.getText().equals("Назад") == false) || (this.vari == 11 && message.getText().equals("Назад"))) {
                                this.vari = 10;
                                if(message.getText().equals("Да")){
                                    TestConnection testConnection = new TestConnection();
                                    try {
                                        String[] zapis = testConnection.zapisKlient(iddoc,datepriem,timepriem,idklient);
                                        String[] clinica = testConnection.nameClinic(zapis[0]);
                                        sendMsg(message,"Вы записаны на прием!" +
                                                "\nВрач: " + fiodoc + ".\nАдрес: '" + clinica[1] + "' г. " + clinica[2] + ", " + clinica[3] + "." + "\nПрием: " + zapis[2] + " в " + zapis[3]);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(message.getText().equals("Нет, вернуться в меню")){
                                    sendMsg(message,"Выберите, что нужно сделать:");
                                }
                            }
                            }
                    }
                }
            }
            }
        if (update.getMessage().hasContact()) {
            message.setText("fff");
                sendMsg(message, "Введите ФИО и дату рождения в формате: Иванов Иван Иванович, гггг-мм-дд");
                this.idklient = update.getMessage().getContact().getPhoneNumber();
            this.vari = 3;
        }
        if (chatUserDataMap.containsKey(String.valueOf(update.getMessage().getChatId())) && chatUserDataMap.get(String.valueOf(update.getMessage().getChatId())).equals("wait_fio_and_date")) {
            // Обрабатываем введенное пользователем ФИО и дату рождения
            vari = 0;
            String text = update.getMessage().getText();
            Pattern pattern = Pattern.compile("^([А-ЯЁ][а-яё]+\\s){2}[А-ЯЁ][а-яё]+(\\s[А-ЯЁ][а-яё]+)?,\\s((\\d{4})-([0][1-9]|[1][012])-(0[1-9]|[12][0-9]|3[01]))$");
            Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                // Если данные введены правильно, добавляем их в базу данных
                String[] splitText = text.split(", ");
                String fullName = splitText[0];
                String birthDate = splitText[1];
                boolean est = false;
                TestConnection testConnection = new TestConnection();
                try {
                    est = testConnection.setKlient(phoneNumber,fullName,birthDate);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String userdd = String.valueOf(update.getMessage().getChatId());
                chatUserDataMap.remove(userdd);
                if(est) {
                    this.countMes = true;
                    this.vari = 0;
                    sendMsg(message, "Данные успешно добавлены в базу данных!");
                }
                else {
                    this.countMes = true;
                    sendMsg(message, "Вы уже зарегистрированы!");
                    this.idklient = phoneNumber;
                    this.vari = 0;
                }
            } else {
                // Если данные введены неправильно, отправляем сообщение с просьбой повторить ввод
                sendMsg(message, "Некорректный ввод! Введите ФИО и дату рождения в формате: Иванов Иван Иванович, гггг-мм-дд");
                vari = 99;
            }
        }
    }
    public void numberphone(Message message){
            // Создаем объект клавиатуры и настраиваем его
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);

// Создаем кнопку для запроса номера телефона
            KeyboardButton requestPhoneButton = new KeyboardButton();
            requestPhoneButton.setText("Отправить номер телефона");
            requestPhoneButton.setRequestContact(true);

// Добавляем кнопку на нашу клавиатуру
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow row = new KeyboardRow();
            row.add(requestPhoneButton);
            keyboard.add(row);
            replyKeyboardMarkup.setKeyboard(keyboard);

            SendMessage sendmessage = new SendMessage();
            sendmessage.enableMarkdown(true);
            sendmessage.setChatId(message.getChatId().toString());
            sendmessage.setReplyToMessageId(message.getMessageId());
            sendmessage.setText("Здравствуйте! Это бот для записи на прием. Нажмите на кнопку ниже, чтобы отправить свой номер телефона");
            sendmessage.setReplyMarkup(replyKeyboardMarkup);
            try {
                    execute(sendmessage);
            } catch (TelegramApiException e) {
                    e.printStackTrace();
            }
    }
}
